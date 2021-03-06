package me.superckl.prayers.potion;

import me.superckl.prayers.capability.CapabilityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;

public class InstantPrayerEffect extends InstantEffect{

	private final double base;
	private final double amplifier;

	public InstantPrayerEffect(final double base, final double amplifier) {
		super(EffectType.BENEFICIAL, 0x3bc492);
		this.base = base;
		this.amplifier = amplifier;
	}

	@Override
	public void applyEffectTick(final LivingEntity entity, final int amplifier) {
		this.applyInstantenousEffect(null, null, entity, amplifier, 1);
	}

	@Override
	public void applyInstantenousEffect(final Entity damager, final Entity source, final LivingEntity entity,
			final int amplifier, final double modifier) {
		if(entity.isAlive())
			CapabilityHandler.getPrayerCapability(entity).addPoints((this.base+this.amplifier*amplifier)*modifier);
	}

}
