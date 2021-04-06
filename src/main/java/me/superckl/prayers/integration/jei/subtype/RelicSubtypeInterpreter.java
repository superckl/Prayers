package me.superckl.prayers.integration.jei.subtype;

import me.superckl.prayers.item.RelicItem;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class RelicSubtypeInterpreter implements ISubtypeInterpreter{

	@Override
	public String apply(final ItemStack itemStack) {
		if(RelicItem.isCharged(itemStack))
			return "charged";
		return null;
	}

}
