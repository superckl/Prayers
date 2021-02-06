package me.superckl.prayers.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.Entity;

public interface IPrayerUser{

	default boolean canActivatePrayer(final Prayer prayer) {
		if(!prayer.isEnabled() || this.getCurrentPrayerPoints() < prayer.getDrain()/20F)
			return false;
		final Set<String> excludes = Sets.newHashSet();
		this.getActivePrayers().forEach(activePrayer -> excludes.addAll(activePrayer.getExclusionTypes()));
		return Collections.disjoint(prayer.getExclusionTypes(), excludes);
	}

	void activatePrayer(Prayer prayer);

	void deactivatePrayer(Prayer prayer);

	boolean isPrayerActive(Prayer prayer);

	float getMaxPrayerPoints();

	float getCurrentPrayerPoints();

	float addMaxPointsBoost(float boost);

	float setMaxPointsBoost(float boost);

	float getMaxPointsBoost();

	float setCurrentPrayerPoints(float currentPoints);

	int getPrayerLevel();

	int setPrayerLevel(int level);

	Collection<Prayer> getActivePrayers();

	void deactivateAllPrayers();

	default void togglePrayer(final Prayer prayer) {
		if (this.isPrayerActive(prayer))
			this.deactivatePrayer(prayer);
		else
			this.activatePrayer(prayer);
	}

	default void applyDrain() {
		final float drain = (float) this.getActivePrayers().stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			newPoints = 0;
			this.deactivateAllPrayers();
		}
		this.setCurrentPrayerPoints(newPoints);
	}

	static IPrayerUser getUser(final Entity entity) {
		return entity.getCapability(Prayers.PRAYER_USER_CAPABILITY)
				.orElseThrow(() -> new IllegalStateException(String.format("Received entity %s with no prayer capability!", entity.toString())));
	}

}
