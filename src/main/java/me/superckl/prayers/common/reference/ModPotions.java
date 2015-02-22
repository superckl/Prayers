package me.superckl.prayers.common.reference;

import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.item.ItemPotionPrayers;
import me.superckl.prayers.common.potion.PotionAttunement;
import me.superckl.prayers.common.potion.PotionPrayerBoost;
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

	private static int offset;

	public static void init(){
		LogHelper.info("Extending Potions array...");
		final Config c = Prayers.getInstance().getConfig();
		ModPotions.offset = Potion.potionTypes.length;
		if((ModPotions.offset >= 256) || !c.isDynamicPotionIDs()){
			if(c.isDynamicPotionIDs()){
				LogHelper.warn("A mod decided to yolo it and set the potion array to length 256! Tsk tsk...");
				LogHelper.warn("If you're certain that no mod did this, then you have a larger problem occuring. The potion array is full.");
				LogHelper.warn("Resorting to static potion IDs...");
			}
			ModPotions.prayerBoost = new PotionPrayerBoost(c.getPrayerBoostID());
			ModPotions.prayerRestore = new PotionPrayerRestore(c.getPrayerRestoreID());
			ModPotions.prayerRestoreInstant = new PotionPrayerRestoreInstant(c.getPrayerRestoreInstantID());
			ModPotions.prayerMaxPointsRaise = new PotionAttunement(c.getPrayerMaxPointsRaiseID());
		}else{
			final Potion[] potionTypes = new Potion[ModPotions.offset + 4];
			System.arraycopy(Potion.potionTypes, 0, potionTypes, 0, ModPotions.offset);

			PSReflectionHelper.setPrivateFinalValue(Potion.class, null, potionTypes, "potionTypes", "field_76425_a");

			ModPotions.prayerBoost = new PotionPrayerBoost(ModPotions.offset++);
			ModPotions.prayerRestore = new PotionPrayerRestore(ModPotions.offset++);
			ModPotions.prayerRestoreInstant = new PotionPrayerRestoreInstant(ModPotions.offset++);
			ModPotions.prayerMaxPointsRaise = new PotionAttunement(ModPotions.offset++);
		}
		ModItems.potion = new ItemPotionPrayers();
	}

}
