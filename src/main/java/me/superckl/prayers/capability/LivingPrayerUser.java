package me.superckl.prayers.capability;

import net.minecraft.entity.LivingEntity;

public abstract class LivingPrayerUser extends TickablePrayerProvider<LivingEntity>{

	public LivingPrayerUser(final LivingEntity ref) {
		super(ref);
	}

}
