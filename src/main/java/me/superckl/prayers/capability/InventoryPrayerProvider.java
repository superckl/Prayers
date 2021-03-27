package me.superckl.prayers.capability;

import java.util.Collection;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.capability.PlayerPrayerUser.Result;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public abstract class InventoryPrayerProvider extends TickablePrayerProvider<ItemStack>{

	public InventoryPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	public void inventoryTick(final LivingEntity entity) {
		final Collection<Prayer> prayers = this.getActivePrayers();
		if(prayers.isEmpty())
			return;
		final float drain = (float) prayers.stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			final float diff = -newPoints;
			newPoints = 0;
			final PrayerInventoryItem<?> item = (PrayerInventoryItem<?>) this.ref.getItem();
			if(item.isShouldDrainHolder()) {
				final TickablePrayerProvider<? extends LivingEntity> user = CapabilityHandler.getPrayerCapability(entity);
				final float remainingPoints = user.setCurrentPrayerPoints(user.getCurrentPrayerPoints()-diff);
				if(remainingPoints <= 0) {
					this.deactivateAllPrayers();
					item.onPointsDepleted();
				}
			}else {
				this.deactivateAllPrayers();
				item.onPointsDepleted();
			}
		}
		this.setCurrentPrayerPoints(newPoints);
	}

	public boolean activatePrayer(final Prayer prayer, final PlayerEntity player) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		if(user.isPrayerActive(prayer, false) || user.canActivatePrayer(prayer) == Result.YES) {
			super.activatePrayer(prayer);
			user.deactivatePrayer(prayer);
			return true;
		}else
			return false;
	}

	public boolean togglePrayer(final Prayer prayer, final PlayerEntity player) {
		if (this.isPrayerActive(prayer)) {
			this.deactivatePrayer(prayer);
			return true;
		}else
			return this.activatePrayer(prayer, player);
	}

	@Override
	public void tick() {}

}
