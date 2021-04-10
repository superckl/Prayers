package me.superckl.prayers.capability;

import net.minecraft.entity.LivingEntity;

public class DefaultLivingPrayerUser extends LivingPrayerUser<LivingEntity>{

	private final double maxPoints;

	public DefaultLivingPrayerUser(final LivingEntity ref, final double maxPoints) {
		super(ref);
		this.maxPoints = maxPoints;
		this.currentPrayerPoints = maxPoints;
	}

	@Override
	public double getMaxPrayerPoints() {
		return this.maxPoints;
	}

}
