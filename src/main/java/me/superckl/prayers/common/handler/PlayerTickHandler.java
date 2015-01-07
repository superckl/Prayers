package me.superckl.prayers.common.handler;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class PlayerTickHandler {

	@SubscribeEvent(receiveCanceled = false, priority = EventPriority.LOW)
	public void onPlayerTick(final PlayerTickEvent e){
		if((e.phase != TickEvent.Phase.END) || (e.side != Side.SERVER))
			return;
		((PrayerExtendedProperties)e.player.getExtendedProperties("prayer")).playerTick();
	}

}
