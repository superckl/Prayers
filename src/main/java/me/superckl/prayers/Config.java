package me.superckl.prayers;

import java.io.File;

import lombok.Getter;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static final class Category{

		public static String GENERAL = "general";
		public static String ALTAR = "altar";

	}

	@Getter
	private final Configuration configFile;
	@Getter
	private boolean rechargeEverywhere;
	@Getter
	private float tier1Max;
	@Getter
	private int tier1RechargeDelay;
	@Getter
	private float tier1RechargeRate;

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
			this.rechargeEverywhere = this.configFile.getBoolean("Recharge Everywhere", Category.ALTAR, true, "If false, a player will only be able to recharge their prayer points by right clicking an offering table, instead of any block in the altar. If true, this can get quite CPU intensive if there are many large altars loaded.");
			this.tier1Max = this.configFile.getFloat("Tier 1 Max Points", Category.ALTAR, 500F, 0F, Float.MAX_VALUE, "Determines how many prayer points a tier 1 altar will have.");
			this.tier1RechargeDelay = this.configFile.getInt("Tier 1 Recharge Delay", Category.ALTAR, 200, 0, Integer.MAX_VALUE, "Determines how long a tier 1 altar will wate before applying the recharge rate. Measured in ticks.");
			this.tier1RechargeRate = this.configFile.getFloat("Tier 1 Recharge Rate", Category.ALTAR, 1F, 0F, Float.MAX_VALUE, "Determines how much a tier 1 altar will recharge.");
			this.configFile.save();
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
			if(this.configFile.hasChanged())
				this.configFile.save();
		}
	}

	public void setStats(final Altar altar){
		switch(altar.getTier()){
		case 1:
		{
			altar.setBaseRechargeDelay(this.tier1RechargeDelay);
			altar.setBaseRechargeRate(this.tier1RechargeRate);
			altar.setMaxPrayerPoints(this.tier1Max);
			altar.setPrayerPoints(this.tier1Max);
		}
		}
	}

	@SubscribeEvent
	public void onConfigChange(final OnConfigChangedEvent e){
		if(e.modID.equals(ModData.MOD_ID))
			this.loadValues();
	}

}
