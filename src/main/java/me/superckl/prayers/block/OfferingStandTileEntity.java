package me.superckl.prayers.block;

import java.util.Random;

import lombok.Getter;
import me.superckl.prayers.AltarItem;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.inventory.InteractableInventoryTileEntity;
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
public class OfferingStandTileEntity extends InteractableInventoryTileEntity implements ITickableTileEntity{

	public static String OFFERING_STAND_KEY = "offering_stand_data";
	public static String ITEM_KEY = "item";
	public static String ITEM_PROGRESS_KEY = "item_progress";

	private final Random rand = new Random();

	private int itemTicks;
	private int reqTicks;

	public OfferingStandTileEntity() {
		super(ModTiles.OFFERING_STAND.get(), 1);
	}

	@Override
	public void tick() {
		if(this.level.isClientSide || this.getItem(0).isEmpty())
			return;
		this.findValidAltar().ifPresent(altar -> {
			if(++this.itemTicks >= this.reqTicks) {
				final AltarItem aItem = AltarItem.find(this.getItem(0));
				if(aItem.getOfferPoints() <= altar.getMaxPoints()-altar.getCurrentPoints() || altar.getCurrentPoints() == 0) {
					altar.addPoints(aItem.getOfferPoints());
					this.removeItem(0, 1);
					this.itemTicks = 0;
					((ServerWorld)this.level).sendParticles(ParticleTypes.SMOKE, this.worldPosition.getX()+0.5, this.worldPosition.getY()+7F/16F, this.worldPosition.getZ()+0.5, 1+this.rand.nextInt(2), 0, 0, 0, 0);
					((ServerWorld)this.level).playSound(null, this.worldPosition.getX()+0.5, this.worldPosition.getY()+1, this.worldPosition.getZ()+0.5, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.01F, 1.2F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
				}
			}
		});
	}

	public ActionResultType onActivateBy(final PlayerEntity player, final Hand hand) {
		return this.onInteract(player, hand, 0);
	}

	@Override
	public void onSlotChange(final int slot, final boolean itemChanged) {
		if(itemChanged) {
			this.itemTicks = 0;
			this.reqTicks = this.getItem(0).isEmpty() ? 0:AltarItem.find(this.getItem(0)).getOfferTicks();
		}
		super.onSlotChange(slot, itemChanged);
	}

	@Override
	public boolean canPlaceItem(final int index, final ItemStack stack) {
		return AltarItem.find(stack) != null;
	}

	protected LazyOptional<AltarTileEntity> findValidAltar() {
		final TileEntity below = this.level.getBlockEntity(this.worldPosition.below());
		if(below != null && below instanceof AltarTileEntity) {
			final AltarTileEntity altar = (AltarTileEntity) below;
			return altar.canRegen() ? LazyOptional.of(() -> altar):LazyOptional.empty();
		}else
			return LazyOptional.empty();
	}

	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		final CompoundNBT offer_data = new CompoundNBT();
		this.writeInventory(offer_data);
		if(!this.getItem(0).isEmpty())
			offer_data.putInt(OfferingStandTileEntity.ITEM_PROGRESS_KEY, this.itemTicks);
		compound.put(OfferingStandTileEntity.OFFERING_STAND_KEY, offer_data);
		return super.save(compound);
	}

	@Override
	public void load(final BlockState state, final CompoundNBT nbt) {
		final CompoundNBT offer_data = nbt.getCompound(OfferingStandTileEntity.OFFERING_STAND_KEY);
		this.readInventory(offer_data);
		if(!this.getItem(0).isEmpty()) {
			this.itemTicks = offer_data.getInt(OfferingStandTileEntity.ITEM_PROGRESS_KEY);
			this.reqTicks = AltarItem.find(this.getItem(0)).getOfferTicks();
		}
		super.load(state, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.load(state, tag);
	}

}
