package me.superckl.prayercraft.proxy;

import me.superckl.prayercraft.PrayerCraft;
import me.superckl.prayercraft.common.gui.GuiHandler;
import me.superckl.prayercraft.common.handler.EntityEventHandler;
import me.superckl.prayercraft.common.handler.PlayerTickHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy implements IProxy{

	@Override
	public void registerHandlers() {
		FMLCommonHandler.instance().bus().register(PrayerCraft.getInstance().getConfig());
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(PrayerCraft.getInstance(), new GuiHandler());
	}

}
