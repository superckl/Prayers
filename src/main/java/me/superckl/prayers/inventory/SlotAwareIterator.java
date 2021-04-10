package me.superckl.prayers.inventory;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

public interface SlotAwareIterator<T extends SlotHelper> extends Iterator<ItemStack>{

	/*
	 * Returns a SlotHelper for the current element
	 */
	T getHelper();

	public static class Combined implements SlotAwareIterator<SlotHelper>{

		private final SlotAwareIterator<?>[] iterators;
		private int currentIt = 0;

		public Combined(final SlotAwareIterator<?>... iterators) {
			this.iterators = iterators;
		}

		@Override
		public boolean hasNext() {
			return this.iterators.length > this.currentIt && this.iterators[this.currentIt].hasNext() ||
					this.iterators.length > this.currentIt+1 && this.iterators[this.currentIt+1].hasNext();
		}

		@Override
		public ItemStack next() {
			if(!this.hasNext())
				throw new IllegalStateException("No more elements!");
			if(this.iterators.length > this.currentIt && this.iterators[this.currentIt].hasNext())
				return this.iterators[this.currentIt].next();
			return this.iterators[++this.currentIt].next();
		}

		@Override
		public SlotHelper getHelper() {
			return this.iterators[this.currentIt].getHelper();
		}

	}

}
