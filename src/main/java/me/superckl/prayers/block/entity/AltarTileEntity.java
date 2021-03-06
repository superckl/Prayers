package me.superckl.prayers.block.entity;

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
import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarBlock;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.criteria.OwnAltarCriteriaTrigger;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.network.packet.PacketSetAltarItem;
import me.superckl.prayers.network.packet.PacketSetAltarItemTicks;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketSetPrayerPoints;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.util.MathUtil;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

@Getter
public class AltarTileEntity extends TileEntity implements ITickableTileEntity{

	public static String ALTAR_KEY = "altar_data";
	public static String VALID_KEY = "valid_multiblock";
	public static String OWNER_KEY = "owner";
	public static String OWNER_NAME_KEY = "owner_name";
	public static String HAS_OWNER_KEY = "has_owner";
	public static String CONNECTED_KEY = "connected";
	public static String MAX_POINTS_KEY = "max_points";
	public static String CURRENT_POINTS_KEY = "current_points";
	public static String ALTAR_ITEM_KEY = "altar_item";
	public static String DIRECTION_KEY = "item_direction";
	public static String ITEM_PROGRESS_KEY = "item_progress";
	public static String ITEM_OWNER_KEY = "item_owner";

	public static ResourceLocation ALTAR_TOPPING = new ResourceLocation(Prayers.MOD_ID, "altar_topping");

	private final Random rand = new Random();

	private final AltarTypes altarType;
	private boolean validMultiblock = false;
	private UUID owner;
	private String ownerName;
	private Set<BlockPos> connected = ImmutableSet.of();
	private double maxPoints = 0;
	private double currentPoints = 0;

	private ItemStack altarItem = ItemStack.EMPTY;
	private Direction itemDirection;
	private UUID altarItemOwner;
	private int itemTicks;
	private int itemTicks0;
	private int reqTicks;

	public AltarTileEntity(final AltarTypes altarType) {
		super(ModTiles.ALTARS.get(altarType).get());
		this.altarType = altarType;
	}

	@Override
	public void setRemoved() {
		this.invalidateMultiblock(true);
		super.setRemoved();
	}

	public Set<BlockPos> checkMultiblock(final boolean newlyPlaced) {
		final Set<BlockPos> connected = ImmutableSet.copyOf(AltarBlock.findConnected(this.level, this.worldPosition));
		final int numConnected = connected.size();
		if(numConnected == 1) {
			this.connected = connected;
			this.invalidateMultiblock(false);
			if(newlyPlaced){
				this.maxPoints = 0;
				this.currentPoints = 0;
			}
		} else {
			final List<AltarTileEntity> tiles = AltarTileEntity.toAltars(connected, this.level);
			if(numConnected <= this.altarType.getMaxConnected()) {
				final double points = tiles.stream().mapToDouble(te -> te.currentPoints).sum()/numConnected;
				final double maxPoints = this.altarType.getMaxPoints()*MathHelper.sqrt(numConnected);
				tiles.forEach(tile -> {
					tile.validMultiblock = true;
					tile.connected = connected;
					tile.maxPoints = maxPoints/numConnected;
					tile.currentPoints = points;
					tile.setChanged();
				});
			} else {
				tiles.forEach(tile -> {
					tile.connected = connected;
					tile.invalidateMultiblock(false);
					tile.setChanged();
				});
				if(newlyPlaced)
					this.maxPoints = 0;
			}
		}
		this.setChanged();
		return connected;
	}

	public double getMaxPoints() {
		if(!this.validMultiblock)
			return this.maxPoints;
		return this.getConnected().stream().mapToDouble(altar -> altar.maxPoints).sum();
	}

	public double getCurrentPoints() {
		if(!this.validMultiblock)
			return this.currentPoints;
		return this.getConnected().stream().mapToDouble(altar -> altar.currentPoints).sum();
	}

	public List<AltarTileEntity> getConnected(){
		return AltarTileEntity.toAltars(this.connected, this.level);
	}

	public static List<AltarTileEntity> toAltars(final Set<BlockPos> blockPos, final IBlockReader reader){
		return blockPos.stream().map(pos -> (AltarTileEntity) reader.getBlockEntity(pos)).collect(Collectors.toList());
	}

	public double addPoints(final double points) {
		final List<AltarTileEntity> connected = this.getConnected();
		final double toAdd = points/connected.size();
		return connected.stream().mapToDouble(altar -> altar.addPointsInternal(toAdd)).sum();
	}

	public double removePoints(final double points) {
		return -this.addPoints(-points);
	}

	private double addPointsInternal(final double points) {
		final int signal = this.getRedstoneSignal();
		this.currentPoints += points;
		if(this.currentPoints > this.maxPoints) {
			final double diff = this.maxPoints - this.currentPoints;
			this.currentPoints = this.maxPoints;
			return diff;
		}
		if(this.currentPoints < 0) {
			final double diff = this.currentPoints;
			this.currentPoints = 0;
			return points - diff;
		}
		if(signal != this.getRedstoneSignal())
			this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
		return points;
	}

	public int getRedstoneSignal() {
		return (int) (15*this.currentPoints/this.maxPoints);
	}

	private void invalidateMultiblock(final boolean reCheck) {
		this.validMultiblock = false;
		if(reCheck) {
			final Set<BlockPos> connected = Sets.newHashSet(this.connected);
			connected.remove(this.worldPosition);
			final Set<BlockPos> visited = Sets.newHashSet();
			final Iterator<BlockPos> it = connected.iterator();
			while(it.hasNext()) {
				final BlockPos pos = it.next();
				if(visited.contains(pos))
					continue;
				visited.addAll(((AltarTileEntity) this.level.getBlockEntity(pos)).checkMultiblock(false));
			}
		}
		this.setOwner(null, false);
		this.setChanged();
	}

	/**
	 * Sets the owner of this altar
	 * @param owner The UUID of the owner. If this is null, it is treated as a remove owner operation
	 * @param propogate If this tile entity should propagate the new owner to all connected altars
	 * @return If the owner was changed
	 */
	public boolean setOwner(UUID owner, final boolean propogate) {
		if(owner != null && (!this.validMultiblock || this.owner != null))
			return false; //Can't set owner of an invalid multiblock or this altar is valid and already has an owner
		if(this.owner != null && this.owner.equals(owner))
			return false; //This altar is already owned by this person.
		if(!this.level.isClientSide) {
			final AltarsSavedData savedData = AltarsSavedData.get((ServerWorld) this.level);
			if(owner == null && this.owner != null)
				AltarsSavedData.get((ServerWorld) this.level).removeAltar(this.owner);
			else if(savedData.ownsAltar(owner)) {
				//Does this altar already have an owner?
				owner = null;
				for(final AltarTileEntity te:this.getConnected())
					if(te.owner != null) {
						owner = te.owner;
						break;
					}
				if(owner == null)
					return false;
			}
			if(propogate) {
				final UUID toAssign = owner;
				this.getConnected().forEach(tile -> {
					tile.owner = toAssign;
					tile.setChanged();
					tile.syncToClientLight(null);
				});
			}else {
				this.owner = owner;
				this.setChanged();
				this.syncToClientLight(null);
			}
			//Send update packets to client
			if(owner != null) {
				savedData.setAltar(owner, this.worldPosition);
				final PlayerEntity player = this.level.getPlayerByUUID(owner);
				if(player instanceof ServerPlayerEntity)
					OwnAltarCriteriaTrigger.INSTANCE.trigger((ServerPlayerEntity) player);
			}
			return true;
		}
		return false;
	}

	public void setItemTicks(final int ticks) {
		if(this.level.isClientSide) {
			this.itemTicks0 = this.itemTicks;
			this.itemTicks = ticks;
		}
	}

	public double rechargeUser(final PlayerEntity player) {
		if(this.level.isClientSide || !this.canRegen())
			return 0;
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		final double current = user.getCurrentPrayerPoints();
		final double max = user.getMaxPrayerPoints();
		if(current < max) {
			final List<AltarTileEntity> altars = this.getConnected();
			final double altarCharge = this.getCurrentPoints();
			final double recharge = Math.min(altarCharge, max-current);
			altars.forEach(altar -> {
				altar.currentPoints -= recharge/altars.size();
			});
			user.setCurrentPrayerPoints(current+recharge);
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
					PacketSetPrayerPoints.builder().entityID(player.getId()).amount(user.getCurrentPrayerPoints()).build());
			this.setChanged();
			return recharge;
		}
		return 0;
	}

	public void syncToClientLight(final ServerPlayerEntity player) {
		if(player == null)
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NO_RERENDER);
		else
			PacketDistributor.PLAYER.with(() -> player).send(this.getUpdatePacket());
	}

	@Override
	public void tick() {
		this.itemTicks0 = this.itemTicks;
		if(this.level.isClientSide)
			return;
		if(!this.altarItem.isEmpty() && !this.isTopClear(true)) {
			final double x = this.level.random.nextDouble() * 0.5 + 0.25;
			final double y = 1+this.level.random.nextDouble() * 0.1;
			final double z = this.level.random.nextDouble() * 0.5 + 0.25;
			final ItemEntity itemEntity = new ItemEntity(this.level, this.worldPosition.getX() + x, this.worldPosition.getY() + y, this.worldPosition.getZ() + z, this.altarItem);
			itemEntity.setDefaultPickUpDelay();
			this.level.addFreshEntity(itemEntity);
			this.clearItem();
			this.setChanged();
		}
		if (this.canRegen()) {
			if(this.rand.nextDouble() < 0.015F)
				this.spawnActiveParticle();
			if(this.addPointsInternal(this.altarType.getRechargeRate()) > 0)
				this.setChanged();
			this.updateItem();
		}
	}

	public void updateItem() {
		if(this.level.isClientSide || this.altarItem.isEmpty())
			return;
		//Update visuals on client, but only do xp and particles on server.
		if(this.rand.nextFloat() < 0.15F) {
			final PlayerEntity player = this.level.getPlayerByUUID(this.altarItemOwner);
			if(player != null) {
				final Vector3d altarTop = new Vector3d(this.worldPosition.getX()+0.5, this.worldPosition.getY()+1, this.worldPosition.getZ()+0.5)
						.add((2*this.rand.nextDouble()-1)*.15, this.rand.nextDouble()*.05, (2*this.rand.nextDouble()-1)*.15);
				Vector3d toPlayer = player.position().add(0, player.getEyeHeight()-0.35, 0).subtract(altarTop);
				final double sqrMag = toPlayer.lengthSqr();
				if(sqrMag <= 9) {
					final double mag = Math.sqrt(sqrMag);
					toPlayer = toPlayer.scale(1/mag);
					((ServerWorld)this.level).sendParticles(ModParticles.ITEM_SACRIFICE.get(), altarTop.x, altarTop.y, altarTop.z, 0, toPlayer.x, toPlayer.y, toPlayer.z, mag/20);
				}
			}
		}
		if(++this.itemTicks >= this.reqTicks) {
			final PlayerEntity player = this.level.getPlayerByUUID(this.altarItemOwner);
			final AltarItem aItem = AltarItem.find(this.altarItem);
			final float xp = aItem.getSacrificeXP()*this.altarItem.getCount();
			if(player == null)
				AltarsSavedData.get((ServerWorld) this.level).addPendingXP(this.altarItemOwner, xp);
			else {
				CapabilityHandler.getPrayerCapability(player).giveXP(xp);
				PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), PacketSyncPrayerUser.from(player));
			}
			((ServerWorld)this.level).sendParticles(ParticleTypes.SMOKE, this.worldPosition.getX()+0.5, this.worldPosition.getY()+1, this.worldPosition.getZ()+0.5, 1+this.rand.nextInt(2), 0, 0, 0, 0);
			((ServerWorld)this.level).playSound(null, this.worldPosition.getX()+0.5, this.worldPosition.getY()+1, this.worldPosition.getZ()+0.5, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.02F, 1.2F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
			this.clearItem();
		}
		//Unfortunately, this has to be done every tick for smooth rendering
		//However, it's only synced when there is an item being sacrificed
		this.syncTicks();
		this.setChanged();
	}

	public void clearItem() {
		this.altarItem = ItemStack.EMPTY;
		this.itemDirection = null;
		this.altarItemOwner = null;
		this.reqTicks = 0;
		this.itemTicks = 0;
		this.setChanged();
		this.syncItem();
	}

	public void spawnActiveParticle() {
		if(!this.isTopClear(false))
			return;
		int clearance = 1;
		final BlockPos pos = this.worldPosition.offset(0, 2, 0);
		while (clearance < 3) {
			final BlockState state = this.level.getBlockState(pos);
			if(!state.getBlock().isAir(state, this.level, pos))
				break;
			clearance++;
		}
		final double yAdj = 1.5+this.rand.nextDouble();
		if(yAdj >= clearance)
			return;

		final double posX = this.worldPosition.getX()+this.rand.nextDouble();
		final double posY = this.worldPosition.getY()+yAdj;
		final double posZ = this.worldPosition.getZ()+this.rand.nextDouble();
		((ServerWorld)this.level).sendParticles(ModParticles.ALTAR_ACTIVE.get(), posX, posY, posZ, 0, 0, 0, 0, 0);
	}

	public ActionResultType onActivateBy(final PlayerEntity player, final Hand hand) {
		if(this.level.isClientSide)
			return ActionResultType.SUCCESS;
		if(player instanceof ServerPlayerEntity)
			this.getConnected().forEach(altar -> altar.syncToClientLight((ServerPlayerEntity) player));
		if(!CapabilityHandler.getPrayerCapability(player).isUnlocked())
			return ActionResultType.PASS;
		if(player.isCrouching()) {
			if(!this.altarItem.isEmpty() && player.getItemInHand(hand).isEmpty()) {
				player.addItem(this.altarItem);
				this.clearItem();
				return ActionResultType.CONSUME;
			}
		}else if(this.altarItem.isEmpty() && this.isTopClear(true)){
			final ItemStack toPlace = player.getItemInHand(hand).copy();
			final int confMaxStack = Config.getInstance().getMaxSacrificeStack().get(this.altarType).get();
			final int maxStack = confMaxStack == 0 ? toPlace.getMaxStackSize():confMaxStack;
			if(maxStack < toPlace.getCount())
				toPlace.setCount(maxStack);
			if(this.setItem(toPlace, player.getUUID(), Direction.fromYRot(player.yHeadRot))) {
				if(!player.isCreative())
					player.setItemInHand(hand, ItemStack.EMPTY);
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}

	//We also require the direction here because the owner may not be online or "visible" on the client
	public boolean setItem(final ItemStack item, final UUID owner, final Direction dir) {
		if(item.isEmpty())
			return false;
		final AltarItem aItem = AltarItem.find(item);
		if(aItem == null || !aItem.canSacrifice())
			return false;
		this.altarItem = item;
		this.altarItemOwner = owner;
		this.itemTicks = 0;
		this.reqTicks = aItem.getSacrificeTicks()*item.getCount();
		this.itemDirection = dir;
		this.setChanged();
		this.syncItem();
		return true;
	}

	protected void syncItem() {
		if(this.level.isClientSide)
			return;
		//We don't sync the item owner because the client doesn't care about it. All particles and xp logic are done server-side
		PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
				new PacketSetAltarItem(this.worldPosition, this.altarItem, this.itemDirection));
	}

	protected void syncTicks() {
		if(this.level.isClientSide)
			return;
		//Sync item ticks to all tracking clients for rendering item shrinking
		PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
				new PacketSetAltarItemTicks(this.worldPosition, this.itemTicks));
	}

	public boolean isTopClear(final boolean requireAir) {
		final BlockPos up = this.worldPosition.above();
		final BlockState state = this.level.getBlockState(up);
		final boolean isAir = state.getBlock().isAir(state, this.level, up);
		return !requireAir && state.getBlock().getTags().contains(AltarTileEntity.ALTAR_TOPPING) || isAir;
	}

	public boolean canRegen() {
		return this.validMultiblock && this.owner != null;
	}

	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		final CompoundNBT altarNBT = new CompoundNBT();
		altarNBT.putBoolean(AltarTileEntity.VALID_KEY, this.validMultiblock);
		if(this.owner != null)
			altarNBT.putUUID(AltarTileEntity.OWNER_KEY, this.owner);
		if(this.ownerName != null)
			altarNBT.putString(AltarTileEntity.OWNER_NAME_KEY, this.ownerName);
		altarNBT.putDouble(AltarTileEntity.CURRENT_POINTS_KEY, this.currentPoints);
		altarNBT.putDouble(AltarTileEntity.MAX_POINTS_KEY, this.maxPoints);
		if(!this.altarItem.isEmpty()) {
			altarNBT.put(AltarTileEntity.ALTAR_ITEM_KEY, this.altarItem.save(new CompoundNBT()));
			altarNBT.putInt(AltarTileEntity.ITEM_PROGRESS_KEY, this.itemTicks);
			altarNBT.putUUID(AltarTileEntity.ITEM_OWNER_KEY, this.altarItemOwner);
			altarNBT.putIntArray(AltarTileEntity.DIRECTION_KEY, new int[] {this.itemDirection.getStepX(), this.itemDirection.getStepY(), this.itemDirection.getStepZ()});
		}
		final ListNBT connectedList = new ListNBT();
		MathUtil.toIntList(this.connected).forEach(connected -> connectedList.add(new IntArrayNBT(connected)));
		altarNBT.put(AltarTileEntity.CONNECTED_KEY, connectedList);
		compound.put(AltarTileEntity.ALTAR_KEY, altarNBT);
		return super.save(compound);
	}

	@Override
	public void load(final BlockState state, final CompoundNBT nbt) {
		super.load(state, nbt);
		final CompoundNBT altarNBT = nbt.getCompound(AltarTileEntity.ALTAR_KEY);
		this.validMultiblock = altarNBT.getBoolean(AltarTileEntity.VALID_KEY);
		this.maxPoints = altarNBT.getDouble(AltarTileEntity.MAX_POINTS_KEY);
		this.currentPoints = altarNBT.getDouble(AltarTileEntity.CURRENT_POINTS_KEY);
		if(altarNBT.contains(AltarTileEntity.ALTAR_ITEM_KEY)) {
			this.altarItem = ItemStack.of(altarNBT.getCompound(AltarTileEntity.ALTAR_ITEM_KEY));
			final int[] dirs = altarNBT.getIntArray(AltarTileEntity.DIRECTION_KEY);
			this.itemDirection = Direction.getNearest(dirs[0], dirs[1], dirs[2]);
			this.itemTicks = altarNBT.getInt(AltarTileEntity.ITEM_PROGRESS_KEY);
			this.itemTicks0 = this.itemTicks;
			this.altarItemOwner = altarNBT.getUUID(AltarTileEntity.ITEM_OWNER_KEY);
			this.reqTicks = AltarItem.find(this.altarItem).getSacrificeTicks()*this.altarItem.getCount();
		}
		if(altarNBT.contains(AltarTileEntity.OWNER_KEY))
			this.owner = altarNBT.getUUID(AltarTileEntity.OWNER_KEY);
		if(altarNBT.contains(AltarTileEntity.OWNER_NAME_KEY))
			this.ownerName = altarNBT.getString(AltarTileEntity.OWNER_NAME_KEY);
		final ListNBT connectedList = altarNBT.getList(AltarTileEntity.CONNECTED_KEY, Constants.NBT.TAG_INT_ARRAY);
		final Set<BlockPos> connected = Sets.newHashSet();
		connectedList.stream().map(inbt -> ((IntArrayNBT) inbt).getAsIntArray()).forEach(array -> connected.add(new BlockPos(array[0], array[1], array[2])));
		this.connected = ImmutableSet.copyOf(connected);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.load(state, tag);
	}

	//These methods are called on each altar that needs to be updated, so read and modify fields directly
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		if(this.level.isClientSide)
			return null;
		final CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean(AltarTileEntity.HAS_OWNER_KEY, this.owner != null);
		if(this.owner != null)
			nbt.putString(AltarTileEntity.OWNER_NAME_KEY, UsernameCache.containsUUID(this.owner) ? UsernameCache.getLastKnownUsername(this.owner):"Unknown");
		nbt.putDouble(AltarTileEntity.CURRENT_POINTS_KEY, this.currentPoints);
		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		if(!this.level.isClientSide)
			return;
		final CompoundNBT nbt = pkt.getTag();
		if(nbt.getBoolean(AltarTileEntity.HAS_OWNER_KEY))
			this.ownerName = nbt.getString(AltarTileEntity.OWNER_NAME_KEY);
		else
			this.ownerName = null;
		this.currentPoints = nbt.getDouble(AltarTileEntity.CURRENT_POINTS_KEY);
	}

}
