package me.superckl.prayercraft.proxy;

import me.superckl.prayercraft.PrayerCraft;
import me.superckl.prayercraft.common.gui.GuiHandler;
import me.superckl.prayercraft.common.handler.EntityEventHandler;
import me.superckl.prayercraft.common.handler.PlayerTickHandler;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.network.MessageDisablePrayer;
import me.superckl.prayercraft.network.MessageEnablePrayer;
import me.superckl.prayercraft.network.MessageHandlerDisablePrayerServer;
import me.superckl.prayercraft.network.MessageHandlerEnablePrayerServer;
import me.superckl.prayercraft.network.MessageHandlerOpenPrayerGui;
import me.superckl.prayercraft.network.MessageOpenPrayerGui;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IProxy{

	@Override
	public void registerHandlers() {
		FMLCommonHandler.instance().bus().register(PrayerCraft.getInstance().getConfig());
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(PrayerCraft.getInstance(), new GuiHandler());
		ModData.PRAYER_UPDATE_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("prayerUpdate");
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerEnablePrayerServer.class, MessageEnablePrayer.class, 0, Side.SERVER);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerDisablePrayerServer.class, MessageDisablePrayer.class, 1, Side.SERVER);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerOpenPrayerGui.class, MessageOpenPrayerGui.class, 2, Side.SERVER);
	}

	@Override
	public void setupGuis() {}

}
