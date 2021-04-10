package me.superckl.prayers.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class PlayerInventoryIterator implements SlotAwareIterator<MainInventorySlotHelper>{

	private final PlayerInvWrapper wrapper;
	private int slot = -1;

	public PlayerInventoryIterator(final PlayerEntity player) {
		this.wrapper = new PlayerInvWrapper(player.inventory);
	}

	@Override
	public boolean hasNext() {
		return this.wrapper.getSlots() > this.slot+1;
	}

	@Override
	public ItemStack next() {
		return this.wrapper.getStackInSlot(++this.slot);
	}

	@Override
	public MainInventorySlotHelper getHelper() {
		return new MainInventorySlotHelper(this.slot);
	}

}
