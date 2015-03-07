package me.superckl.prayers.api;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import me.superckl.prayers.client.gui.GuiContainerPrayers;
import me.superckl.prayers.client.gui.PrayerGuiClickHandler;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.prayer.IPrayerUser;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PrayerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public final class PrayersAPI {

	private static final Set<Class<? extends EntityLivingBase>> excludes = new HashSet<Class<? extends EntityLivingBase>>();

	/**
	 * Adds an entity class to the no drops set. No bones will be dropped for entities of classes in this set.
	 * @param entityClass The entity class to exclude
	 */
	public static void removeFromBoneDrops(final Class<? extends EntityLivingBase> entityClass){
		PrayersAPI.excludes.add(entityClass);
		LogHelper.debug("Removed bone drops for entity class: "+entityClass.getSimpleName());
	}

	public static boolean isExcludedFromBoneDrops(final EntityLivingBase entity){
		return PrayersAPI.isExcludedFromBoneDrops(entity.getClass());
	}

	public static boolean isExcludedFromBoneDrops(final Class<? extends EntityLivingBase> clazz){
		return PrayersAPI.excludes.contains(clazz);
	}

	public static boolean doesEntityusePrayers(final EntityLivingBase entity){
		return (entity instanceof EntityPlayer) || (entity instanceof IPrayerUser);
	}

	public static EnumSet<EnumPrayers> getActivePrayers(final EntityLivingBase entity){
		return PrayerHelper.getActivePrayers(entity);
	}

	/**
	 * Registers a ClickHandler.
	 * @param prayer The prayer to register the handler for.
	 * @param priority The priority to register the handler with. Higher priorities will be called earlier.
	 * @param handler The handler to register.
	 */
	public static void registerPrayerClickHandler(final EnumPrayers prayer, final int priority, final PrayerGuiClickHandler handler){
		GuiContainerPrayers.registerPrayerClickHandler(prayer, priority, handler);
	}

}
