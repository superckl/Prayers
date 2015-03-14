package me.superckl.prayers;

import java.io.File;

import lombok.Getter;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Getter
public class Config {

	public static final class Category{

		public static String GENERAL = "general";
		public static String ALTAR = "altar";
		public static String BLOOD_MAGIC = "bloodmagic";
		public static String POTIONS = "potions";

	}

	private final Configuration configFile;
	private int villagerID;
	//@Getter
	//private boolean rechargeEverywhere;
	private float tier1Max;
	private int tier1RechargeDelay;
	private float tier1RechargeRate;
	private boolean orbRecipe;
	private int orbExchangeRate;
	private boolean dynamicPotionIDs;
	private int prayerBoostID;
	private int prayerRestoreID;
	private int prayerRestoreInstantID;
	private int prayerMaxPointsRaiseID;
	private int prayerDrainID;

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
			this.villagerID = this.configFile.getInt("Villager ID", Category.GENERAL, 757767, 0, Integer.MAX_VALUE, "The ID with which to register the priest villager.");
			//this.rechargeEverywhere = this.configFile.getBoolean("Recharge Everywhere", Category.ALTAR, true, "If false, a player will only be able to recharge their prayer points by right clicking an offering table, instead of any block in the altar. If true, this can get quite CPU intensive if there are many large altars loaded.");
			this.tier1Max = this.configFile.getFloat("Tier 1 Max Points", Category.ALTAR, 500F, 0F, Float.MAX_VALUE, "Determines how many prayer points a tier 1 altar will have.");
			this.tier1RechargeDelay = this.configFile.getInt("Tier 1 Recharge Delay", Category.ALTAR, 200, 0, Integer.MAX_VALUE, "Determines how long a tier 1 altar will wate before applying the recharge rate. Measured in ticks.");
			this.tier1RechargeRate = this.configFile.getFloat("Tier 1 Recharge Rate", Category.ALTAR, 1F, 0F, Float.MAX_VALUE, "Determines how much a tier 1 altar will recharge.");
			this.orbRecipe = this.configFile.getBoolean("Blood Orb Charge Recipe", Category.BLOOD_MAGIC, true, "Enables a player to place blood orbs on an altar to exchange prayer points from that altar for points in their soul network.");
			this.orbExchangeRate = this.configFile.getInt("Point Exchange Rate", Category.BLOOD_MAGIC, 5, 0, Integer.MAX_VALUE, "Determines how many blood points one prayer point gets converted to.");
			this.dynamicPotionIDs = this.configFile.getBoolean("Dynamic Potion IDs", Category.POTIONS, true, "If true, Prayers will attempt to insert potions at the end of the array. If this fails, Prayers will resort to the static IDs specified.");
			this.prayerBoostID = this.configFile.getInt("Prayer Boost ID", Category.POTIONS, 75, 0, 255, "The static ID to be used if dynamic IDs are disabled or dynamic ID resolution fails.");
			this.prayerRestoreID = this.configFile.getInt("Prayer Restore ID", Category.POTIONS, 76, 0, 255, "The static ID to be used if dynamic IDs are disabled or dynamic ID resolution fails.");
			this.prayerRestoreInstantID = this.configFile.getInt("Prayer Restore Instant ID", Category.POTIONS, 77, 0, 255, "The static ID to be used if dynamic IDs are disabled or dynamic ID resolution fails.");
			this.prayerMaxPointsRaiseID = this.configFile.getInt("Prayer Max Points Raise ID", Category.POTIONS, 78, 0, 255, "The static ID to be used if dynamic IDs are disabled or dynamic ID resolution fails.");
			this.prayerDrainID = this.configFile.getInt("Prayer Drain ID", Category.POTIONS, 79, 0, 255, "The static ID to be used if dynamic IDs are disabled or dynamic ID resolution fails.");
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
