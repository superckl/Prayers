package me.superckl.prayers.integration.curios;

import java.util.Iterator;
import java.util.Map.Entry;

import me.superckl.prayers.inventory.SlotAwareIterator;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CurioSlotAwareIterator implements SlotAwareIterator<CurioSlotHelper>{

	private final Iterator<Entry<String, ICurioStacksHandler>> curiosIt;
	private String currentId;
	private IDynamicStackHandler currentStackHandler;
	private int currentSlot = -1;

	public CurioSlotAwareIterator(final ICuriosItemHandler handler) {
		this.curiosIt = handler.getCurios().entrySet().iterator();
		if(this.curiosIt.hasNext()) {
			final Entry<String, ICurioStacksHandler> next = this.curiosIt.next();
			this.currentId = next.getKey();
			this.currentStackHandler = next.getValue().getStacks();
		}
	}

	@Override
	public boolean hasNext() {
		if(this.currentId == null || this.currentStackHandler == null || this.currentSlot >= this.currentStackHandler.getSlots()-1)
			return this.curiosIt.hasNext();
		return true;
	}

	@Override
	public ItemStack next() {
		if(!this.hasNext())
			throw new IllegalStateException("No more elements!");
		if(this.currentId == null || this.currentStackHandler == null || this.currentSlot >= this.currentStackHandler.getSlots()-1) {
			final Entry<String, ICurioStacksHandler> next = this.curiosIt.next();
			this.currentId = next.getKey();
			this.currentStackHandler = next.getValue().getStacks();
			this.currentSlot = -1;
		}
		return this.currentStackHandler.getStackInSlot(++this.currentSlot);
	}

	@Override
	public CurioSlotHelper getHelper() {
		return new CurioSlotHelper(new SlotContext(this.currentId, null, this.currentSlot));
	}

}
