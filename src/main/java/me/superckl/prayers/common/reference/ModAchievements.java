package me.superckl.prayers.common.reference;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class ModAchievements {

	public static final Achievement RECHARGED = new Achievement("prayerachrecharge", "prayerrecharged", 0, 0, ModBlocks.altarBase, null).registerStat();
	public static final Achievement FIRST_STEPS = new Achievement("prayerachfirststeps", "prayerfirststeps", 1, 0, ModFluids.filledHolyBottle(), ModAchievements.RECHARGED).registerStat();

	public static final AchievementPage PRAYERS_PAGE = new AchievementPage(ModData.MOD_NAME, ModAchievements.RECHARGED, ModAchievements.FIRST_STEPS);

	public static void init(){
		AchievementPage.registerAchievementPage(ModAchievements.PRAYERS_PAGE);
	}
}
