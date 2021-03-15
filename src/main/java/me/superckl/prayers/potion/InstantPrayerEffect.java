package me.superckl.prayers.potion;

import java.awt.Color;

import me.superckl.prayers.capability.IPrayerUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;

public class InstantPrayerEffect extends InstantEffect{

	public InstantPrayerEffect() {
		super(EffectType.BENEFICIAL, Color.CYAN.getRGB());
	}

	@Override
	public void performEffect(final LivingEntity entityLivingBaseIn, final int amplifier) {
		IPrayerUser.getUser(entityLivingBaseIn).addPoints(75+50*amplifier);
	}

}
