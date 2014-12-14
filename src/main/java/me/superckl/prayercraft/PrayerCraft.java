package me.superckl.prayercraft;

import lombok.Getter;
import me.superckl.prayercraft.common.reference.ModBlocks;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModFluids;
import me.superckl.prayercraft.common.reference.ModItems;
import me.superckl.prayercraft.common.utility.LogHelper;
import me.superckl.prayercraft.proxy.IProxy;
import me.superckl.prayercraft.server.commands.CommandPrayerCraft;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid=ModData.MOD_ID, name=ModData.MOD_NAME, version=ModData.VERSION, guiFactory=ModData.GUI_FACTORY)
public class PrayerCraft {

	@Instance(ModData.MOD_ID)
	@Getter
	private static PrayerCraft instance;

	@SidedProxy(clientSide=ModData.CLIENT_PROXY, serverSide=ModData.SERVER_PROXY)
	@Getter
	private static IProxy proxy;

	@Getter
	private Config config;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent e){
		LogHelper.info("Please note, you are running a beta version! Please report any bugs you find.");
		this.config = new Config(e.getSuggestedConfigurationFile());
		this.config.loadValues();
		ModItems.init();
		ModFluids.init();
		ModBlocks.init();
		PrayerCraft.proxy.registerRecipes();
		PrayerCraft.proxy.registerEntities();
		PrayerCraft.proxy.setupGuis();
		PrayerCraft.proxy.registerBindings();
	}

	@EventHandler
	public void init(final FMLInitializationEvent e){
		PrayerCraft.proxy.registerHandlers();
		ModItems.addChestLoot();
	}

	@EventHandler
	public void onServerStarting(final FMLServerStartingEvent e){
		e.registerServerCommand(new CommandPrayerCraft());
	}

}
