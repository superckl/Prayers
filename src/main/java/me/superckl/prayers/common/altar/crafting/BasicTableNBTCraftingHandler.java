package me.superckl.prayers.common.altar.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;

public class BasicTableNBTCraftingHandler extends BasicTableCraftingHandler{

	public BasicTableNBTCraftingHandler(final ItemStack result, final ItemStack baseIngredient, final List<ItemStack> tertiaryIngredients, final int length, final float pointDrain) {
		super(result, baseIngredient, tertiaryIngredients, length, pointDrain);
	}

	@Override
	public boolean checkCompletion(final ItemStack base, final List<ItemStack> tertiary){
		if(!base.isItemEqual(this.getBaseIngredient()))
			return false;
		final List<ItemStack> list = new ArrayList<ItemStack>(this.getTertiaryIngredients());
		if(list.size() != tertiary.size())
			return false;
		final Iterator<ItemStack> it = list.iterator();
		while(it.hasNext()){
			final ItemStack stack = it.next();
			for(final ItemStack tert:tertiary)
				if(stack.isItemEqual(tert) && ItemStack.areItemStackTagsEqual(stack, tert)){
					it.remove();
					break;
				}
		}
		return list.isEmpty();
	}

}
