package me.superckl.prayercraft.proxy;

import me.superckl.prayercraft.client.handler.EntityRenderHandler;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.network.MessageDisablePrayer;
import me.superckl.prayercraft.network.MessageEnablePrayer;
import me.superckl.prayercraft.network.MessageHandlerDisablePrayerClient;
import me.superckl.prayercraft.network.MessageHandlerEnablePrayerClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerHandlers() {
		super.registerHandlers();
		MinecraftForge.EVENT_BUS.register(new EntityRenderHandler());
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerEnablePrayerClient.class, MessageEnablePrayer.class, 0, Side.CLIENT);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerDisablePrayerClient.class, MessageDisablePrayer.class, 1, Side.CLIENT);
	}



}
