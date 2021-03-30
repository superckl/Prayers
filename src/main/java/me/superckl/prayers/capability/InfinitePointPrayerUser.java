package me.superckl.prayers.capability;

import net.minecraft.entity.LivingEntity;

public class InfinitePointPrayerUser extends LivingPrayerUser{

	public InfinitePointPrayerUser(final LivingEntity ref) {
		super(ref);
	}

	@Override
	public float getMaxPrayerPoints() {
		return Float.MAX_VALUE;
	}

	@Override
	public float getCurrentPrayerPoints() {
		return this.getMaxPrayerPoints();
	}

	@Override
	public float setCurrentPrayerPoints(final float currentPoints) {
		return this.getMaxPrayerPoints();
	}

	@Override
	public float addPoints(final float points) {
		return 0;
	}

	@Override
	public void tick() {}

}
