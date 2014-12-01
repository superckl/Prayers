package me.superckl.prayercraft.common.handler;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityConstruction(final EntityConstructing e){
		if(e.entity instanceof EntityPlayer)
			e.entity.registerExtendedProperties("prayer", new PrayerExtendedProperties());
	}

}
