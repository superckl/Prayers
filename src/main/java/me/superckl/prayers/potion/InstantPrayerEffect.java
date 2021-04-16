package me.superckl.prayers.potion;

import me.superckl.prayers.capability.CapabilityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;

public class InstantPrayerEffect extends InstantEffect{

	private final double base;
	private final double amplifier;
	
	public InstantPrayerEffect(double base, double amplifier) {
		super(EffectType.BENEFICIAL, 0x3bc492);
		this.base = base;
		this.amplifier = amplifier;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		this.applyInstantenousEffect(null, null, entity, amplifier, 1);
	}
	
	@Override
	public void applyInstantenousEffect(Entity damager, Entity source, LivingEntity entity,
			int amplifier, double modifier) {
		if(entity.isAlive())
			CapabilityHandler.getPrayerCapability(entity).addPoints((this.base+this.amplifier*amplifier)*modifier);
	}
	
}
