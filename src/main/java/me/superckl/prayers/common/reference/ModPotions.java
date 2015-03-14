package me.superckl.prayers.common.reference;

import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.item.ItemPotionPrayers;
import me.superckl.prayers.common.potion.PotionAttunement;
import me.superckl.prayers.common.potion.PotionPrayerBoost;
import me.superckl.prayers.common.potion.PotionPrayerDrain;
import me.superckl.prayers.common.potion.PotionPrayerRestore;
import me.superckl.prayers.common.potion.PotionPrayerRestoreInstant;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import net.minecraft.potion.Potion;

public final class ModPotions {

	public static PotionPrayerBoost prayerBoost;
	public static PotionPrayerRestore prayerRestore;
	public static PotionPrayerRestoreInstant prayerRestoreInstant;
	public static PotionAttunement prayerMaxPointsRaise;
	public static PotionPrayerDrain prayerDrain;

	public static void init(){
		LogHelper.info("Extending Potions array...");
		final Config c = Prayers.getInstance().getConfig();
		if((Potion.potionTypes.length >= 256) || !c.isDynamicPotionIDs()){
			if(c.isDynamicPotionIDs()){
				LogHelper.warn("A mod decided to yolo it and set the potion array to length 256! Tsk tsk...");
				LogHelper.warn("Resorting to static potion IDs...");
			}
			final Potion[] potionTypes = new Potion[256];
			System.arraycopy(Potion.potionTypes, 0, potionTypes, 0, Potion.potionTypes.length);

			PSReflectionHelper.setPrivateFinalValue(Potion.class, null, potionTypes, "potionTypes", "field_76425_a");

			ModPotions.prayerBoost = new PotionPrayerBoost(c.getPrayerBoostID());
			ModPotions.prayerRestore = new PotionPrayerRestore(c.getPrayerRestoreID());
			ModPotions.prayerRestoreInstant = new PotionPrayerRestoreInstant(c.getPrayerRestoreInstantID());
			ModPotions.prayerMaxPointsRaise = new PotionAttunement(c.getPrayerMaxPointsRaiseID());
			ModPotions.prayerDrain = new PotionPrayerDrain(c.getPrayerDrainID());
		}else{
			int offset = Potion.potionTypes.length;
			final Potion[] potionTypes = new Potion[offset + 5];
			System.arraycopy(Potion.potionTypes, 0, potionTypes, 0, offset);

			PSReflectionHelper.setPrivateFinalValue(Potion.class, null, potionTypes, "potionTypes", "field_76425_a");

			ModPotions.prayerBoost = new PotionPrayerBoost(offset++);
			ModPotions.prayerRestore = new PotionPrayerRestore(offset++);
			ModPotions.prayerRestoreInstant = new PotionPrayerRestoreInstant(offset++);
			ModPotions.prayerMaxPointsRaise = new PotionAttunement(offset++);
			ModPotions.prayerDrain = new PotionPrayerDrain(offset++);
		}
		ModItems.potion = new ItemPotionPrayers();
	}

}
