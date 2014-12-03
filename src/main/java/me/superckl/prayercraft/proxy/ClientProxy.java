package me.superckl.prayercraft.proxy;

import me.superckl.prayercraft.client.gui.InventoryTabPrayers;
import me.superckl.prayercraft.client.handler.EntityRenderHandler;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.network.MessageDisablePrayer;
import me.superckl.prayercraft.network.MessageEnablePrayer;
import me.superckl.prayercraft.network.MessageHandlerDisablePrayerClient;
import me.superckl.prayercraft.network.MessageHandlerEnablePrayerClient;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerHandlers() {
		super.registerHandlers();
		MinecraftForge.EVENT_BUS.register(new EntityRenderHandler());
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerEnablePrayerClient.class, MessageEnablePrayer.class, 0, Side.CLIENT);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerDisablePrayerClient.class, MessageDisablePrayer.class, 1, Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(new TabRegistry());
	}

	@Override
	public void setupGuis() {
		super.setupGuis();
		if(!Loader.isModLoaded("TConstruct") && !Loader.isModLoaded("Galacticraft")){
			boolean found = false;
			for(final AbstractTab tab:TabRegistry.getTabList())
				if(tab instanceof InventoryTabVanilla){
					found = true;
					break;
				}
			if(!found)
				TabRegistry.registerTab(new InventoryTabVanilla());
		}
		TabRegistry.registerTab(new InventoryTabPrayers());

	}



}
