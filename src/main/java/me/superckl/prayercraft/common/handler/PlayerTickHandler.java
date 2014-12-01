package me.superckl.prayercraft.common.handler;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerTickHandler {

	@SubscribeEvent
	public void onPlayerTick(final PlayerTickEvent e){
		if(e.phase != TickEvent.Phase.END)
			return;
		((PrayerExtendedProperties)e.player.getExtendedProperties("prayer")).playerTick();
	}

}
