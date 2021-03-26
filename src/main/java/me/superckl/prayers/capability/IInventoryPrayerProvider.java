package me.superckl.prayers.capability;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IInventoryPrayerProvider extends ITickablePrayerProvider<Pair<LivingEntity, ItemStack>>{

	static IInventoryPrayerProvider get(final ItemStack stack) {
		return stack.getCapability(Prayers.INVENTORY_PRAYER_CAPABILITY).orElseThrow(() ->
		new IllegalArgumentException("Passed itemstack with no inventory prayer capability!"));
	}

	@Override
	default void tick(final Pair<LivingEntity, ItemStack> reference) {
		final Collection<Prayer> prayers = this.getActivePrayers();
		if(prayers.isEmpty())
			return;
		final float drain = (float) prayers.stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			final float diff = -newPoints;
			newPoints = 0;
			final PrayerInventoryItem item = (PrayerInventoryItem) reference.getValue().getItem();
			if(item.isShouldDrainHolder()) {
				final ILivingPrayerUser user = ILivingPrayerUser.get(reference.getKey());
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

}
