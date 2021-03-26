package me.superckl.prayers.capability;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TalismanPrayerProvider extends SimplePrayerTracker<Pair<LivingEntity, ItemStack>> implements IInventoryPrayerProvider{

	@Override
	public float getMaxPrayerPoints() {
		return 200;
	}

}
