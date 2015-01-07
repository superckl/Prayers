package me.superckl.prayers.common.reference;

import me.superckl.prayers.common.item.ItemPotionPrayers;
import me.superckl.prayers.common.potion.PotionPrayerBoost;
import me.superckl.prayers.common.potion.PotionPrayerRestore;
import me.superckl.prayers.common.potion.PotionPrayerRestoreInstant;
import me.superckl.prayers.common.potion.PotionRaiseMaxPoints;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import net.minecraft.potion.Potion;

public class ModPotions {

	public static PotionPrayerBoost prayerBoost;
	public static PotionPrayerRestore prayerRestore;
	public static PotionPrayerRestoreInstant prayerRestoreInstant;
	public static PotionRaiseMaxPoints prayerMaxPointsRaise;

	private static int offset;

	public static void init(){
		LogHelper.info("Extending Potions array...");
		ModPotions.offset = Potion.potionTypes.length;

		final Potion[] potionTypes = new Potion[ModPotions.offset + 4];
		System.arraycopy(Potion.potionTypes, 0, potionTypes, 0, ModPotions.offset);

		PSReflectionHelper.setPrivateFinalValue(Potion.class, null, potionTypes, "potionTypes", "field_76425_a");

		ModPotions.prayerBoost = new PotionPrayerBoost(ModPotions.offset++);
		ModPotions.prayerRestore = new PotionPrayerRestore(ModPotions.offset++);
		ModPotions.prayerRestoreInstant = new PotionPrayerRestoreInstant(ModPotions.offset++);
		ModPotions.prayerMaxPointsRaise = new PotionRaiseMaxPoints(ModPotions.offset++);

		ModItems.potion = new ItemPotionPrayers();
	}

}
