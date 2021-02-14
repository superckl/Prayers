package me.superckl.prayers.block;

import me.superckl.prayers.AltarItem;
import me.superckl.prayers.init.ModTiles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.LazyOptional;

public class OfferingStandTileEntity extends TileEntity implements ITickableTileEntity{

	public static String OFFERING_STAND_KEY = "offering_stand_data";
	public static String ITEM_KEY = "item";
	public static String ITEM_PROGRESS_KEY = "item_progress";

	private ItemStack item = ItemStack.EMPTY;
	private int itemTicks;
	private int reqTicks;

	public OfferingStandTileEntity() {
		super(ModTiles.OFFERING_STAND.get());
	}

	@Override
	public void tick() {
		if(this.item.isEmpty())
			return;
		this.findValidAltar().ifPresent(altar -> {
			if(++this.itemTicks >= this.reqTicks) {
				final AltarItem aItem = AltarItem.find(this.item);
				if(aItem.getOfferPoints() <= altar.getMaxPoints()-altar.getCurrentPoints() || altar.getCurrentPoints() == 0) {
					altar.addPoints(aItem.getOfferPoints());
					this.decrementItem();
					//TODO particle effect
				} else
					this.itemTicks--;
			}
			this.markDirty();
		});
	}

	public ActionResultType onActivateBy(final PlayerEntity player, final Hand hand) {
		if(player.isSneaking()) {
			if(!this.item.isEmpty() && player.getHeldItem(hand).isEmpty()) {
				player.addItemStackToInventory(this.item);
				this.clearItem();
				return ActionResultType.SUCCESS;
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
				return ActionResultType.SUCCESS;
			}else
				return ActionResultType.FAIL;
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

}
