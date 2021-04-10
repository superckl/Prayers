package me.superckl.prayers.capability;

import me.superckl.prayers.Config;
import net.minecraft.item.ItemStack;

public class TalismanPrayerProvider extends InventoryPrayerProvider{

	private final double maxPoints = Config.getInstance().getTalimsanPoints().get();

	public TalismanPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	@Override
	public double getMaxPrayerPoints() {
		return this.maxPoints;
	}

}
