package me.superckl.prayers.common.reference;

import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public final class ModAchievements {

	public static final Achievement RECHARGED = new Achievement("prayerachrecharge", "prayerrecharged", 0, 0, ModBlocks.offeringTable, null).registerStat();
	public static final Achievement FIRST_STEPS = new Achievement("prayerachfirststeps", "prayerfirststeps", 2, 0, ModItems.bottle, ModAchievements.RECHARGED).registerStat();
	public static final Achievement ANCIENTS_WRATH = new Achievement("prayerachwrath", "prayerwrath", 2, 2, Items.diamond_sword, ModAchievements.FIRST_STEPS).registerStat();
	public static final Achievement TOO_OP = new Achievement("prayerachtooop", "prayertooop", 0, -2, ModBlocks.offeringTable, ModAchievements.RECHARGED).setSpecial().registerStat();

	public static final AchievementPage PRAYERS_PAGE = new AchievementPage(ModData.MOD_NAME, ModAchievements.RECHARGED, ModAchievements.TOO_OP, ModAchievements.FIRST_STEPS, ModAchievements.ANCIENTS_WRATH);

	public static void init(){
		AchievementPage.registerAchievementPage(ModAchievements.PRAYERS_PAGE);
	}
}
