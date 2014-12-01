package me.superckl.prayercraft;

import java.io.File;

import lombok.Getter;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.utility.LogHelper;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	@Getter
	private final Configuration configFile;

	public static final class Category{

		public static String GENERAL = "general";

	}

	public Config(final File config){
		this.configFile = new Configuration(config);
		try{
			this.configFile.load();
		}catch(final Exception e){
			LogHelper.warn("Failed to load configuration! All options will be set to their default values.");
			e.printStackTrace();
		}finally{
			if(this.configFile.hasChanged())
				this.configFile.save();
		}
	}

	public void loadValues(){
		try{
			this.configFile.save();
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
			if(this.configFile.hasChanged())
				this.configFile.save();
		}
	}

	@SubscribeEvent
	public void onConfigChange(final OnConfigChangedEvent e){
		if(e.modID.equals(ModData.MOD_ID))
			this.loadValues();
	}

}
