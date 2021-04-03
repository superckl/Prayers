package me.superckl.prayers.block.entity;

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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

@Getter
public class OfferingStandTileEntity extends InteractableInventoryTileEntity implements ITickableTileEntity{

	public static String OFFERING_STAND_KEY = "offering_stand_data";
	public static String ITEM_KEY = "item";
	public static String ITEM_PROGRESS_KEY = "item_progress";

	private final Random rand = new Random();

	private int itemTicks;
	private int reqTicks;
	private AltarItem currentItem;

	private final ItemStackHandler itemHandler = new OfferingStandItemHandler();

	public OfferingStandTileEntity() {
		super(ModTiles.OFFERING_STAND.get());
	}

	@Override
	public void tick() {
		if(this.level.isClientSide || this.itemHandler.getStackInSlot(0).isEmpty())
			return;
		this.findValidAltar().ifPresent(altar -> {
			if(++this.itemTicks >= this.reqTicks) {
				final AltarItem aItem = AltarItem.find(this.itemHandler.getStackInSlot(0));
				if(aItem.getOfferPoints() <= altar.getMaxPoints()-altar.getCurrentPoints() || altar.getCurrentPoints() == 0) {
					altar.addPoints(aItem.getOfferPoints());
					this.itemHandler.extractItem(0, 1, false);
					this.itemTicks = 0;
					((ServerWorld)this.level).sendParticles(ParticleTypes.SMOKE, this.worldPosition.getX()+0.5, this.worldPosition.getY()+7F/16F, this.worldPosition.getZ()+0.5, 1+this.rand.nextInt(2), 0, 0, 0, 0);
					((ServerWorld)this.level).playSound(null, this.worldPosition.getX()+0.5, this.worldPosition.getY()+1, this.worldPosition.getZ()+0.5, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.01F, 1.2F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
				}
			}
		});
	}

	public ActionResultType onActivateBy(final PlayerEntity player, final Hand hand) {
		return this.onInteract(player, hand, this.itemHandler, 0);
	}

	@Override
	public void onSlotChange(final int slot) {
		final AltarItem aItem = AltarItem.find(this.itemHandler.getStackInSlot(0));
		if(aItem != this.currentItem){
			this.itemTicks = 0;
			this.reqTicks = this.itemHandler.getStackInSlot(0).isEmpty() ? 0:AltarItem.find(this.itemHandler.getStackInSlot(0)).getOfferTicks();
			this.currentItem = aItem;
		}
		super.onSlotChange(slot);
	}

	protected LazyOptional<AltarTileEntity> findValidAltar() {
		final TileEntity below = this.level.getBlockEntity(this.worldPosition.below());
		if(below instanceof AltarTileEntity) {
			final AltarTileEntity altar = (AltarTileEntity) below;
			return altar.canRegen() ? LazyOptional.of(() -> altar):LazyOptional.empty();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		final CompoundNBT offer_data = new CompoundNBT();
		offer_data.put(InteractableInventoryTileEntity.INV_KEY, this.itemHandler.serializeNBT());
		if(!this.itemHandler.getStackInSlot(0).isEmpty())
			offer_data.putInt(OfferingStandTileEntity.ITEM_PROGRESS_KEY, this.itemTicks);
		compound.put(OfferingStandTileEntity.OFFERING_STAND_KEY, offer_data);
		return super.save(compound);
	}

	@Override
	public void load(final BlockState state, final CompoundNBT nbt) {
		super.load(state, nbt);
		final CompoundNBT offer_data = nbt.getCompound(OfferingStandTileEntity.OFFERING_STAND_KEY);
		this.itemHandler.deserializeNBT(offer_data.getCompound(InteractableInventoryTileEntity.INV_KEY));
		if(!this.itemHandler.getStackInSlot(0).isEmpty()) {
			this.itemTicks = offer_data.getInt(OfferingStandTileEntity.ITEM_PROGRESS_KEY);
			this.reqTicks = AltarItem.find(this.itemHandler.getStackInSlot(0)).getOfferTicks();
		}
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.load(state, tag);
	}

	@Override
	public IItemHandlerModifiable getInternalItemHandler() {
		return this.itemHandler;
	}

	public class OfferingStandItemHandler extends ItemStackHandler{

		public OfferingStandItemHandler() {
			super(1);
		}

		@Override
		public boolean isItemValid(final int slot, final ItemStack stack) {
			return AltarItem.find(stack) != null;
		}

		@Override
		protected void onContentsChanged(final int slot) {
			OfferingStandTileEntity.this.onSlotChange(slot);
		}

	}

}
