package me.superckl.prayers.capability;

import net.minecraft.entity.LivingEntity;

public class DefaultLivingPrayerUser extends LivingPrayerUser<LivingEntity>{

	private final float maxPoints;

	public DefaultLivingPrayerUser(final LivingEntity ref, final float maxPoints) {
		super(ref);
		this.maxPoints = maxPoints;
		this.currentPrayerPoints = maxPoints;
	}

	@Override
	public float getMaxPrayerPoints() {
		return this.maxPoints;
	}

}
