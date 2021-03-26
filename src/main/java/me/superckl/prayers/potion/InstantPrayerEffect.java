package me.superckl.prayers.potion;

import me.superckl.prayers.capability.ILivingPrayerUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;

public class InstantPrayerEffect extends InstantEffect{

	public InstantPrayerEffect() {
		super(EffectType.BENEFICIAL, 0x3bc492);
	}

	@Override
	public void applyEffectTick(final LivingEntity entityLivingBaseIn, final int amplifier) {
		ILivingPrayerUser.getUser(entityLivingBaseIn).addPoints(75+50*amplifier);
	}

}
