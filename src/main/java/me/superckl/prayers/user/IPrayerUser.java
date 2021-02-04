package me.superckl.prayers.user;

import java.util.Collection;

import me.superckl.prayers.Prayer;

public interface IPrayerUser{

	boolean canActivatePrayer(Prayer prayer);

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

}
