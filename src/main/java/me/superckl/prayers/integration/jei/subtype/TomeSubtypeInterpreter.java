package me.superckl.prayers.integration.jei.subtype;

import me.superckl.prayers.item.PrayerTomeItem;
import me.superckl.prayers.prayer.Prayer;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public class TomeSubtypeInterpreter implements ISubtypeInterpreter{

	@Override
	public String apply(final ItemStack itemStack) {
		final LazyOptional<Prayer> prayer = PrayerTomeItem.getStoredPrayer(itemStack);
		if(prayer.isPresent())
			return prayer.orElse(null).getRegistryName().getPath();
		return null;
	}

}
