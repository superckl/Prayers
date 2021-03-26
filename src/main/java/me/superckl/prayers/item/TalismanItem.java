package me.superckl.prayers.item;

import me.superckl.prayers.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TalismanItem extends Item{

	public TalismanItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
	}

	public void toggle(final ItemStack stack) {

	}

}
