package me.superckl.prayers.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.Getter;
import me.superckl.prayers.AltarItem;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.capability.IPrayerUser;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.util.MathUtil;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

@Getter
public class AltarTileEntity extends TileEntity implements ITickableTileEntity{

	public static String ALTAR_KEY = "altar_data";
	public static String VALID_KEY = "valid_multiblock";
	public static String OWNER_KEY = "owner";
	public static String HAS_OWNER_KEY = "has_owner";
	public static String CONNECTED_KEY = "connected";
	public static String MAX_POINTS_KEY = "max_points";
	public static String CURRENT_POINTS_KEY = "current_points";
	public static String ALTAR_ITEM_KEY = "altar_item";
	public static String DIRECTION_KEY = "item_direction";
	public static String ITEM_PROGRESS_KEY = "item_progress";
	public static String ITEM_OWNER_KEY = "item_owner";

	private final Random rand = new Random();

	private final AltarTypes altarType;
	private boolean validMultiblock = false;
	private UUID owner;
	private Set<BlockPos> connected = ImmutableSet.of();
	private float maxPoints = 0;
	private float currentPoints = 0;

	private ItemStack altarItem = ItemStack.EMPTY;
	private Direction itemDirection;
	private UUID altarItemOwner;
	private int itemTicks;
	private int reqTicks;

	public AltarTileEntity(final AltarTypes altarType) {
		super(ModTiles.ALTARS.get(altarType).get());
		this.altarType = altarType;
	}

	@Override
	public void remove() {
		this.invalidateMultiblock(true);
		super.remove();
	}

	public Set<BlockPos> checkMultiblock(final boolean newlyPlaced) {
		final Set<BlockPos> connected = ImmutableSet.copyOf(AltarBlock.findConnected(this.world, this.pos));
		final int numConnected = connected.size();
		if(numConnected == 1) {
			this.connected = connected;
			this.invalidateMultiblock(false);
			if(newlyPlaced){
				this.maxPoints = 0;
				this.currentPoints = 0;
			}
		} else {
			final List<AltarTileEntity> tiles = AltarTileEntity.toAltars(connected, this.world);
			if(numConnected <= this.altarType.getMaxConnected()) {
				final float points = (float) (tiles.stream().mapToDouble(AltarTileEntity::getCurrentPoints).sum()/numConnected);
				final float maxPoints = this.altarType.getMaxPoints()*numConnected; //TODO diminishing returns
				tiles.forEach(tile -> {
					tile.validMultiblock = true;
					tile.connected = connected;
					tile.maxPoints = maxPoints/numConnected;
					tile.currentPoints = points;
					tile.markDirty();
				});
			} else {
				tiles.forEach(tile -> {
					tile.connected = connected;
					tile.invalidateMultiblock(false);
					tile.markDirty();
				});
				if(newlyPlaced)
					this.maxPoints = 0;
			}
		}
		this.markDirty();
		return connected;
	}

	public List<AltarTileEntity> getConnected(){
		return AltarTileEntity.toAltars(this.connected, this.world);
	}

	public static List<AltarTileEntity> toAltars(final Set<BlockPos> blockPos, final IBlockReader reader){
		return blockPos.stream().map(pos -> (AltarTileEntity) reader.getTileEntity(pos)).collect(Collectors.toList());
	}

	public float addPoints(final float points) {
		final List<AltarTileEntity> connected = this.getConnected();
		final float toAdd = points/connected.size();
		return (float) connected.stream().mapToDouble(altar -> altar.addPointsInternal(toAdd)).sum();
	}

	private float addPointsInternal(final float points) {
		if(this.currentPoints >= this.maxPoints)
			return 0;
		this.currentPoints += points;
		if(this.currentPoints > this.maxPoints) {
			final float diff = this.maxPoints - this.currentPoints;
			this.currentPoints = this.maxPoints;
			return diff;
		}
		return points;
	}

	private void invalidateMultiblock(final boolean reCheck) {
		this.validMultiblock = false;
		if(reCheck) {
			final Set<BlockPos> connected = Sets.newHashSet(this.connected);
			connected.remove(this.pos);
			final Set<BlockPos> visited = Sets.newHashSet();
			final Iterator<BlockPos> it = connected.iterator();
			while(it.hasNext()) {
				final BlockPos pos = it.next();
				if(visited.contains(pos))
					continue;
				visited.addAll(((AltarTileEntity) this.world.getTileEntity(pos)).checkMultiblock(false));
			}
		}
		this.setOwner(null, false);
		this.markDirty();
	}

	/**
	 * Sets the owner of this altar
	 * @param owner The UUID of the owner. If this is null, it is treated as a remove owner operation
	 * @param propogate If this tile entity should propagate the new owner to all connected altars
	 * @return If the owner was changed
	 */
	public boolean setOwner(final UUID owner, final boolean propogate) {
		if(owner != null && (!this.validMultiblock || this.owner != null && owner.equals(this.owner)))
			return false;
		if(!this.world.isRemote) {
			final AltarsSavedData savedData = AltarsSavedData.get((ServerWorld) this.world);
			if(owner == null && this.owner != null)
				AltarsSavedData.get((ServerWorld) this.world).removeAltar(this.owner);
			else if(savedData.ownsAltar(owner))
				return false;
			if(propogate)
				this.getConnected().forEach(tile -> {
					tile.owner = owner;
					tile.markDirty();
					tile.syncToClientLight();
				});
			else {
				this.owner = owner;
				this.markDirty();
				this.syncToClientLight();
			}
			//Send update packets to client
			if(owner != null)
				savedData.setAltar(owner, this.pos);
			return true;
		}
		return false;
	}

	public float rechargeUser(final IPrayerUser user) {
		if(!this.canRegen())
			return 0;
		final float current = user.getCurrentPrayerPoints();
		final float max = user.getMaxPrayerPoints();
		if(current < max) {
			final List<AltarTileEntity> altars = this.getConnected();
			final float altarCharge = (float) altars.stream().mapToDouble(AltarTileEntity::getCurrentPoints).sum();
			final float recharge = Math.min(altarCharge, max-current);
			altars.forEach(altar -> {
				altar.currentPoints -= recharge/altars.size();
				altar.syncToClientLight(); //We can't use block events for this because it's a float
			});
			user.setCurrentPrayerPoints(current+recharge);
			this.markDirty();
			return recharge;
		}
		return 0;
	}

	public void syncToClientLight() {
		if(!this.world.isRemote)
			this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NO_RERENDER);
	}

	@Override
	public void tick() {
		if(!this.altarItem.isEmpty() && !this.isTopClear(true)) {
			if(!this.world.isRemote) {
				final double x = this.world.rand.nextFloat() * 0.5 + 0.25;
				final double y = 1+this.world.rand.nextFloat() * 0.1;
				final double z = this.world.rand.nextFloat() * 0.5 + 0.25;
				final ItemEntity itemEntity = new ItemEntity(this.world, this.pos.getX() + x, this.pos.getY() + y, this.pos.getZ() + z, this.altarItem);
				itemEntity.setDefaultPickupDelay();
				this.world.addEntity(itemEntity);
			}
			this.clearItem();
			this.markDirty();
		}
		if (this.canRegen()) {
			if(this.rand.nextFloat() < 0.015F)
				this.spawnActiveParticle();
			if(this.currentPoints < this.maxPoints) {
				this.currentPoints += this.maxPoints*this.altarType.getRechargeRate();
				if(this.currentPoints > this.maxPoints)
					this.currentPoints = this.maxPoints;
				this.markDirty();
			}
			this.updateItem();
		}
	}

	public void updateItem() {
		if(this.altarItem.isEmpty())
			return;
		if(!this.world.isRemote && this.rand.nextFloat() < 0.15F) {
			final PlayerEntity player = this.world.getPlayerByUuid(this.altarItemOwner);
			if(player != null) {
				final Vector3d altarTop = new Vector3d(this.pos.getX()+0.5, this.pos.getY()+1, this.pos.getZ()+0.5)
						.add((2*this.rand.nextDouble()-1)*.15, this.rand.nextDouble()*.05, (2*this.rand.nextDouble()-1)*.15);
				Vector3d toPlayer = player.getPositionVec().add(0, player.getEyeHeight()-0.35, 0).subtract(altarTop);
				final double mag = toPlayer.length();
				toPlayer = toPlayer.scale(1/mag);
				((ServerWorld)this.world).spawnParticle(ModParticles.ITEM_SACRIFICE.get(), altarTop.x, altarTop.y, altarTop.z, 0, toPlayer.x, toPlayer.y, toPlayer.z, mag/20);
			}
		}
		if(++this.itemTicks >= this.reqTicks) {
			final PlayerEntity player = this.world.getPlayerByUuid(this.altarItemOwner);
			final AltarItem aItem = AltarItem.find(this.altarItem);
			if(player == null)
				AltarsSavedData.get((ServerWorld) this.world).addPendingXP(this.altarItemOwner, aItem.getSacrificeXP());
			else
				IPrayerUser.getUser(player).giveXP(aItem.getSacrificeXP());
			this.clearItem();
			if(!this.world.isRemote) {
				((ServerWorld)this.world).spawnParticle(ParticleTypes.SMOKE, this.pos.getX()+0.5, this.pos.getY()+1, this.pos.getZ()+0.5, 1+this.rand.nextInt(2), 0, 0, 0, 0);
				((ServerWorld)this.world).playSound(null, this.pos.getX()+0.5, this.pos.getY()+1, this.pos.getZ()+0.5, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.02F, 1.2F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
			}
		}
		this.markDirty();
	}

	public void clearItem() {
		this.altarItem = ItemStack.EMPTY;
		this.itemDirection = null;
		this.altarItemOwner = null;
		this.reqTicks = 0;
		this.itemTicks = 0;
		this.markDirty();
	}

	public void spawnActiveParticle() {
		if(this.world.isRemote || !this.isTopClear(false))
			return;
		int clearance = 1;
		final BlockPos pos = this.pos.add(0, 2, 0);
		while (clearance < 3) {
			final BlockState state = this.world.getBlockState(pos);
			if(!state.getBlock().isAir(state, this.world, pos))
				break;
			clearance++;
		}
		final double yAdj = 1.5+this.rand.nextDouble();
		if(yAdj >= clearance)
			return;

		final double posX = this.pos.getX()+this.rand.nextDouble();
		final double posY = this.pos.getY()+yAdj;
		final double posZ = this.pos.getZ()+this.rand.nextDouble();
		((ServerWorld)this.world).spawnParticle(ModParticles.ALTAR_ACTIVE.get(), posX, posY, posZ, 0, 0, 0, 0, 0);
	}

	public ActionResultType onActivateBy(final PlayerEntity player, final Hand hand) {
		if(player.isSneaking()) {
			if(!this.altarItem.isEmpty() && player.getHeldItem(hand).isEmpty()) {
				player.addItemStackToInventory(this.altarItem);
				this.clearItem();
				return ActionResultType.SUCCESS;
			}
		}else if(this.altarItem.isEmpty() && this.isTopClear(true)){
			final ItemStack held = player.getHeldItem(hand);
			final AltarItem aItem = AltarItem.find(held);
			if(aItem != null && aItem.canSacrifice()) {
				this.altarItem = held.copy();
				this.altarItem.setCount(1);
				if(!player.isCreative())
					held.shrink(1);
				this.altarItemOwner = player.getUniqueID();
				this.itemTicks = 0;
				this.reqTicks = aItem.getSacrificeTicks();
				this.itemDirection = Direction.getFacingFromVector(this.pos.getX()-player.getPosX(),
						this.pos.getY()-player.getPosY(), this.pos.getZ()-player.getPosZ());
				this.markDirty();
				return ActionResultType.SUCCESS;
			}else
				return ActionResultType.FAIL;
		}
		return ActionResultType.PASS;
	}

	public boolean isTopClear(final boolean requireAir) {
		final BlockPos up = this.pos.up();
		final BlockState state = this.world.getBlockState(up);
		return requireAir ? state.getBlock().isAir(state, this.world, up):AltarBlock.validTopBlocks.contains(state.getBlock().delegate.name());
	}

	public boolean canRegen() {
		return this.validMultiblock && this.owner != null;
	}

	@Override
	public CompoundNBT write(final CompoundNBT compound) {
		final CompoundNBT altarNBT = new CompoundNBT();
		altarNBT.putBoolean(AltarTileEntity.VALID_KEY, this.validMultiblock);
		if(this.owner != null)
			altarNBT.putUniqueId(AltarTileEntity.OWNER_KEY, this.owner);
		altarNBT.putFloat(AltarTileEntity.CURRENT_POINTS_KEY, this.currentPoints);
		altarNBT.putFloat(AltarTileEntity.MAX_POINTS_KEY, this.maxPoints);
		if(!this.altarItem.isEmpty()) {
			altarNBT.put(AltarTileEntity.ALTAR_ITEM_KEY, this.altarItem.write(new CompoundNBT()));
			altarNBT.putInt(AltarTileEntity.ITEM_PROGRESS_KEY, this.itemTicks);
			altarNBT.putUniqueId(AltarTileEntity.ITEM_OWNER_KEY, this.altarItemOwner);
			altarNBT.putIntArray(AltarTileEntity.DIRECTION_KEY, new int[] {this.itemDirection.getXOffset(), this.itemDirection.getYOffset(), this.itemDirection.getZOffset()});
		}
		final ListNBT connectedList = new ListNBT();
		MathUtil.toIntList(this.connected).forEach(connected -> connectedList.add(new IntArrayNBT(connected)));
		altarNBT.put(AltarTileEntity.CONNECTED_KEY, connectedList);
		compound.put(AltarTileEntity.ALTAR_KEY, altarNBT);
		return super.write(compound);
	}

	@Override
	public void read(final BlockState state, final CompoundNBT nbt) {
		super.read(state, nbt);
		final CompoundNBT altarNBT = nbt.getCompound(AltarTileEntity.ALTAR_KEY);
		this.validMultiblock = altarNBT.getBoolean(AltarTileEntity.VALID_KEY);
		this.maxPoints = altarNBT.getFloat(AltarTileEntity.MAX_POINTS_KEY);
		this.currentPoints = altarNBT.getFloat(AltarTileEntity.CURRENT_POINTS_KEY);
		if(altarNBT.contains(AltarTileEntity.ALTAR_ITEM_KEY)) {
			this.altarItem = ItemStack.read(altarNBT.getCompound(AltarTileEntity.ALTAR_ITEM_KEY));
			final int[] dirs = altarNBT.getIntArray(AltarTileEntity.DIRECTION_KEY);
			this.itemDirection = Direction.getFacingFromVector(dirs[0], dirs[1], dirs[2]);
			this.itemTicks = altarNBT.getInt(AltarTileEntity.ITEM_PROGRESS_KEY);
			this.altarItemOwner = altarNBT.getUniqueId(AltarTileEntity.ITEM_OWNER_KEY);
			this.reqTicks = AltarItem.find(this.altarItem).getSacrificeTicks();
		}
		if(altarNBT.contains(AltarTileEntity.OWNER_KEY))
			this.owner = altarNBT.getUniqueId(AltarTileEntity.OWNER_KEY);
		final ListNBT connectedList = altarNBT.getList(AltarTileEntity.CONNECTED_KEY, Constants.NBT.TAG_INT_ARRAY);
		final Set<BlockPos> connected = Sets.newHashSet();
		connectedList.stream().map(inbt -> ((IntArrayNBT) inbt).getIntArray()).forEach(array -> connected.add(new BlockPos(array[0], array[1], array[2])));
		this.connected = ImmutableSet.copyOf(connected);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.read(state, tag);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		if(this.world.isRemote)
			return null;
		final CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean(AltarTileEntity.HAS_OWNER_KEY, this.owner != null);
		if(this.owner!= null)
			nbt.putUniqueId(AltarTileEntity.OWNER_KEY, this.owner);
		nbt.putFloat(AltarTileEntity.CURRENT_POINTS_KEY, this.currentPoints);
		return new SUpdateTileEntityPacket(this.pos, -1, nbt);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		if(!this.world.isRemote)
			return;
		final CompoundNBT nbt = pkt.getNbtCompound();
		if(nbt.getBoolean(AltarTileEntity.HAS_OWNER_KEY))
			this.owner = nbt.getUniqueId(AltarTileEntity.OWNER_KEY);
		else
			this.owner = null;
		this.currentPoints = nbt.getFloat(AltarTileEntity.CURRENT_POINTS_KEY);
	}

}
