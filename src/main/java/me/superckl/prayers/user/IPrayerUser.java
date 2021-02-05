package me.superckl.prayers.user;

import java.util.Collection;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.Entity;

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

	static IPrayerUser getUser(final Entity entity) {
		return entity.getCapability(Prayers.PRAYER_USER_CAPABILITY)
				.orElseThrow(() -> new IllegalStateException(String.format("Received entity %s with no prayer capability!", entity.toString())));
	}

}
