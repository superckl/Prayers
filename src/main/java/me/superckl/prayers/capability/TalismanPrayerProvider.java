package me.superckl.prayers.capability;

import net.minecraft.item.ItemStack;

public class TalismanPrayerProvider extends InventoryPrayerProvider{

	public TalismanPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	@Override
	public float getMaxPrayerPoints() {
		return 200;
	}

}
