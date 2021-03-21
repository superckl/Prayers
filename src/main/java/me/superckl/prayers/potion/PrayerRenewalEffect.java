package me.superckl.prayers.potion;

import me.superckl.prayers.capability.IPrayerUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class PrayerRenewalEffect extends Effect{

	public PrayerRenewalEffect() {
		super(EffectType.BENEFICIAL, 0x69150e);
	}

	@Override
	public void applyEffectTick(final LivingEntity entityLivingBaseIn, final int amplifier) {
		IPrayerUser.getUser(entityLivingBaseIn).addPoints(1);
	}

	@Override
	public boolean isDurationEffectTick(final int duration, final int amplifier) {
		final int time = 30 >> amplifier;
		if(time > 0)
			return duration % time == 0;
		else
			return true;
	}

}
