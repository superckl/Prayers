package me.superckl.prayers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lombok.Getter;
import me.superckl.prayers.api.AltarRegistry;
import me.superckl.prayers.common.reference.ModAchievements;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.reference.ModPotions;
import me.superckl.prayers.common.utility.InstanceField;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.StringHelper;
import me.superckl.prayers.integration.PrayersIntegration;
import me.superckl.prayers.proxy.IProxy;
import me.superckl.prayers.server.commands.CommandPrayers;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid=ModData.MOD_ID, name=ModData.MOD_NAME, version=ModData.VERSION, guiFactory=ModData.GUI_FACTORY, dependencies = "after:Waila;after:Thaumcraft;after:AWWayofTime")
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
		//The order of init calls is important. Don't randomly change it.
		ModPotions.init();
		ModItems.init();
		ModBlocks.init();
		ModAchievements.init();
		AltarRegistry.registerMultiBlocks();
		Prayers.proxy.registerRecipes();
		Prayers.proxy.registerEntities();
		Prayers.proxy.registerRenderers();
		Prayers.proxy.setupGuis();
		Prayers.proxy.registerBindings();
		PrayersIntegration.INSTANCE.preInit();
	}

	@EventHandler
	public void init(final FMLInitializationEvent e){
		Prayers.proxy.registerHandlers();

		FMLInterModComms.sendMessage("Waila", "register", "me.superckl.prayers.integration.waila.PrayersWailaDataProvider.callbackRegister");
		FMLInterModComms.sendMessage("Waila", "register", "me.superckl.prayers.integration.waila.PrayersWailaEntityProvider.callbackRegister");

		PrayersIntegration.INSTANCE.init();
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent e){
		Prayers.proxy.registerEntitySpawns();
		ModItems.addChestLoot();
		PrayersIntegration.INSTANCE.postInit();
	}

	@EventHandler
	public void onServerStarting(final FMLServerStartingEvent e){
		e.registerServerCommand(new CommandPrayers());
	}

	@EventHandler
	public void processIMCs(final IMCEvent e){
		if(e.getMessages().isEmpty())
			LogHelper.info("No intermod communications found.");
		for(final IMCMessage message:e.getMessages()){
			if(!message.isStringMessage()){
				LogHelper.error(StringHelper.build("Received invalid message from mod ", message.getSender(), " with key ", message.key, " containing ", message.isItemStackMessage() ? message.getItemStackValue().toString():message.getNBTValue().toString()));
				continue;
			}
			if(!message.key.equalsIgnoreCase("register")){
				LogHelper.error(StringHelper.build("Received invalid message from mod ", message.getSender(), " with key ", message.key, " containing ", message.getStringValue()));
				continue;
			}
			int index = message.getStringValue().lastIndexOf(".");
			if(index == -1){
				LogHelper.error(StringHelper.build("Received invalid message from mod ", message.getSender(), " with key ", message.key, " containing ", message.getStringValue()));
				continue;
			}
			LogHelper.info(StringHelper.build("Attempting to process message from mod ", message.getSender(), " with key ", message.key, " containing ", message.getStringValue()));
			try{
				final String className = message.getStringValue().substring(0, index);
				final String methodName = message.getStringValue().substring(index+1, message.getStringValue().length());
				final Class<?> clazz = Class.forName(className);
				final Method method = clazz.getDeclaredMethod(methodName);
				method.setAccessible(true);
				if((method.getModifiers() & Modifier.STATIC) == Modifier.STATIC){
					LogHelper.info("Field is static. Attempting to invoke...");
					method.invoke(null);
				}else if(method.isAnnotationPresent(InstanceField.class)){
					final InstanceField iField = method.getAnnotation(InstanceField.class);
					LogHelper.info("Found InstanceField annotation on method. Attempting to retrieve instance...");
					Field field;
					if(iField.value().contains(".")){
						index = iField.value().lastIndexOf(".");
						final String className2 = iField.value().substring(0, index);
						final String fieldName = iField.value().substring(index+1, iField.value().length());
						final Class<?> clazz2 = Class.forName(className);
						field = clazz2.getDeclaredField(fieldName);
					}else
						field = clazz.getDeclaredField(iField.value());
					field.setAccessible(true);
					if((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
						throw new IllegalArgumentException(StringHelper.build("Instance field ", iField.value(), " is not static!"));
					method.invoke(field.get(null));
				}else{
					LogHelper.info("No instances found. Attempting to instantiate class...");
					final Object obj = clazz.newInstance();
					method.invoke(obj);
				}
				LogHelper.info("Successfully processed message.");
			}catch(final Exception e1){
				LogHelper.error(StringHelper.build("Received invalid message from mod ", message.getSender(), " with key ", message.key, " containing ", message.getStringValue()));
				LogHelper.error("The following error occured while processing the message:");
				e1.printStackTrace();
				continue;
			}
		}
	}

}
