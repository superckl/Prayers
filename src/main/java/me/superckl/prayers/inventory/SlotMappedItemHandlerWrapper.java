package me.superckl.prayers.inventory;

import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

@RequiredArgsConstructor
public class SlotMappedItemHandlerWrapper implements IItemHandlerModifiable{

	protected final IItemHandlerModifiable handler;
	protected final int slot;

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		this.validateSlotIndex(slot);
		return this.handler.getStackInSlot(this.slot);
	}

	@Override
	public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
		this.validateSlotIndex(slot);
		return this.handler.insertItem(this.slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
		this.validateSlotIndex(slot);
		return this.handler.extractItem(this.slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(final int slot) {
		this.validateSlotIndex(slot);
		return this.handler.getSlotLimit(this.slot);
	}

	@Override
	public boolean isItemValid(final int slot, final ItemStack stack) {
		this.validateSlotIndex(slot);
		return this.handler.isItemValid(this.slot, stack);
	}

	@Override
	public void setStackInSlot(final int slot, final ItemStack stack) {
		this.validateSlotIndex(slot);
		this.handler.setStackInSlot(this.slot, stack);
	}

	protected void validateSlotIndex(final int slot) {
		if (this.getSlots() <=  slot)
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
	}

}
