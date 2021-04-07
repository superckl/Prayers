package me.superckl.prayers.capability;

import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ReliquaryPrayerProvider extends InventoryPrayerProvider{

	public ReliquaryPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	@Override
	public float getMaxPrayerPoints() {
		return 500;
	}

	@Override
	public boolean canActivatePrayer(final PlayerEntity player, final Prayer prayer) {
		return false;
	}

}
