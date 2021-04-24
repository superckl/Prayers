package me.superckl.prayers.inventory;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class SlotMappedItemHandlerWrapper extends RangedWrapper{

	public SlotMappedItemHandlerWrapper(final IItemHandlerModifiable wrapped, final int slot) {
		super(wrapped, slot, slot+1);
	}

}
