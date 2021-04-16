package me.superckl.prayers.potion;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.LivingPrayerUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;
import net.minecraft.util.math.MathHelper;

public class ScaledInstantPrayerEffect extends InstantEffect{

	private final double min, max, scaling;

	public ScaledInstantPrayerEffect(final double min, final double max, final double scaling) {
		super(EffectType.BENEFICIAL, 0x3bc492);
		this.min = min;
		this.max = max;
		this.scaling = scaling;
	}

	@Override
	public void applyEffectTick(final LivingEntity entity, final int amplifier) {
		this.applyInstantenousEffect(null, null, entity, amplifier, 1);
	}

	@Override
	public void applyInstantenousEffect(final Entity damager, final Entity source, final LivingEntity entity,
			final int amplifier, final double modifier) {
		if(entity.isAlive()) {
			final LivingPrayerUser<?> user = CapabilityHandler.getPrayerCapability(entity);
			user.addPoints(MathHelper.clamp(user.getMaxPrayerPoints()*this.scaling, this.min, this.max)*modifier);
		}
	}

}
