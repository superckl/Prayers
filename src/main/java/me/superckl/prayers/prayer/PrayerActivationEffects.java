package me.superckl.prayers.prayer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PrayerActivationEffects {

	private static boolean couldFly;

	public static void onActivateFlight(final LivingEntity e) {
		if(e instanceof PlayerEntity) {
			PrayerActivationEffects.couldFly = ((PlayerEntity)e).abilities.mayfly;
			((PlayerEntity)e).abilities.mayfly = true;
		}
	}

	public static void onDeactivateFlight(final LivingEntity e) {
		if(e instanceof PlayerEntity) {
			((PlayerEntity)e).abilities.mayfly = PrayerActivationEffects.couldFly;
			if(!PrayerActivationEffects.couldFly)
				((PlayerEntity)e).abilities.flying = false;
		}
	}

}
