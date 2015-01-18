package me.superckl.prayers.common.reference;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class ModAchievements {

	public static final Achievement RECHARGED = new Achievement("prayerachrecharge", "prayerrecharged", 0, 0, ModBlocks.offeringTable, null).registerStat();
	public static final Achievement TOO_OP = new Achievement("prayersachtooop", "prayerstooop", 0, -2, ModBlocks.offeringTable, ModAchievements.RECHARGED).setSpecial().registerStat();
	public static final Achievement CONVENIENCE = new Achievement("prayersachconvenience", "prayersconvenience", 0, 2, ModBlocks.offeringTable, ModAchievements.RECHARGED).registerStat();
	public static final Achievement FIRST_STEPS = new Achievement("prayerachfirststeps", "prayerfirststeps", 2, 0, ModFluids.filledHolyBottle(), ModAchievements.RECHARGED).registerStat();
	public static final Achievement SELF_SACRIFICE = new Achievement("prayersachselfsac", "prayersselfsac", 5, 1, ModBlocks.offeringTable, ModAchievements.FIRST_STEPS).registerStat();
	public static final Achievement SUCCESS = new Achievement("prayersachsuccess", "prayerssuccess", 7, 1, ModBlocks.offeringTable, ModAchievements.SELF_SACRIFICE).registerStat();

	public static final AchievementPage PRAYERS_PAGE = new AchievementPage(ModData.MOD_NAME, ModAchievements.RECHARGED, ModAchievements.TOO_OP, ModAchievements.CONVENIENCE, ModAchievements.FIRST_STEPS, ModAchievements.SELF_SACRIFICE, ModAchievements.SUCCESS);

	public static void init(){
		AchievementPage.registerAchievementPage(ModAchievements.PRAYERS_PAGE);
	}
}
