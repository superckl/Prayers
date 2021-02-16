package me.superckl.prayers.block;

import java.util.Random;

import lombok.Getter;
import me.superckl.prayers.AltarItem;
import me.superckl.prayers.init.ModTiles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;

@Getter
public class OfferingStandTileEntity extends TileEntity implements ITickableTileEntity{

	public static String OFFERING_STAND_KEY = "offering_stand_data";
	public static String ITEM_KEY = "item";
	public static String ITEM_PROGRESS_KEY = "item_progress";

	private final Random rand = new Random();

	private ItemStack item = ItemStack.EMPTY;
	private int itemAge;
	private int itemTicks;
	private int reqTicks;

	public OfferingStandTileEntity() {
		super(ModTiles.OFFERING_STAND.get());
	}

	@Override
	public void tick() {
		if(this.item.isEmpty())
			return;
		this.itemAge++;
		this.findValidAltar().ifPresent(altar -> {
			if(++this.itemTicks >= this.reqTicks) {
				final AltarItem aItem = AltarItem.find(this.item);
				if(aItem.getOfferPoints() <= altar.getMaxPoints()-altar.getCurrentPoints() || altar.getCurrentPoints() == 0) {
					altar.addPoints(aItem.getOfferPoints());
					this.decrementItem();
					if(!this.world.isRemote) {
						((ServerWorld)this.world).spawnParticle(ParticleTypes.SMOKE, this.pos.getX()+0.5, this.pos.getY()+7F/16F, this.pos.getZ()+0.5, 1+this.rand.nextInt(2), 0, 0, 0, 0);
						((ServerWorld)this.world).playSound(null, this.pos.getX()+0.5, this.pos.getY()+1, this.pos.getZ()+0.5, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.01F, 1.2F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
					}
				}
			}
			this.markDirty();
		});
	}

	public ActionResultType onActivateBy(final PlayerEntity player, final Hand hand) {
		if(player.isSneaking()) {
			if(!this.item.isEmpty() && player.getHeldItem(hand).isEmpty()) {
				player.addItemStackToInventory(this.item);
				this.clearItem();
				return this.world.isRemote ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
			}
		}else if(this.item.isEmpty()){
			final ItemStack held = player.getHeldItem(hand);
			final AltarItem aItem = AltarItem.find(held);
			if(aItem != null && aItem.canOffer()) {
				this.item = held.copy();
				if(!player.isCreative())
					player.setHeldItem(hand, ItemStack.EMPTY);
				this.itemTicks = 0;
				this.reqTicks = aItem.getOfferTicks();
				this.markDirty();
				return this.world.isRemote ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
			}
		}else {
			final ItemStack held = player.getHeldItem(hand);
			if(held.isItemEqual(this.item) && ItemStack.areItemStackTagsEqual(held, this.item)) {
				final int toAdd = Math.min(this.item.getMaxStackSize()-this.item.getCount(), held.getCount());
				if(toAdd == 0)
					return ActionResultType.PASS;
				this.item.grow(toAdd);
				if(!player.isCreative())
					held.shrink(toAdd);
				this.markDirty();
				return this.world.isRemote ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}

	public void decrementItem() {
		this.item.shrink(1);
		this.itemTicks = 0;
		if(this.item.isEmpty())
			this.clearItem();
		this.markDirty();
	}

	public void clearItem() {
		this.item = ItemStack.EMPTY;
		this.itemTicks = 0;
		this.reqTicks = 0;
		this.itemAge = 0;
		this.markDirty();
	}

	protected LazyOptional<AltarTileEntity> findValidAltar() {
		final TileEntity below = this.world.getTileEntity(this.pos.down());
		if(below != null && below instanceof AltarTileEntity) {
			final AltarTileEntity altar = (AltarTileEntity) below;
			return altar.canRegen() ? LazyOptional.of(() -> altar):LazyOptional.empty();
		}else
			return LazyOptional.empty();
	}

	@Override
	public CompoundNBT write(final CompoundNBT compound) {
		final CompoundNBT offer_data = new CompoundNBT();
		if(!this.item.isEmpty()) {
			offer_data.put(OfferingStandTileEntity.ITEM_KEY, this.item.write(new CompoundNBT()));
			offer_data.putInt(OfferingStandTileEntity.ITEM_PROGRESS_KEY, this.itemTicks);
		}
		compound.put(OfferingStandTileEntity.OFFERING_STAND_KEY, offer_data);
		return super.write(compound);
	}

	@Override
	public void read(final BlockState state, final CompoundNBT nbt) {
		final CompoundNBT offer_data = nbt.getCompound(OfferingStandTileEntity.OFFERING_STAND_KEY);
		if(offer_data.contains(OfferingStandTileEntity.ITEM_KEY)) {
			this.item = ItemStack.read(offer_data.getCompound(OfferingStandTileEntity.ITEM_KEY));
			this.itemTicks = offer_data.getInt(OfferingStandTileEntity.ITEM_PROGRESS_KEY);
			this.reqTicks = AltarItem.find(this.item).getOfferTicks();
		}
		super.read(state, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.read(state, tag);
	}

}
