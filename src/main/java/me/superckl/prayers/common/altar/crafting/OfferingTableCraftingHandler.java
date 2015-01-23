package me.superckl.prayers.common.altar.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.item.ItemStack;

public abstract class OfferingTableCraftingHandler implements Cloneable{

	public abstract ItemStack getResult();
	public abstract ItemStack getBaseIngredient();
	public abstract List<ItemStack> getTertiaryIngredients();
	public abstract boolean areAdditionalRequirementsMet(final TileEntityOfferingTable te);
	public abstract void beginCrafting(final TileEntityOfferingTable te);
	public abstract void handleUpdate(final TileEntityOfferingTable te);
	public abstract boolean isCrafting(final TileEntityOfferingTable te);
	public abstract boolean isComplete(final TileEntityOfferingTable te);
	@Override
	public abstract OfferingTableCraftingHandler clone();

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
				if(stack.isItemEqual(tert)){
					it.remove();
					break;
				}
		}
		return list.isEmpty();
	}

}
