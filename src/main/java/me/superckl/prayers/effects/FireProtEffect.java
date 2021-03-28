package me.superckl.prayers.effects;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FireProtEffect extends PrayerEffect{

	@SubscribeEvent(priority =  EventPriority.HIGH)
	public void onLivingHurt(final LivingAttackEvent e) {
		if(e.getSource().isFire() && this.getOwner().isActive(e.getEntityLiving()))
			e.setCanceled(true);
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Fire immunity";
	}

}
