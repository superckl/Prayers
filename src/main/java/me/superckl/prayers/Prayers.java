package me.superckl.prayers;

import lombok.Getter;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.reference.ModPotions;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.proxy.IProxy;
import me.superckl.prayers.server.commands.CommandPrayers;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid=ModData.MOD_ID, name=ModData.MOD_NAME, version=ModData.VERSION, guiFactory=ModData.GUI_FACTORY)
public class Prayers {

	@Instance(ModData.MOD_ID)
	@Getter
	private static Prayers instance;

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
		ModPotions.init();
		Prayers.proxy.registerRecipes();
		Prayers.proxy.registerEntities();
		Prayers.proxy.setupGuis();
		Prayers.proxy.registerBindings();
	}

	@EventHandler
	public void init(final FMLInitializationEvent e){
		Prayers.proxy.registerHandlers();
		ModItems.addChestLoot();
	}

	@EventHandler
	public void onServerStarting(final FMLServerStartingEvent e){
		e.registerServerCommand(new CommandPrayers());
	}

}
