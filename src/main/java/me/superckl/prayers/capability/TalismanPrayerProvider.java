package me.superckl.prayers.capability;

import me.superckl.prayers.Config;
import net.minecraft.item.ItemStack;

public class TalismanPrayerProvider extends InventoryPrayerProvider{

	private final float maxPoints = Config.getInstance().getTalimsanPoints().get().floatValue();

	public TalismanPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	@Override
	public float getMaxPrayerPoints() {
		return this.maxPoints;
	}

}
