package me.superckl.prayers.common.utility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.prayer.IPrayerUser;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import scala.actors.threadpool.Arrays;

public class PrayerHelper {

	public static EnumSet<EnumPrayers> fromNBT(final NBTTagCompound comp){
		final EnumSet<EnumPrayers> list = EnumSet.noneOf(EnumPrayers.class);
		int i = 0;
		while(comp.hasKey(Integer.toString(i)))
			list.add(EnumPrayers.getById(comp.getString(Integer.toString(i++))));
		return list;
	}

	public static NBTTagCompound toNBT(final EnumSet<EnumPrayers> enumSet){
		final List<EnumPrayers> activePrayers = new ArrayList<EnumPrayers>(enumSet);
		final NBTTagCompound comp = new NBTTagCompound();
		for(int i = 0; i < activePrayers.size(); i++)
			comp.setString(Integer.toString(i), activePrayers.get(i).getId());
		return comp;
	}

	public static boolean hasConflictions(final EnumSet<EnumPrayers> list){
		boolean hasOverhead = false;
		for(final EnumPrayers prayer:list)
			if(prayer.isOverhead()){
				if(hasOverhead)
					return true;
				hasOverhead = true;
			}
		return false;
	}

	public static EnumPrayers[] getAllPrayersDrainSorted(){
		return PrayerHelper.getAllPrayersSorted(new PrayerDrainComparator());
	}

	public static EnumPrayers[] getAllPrayersSorted(final Comparator<EnumPrayers> compare){
		final EnumPrayers[] prayers = EnumPrayers.values();
		Arrays.sort(prayers, compare);
		return prayers;
	}

	public static EnumSet<EnumPrayers> getActivePrayers(final EntityLivingBase entity){
		if(entity instanceof EntityPlayer)
			return ((PrayerExtendedProperties)((EntityPlayer)entity).getExtendedProperties("prayer")).getActivePrayers();
		else if(entity instanceof IPrayerUser)
			return ((IPrayerUser)entity).getActivePrayers();
		return EnumSet.noneOf(EnumPrayers.class);
	}

	public static float handlePotency(float amount, final EnumSet<EnumPrayers> enumSet){
		for(final EnumPrayers prayer:enumSet)
			switch(prayer){
			case POTENCY_1:
			{
				amount *= 1.3F;
				break;
			}
			case POTENCY_2:
			{
				amount *= 1.5F;
				break;
			}
			default:
				break;
			}
		return amount;
	}

	public static float handleEnhanceMelee(float amount, final EnumSet<EnumPrayers> prayers){
		amount = PrayerHelper.handlePotency(amount, prayers);
		for(final EnumPrayers prayer:prayers)
			switch(prayer){
			case ENHANCE_MELEE_1:
			{
				amount *= 1.05;
				break;
			}
			case ENHANCE_MELEE_2:
			{
				amount *= 1.1;
				break;
			}
			case ENHANCE_MELEE_3:
			{
				amount *= 1.15;
				break;
			}
			case ENHANCE_MELEE_4:
			{
				amount *= 1.25;
				break;
			}
			default:
				break;
			}
		return amount;
	}

	public static float handleEnhanceRange(float amount, final EnumSet<EnumPrayers> prayers){
		amount = PrayerHelper.handlePotency(amount, prayers);
		for(final EnumPrayers prayer:prayers)
			switch(prayer){
			case ENHANCE_RANGE_1:
			{
				amount *= 1.05;
				break;
			}
			case ENHANCE_RANGE_2:
			{
				amount *= 1.1;
				break;
			}
			case ENHANCE_RANGE_3:
			{
				amount *= 1.15;
				break;
			}
			case ENHANCE_RANGE_4:
			{
				amount *= 1.25;
				break;
			}
			default:
				break;
			}
		return amount;
	}

	public static float handleEnhanceMagic(float amount, final EnumSet<EnumPrayers> enumSet){
		amount = PrayerHelper.handlePotency(amount, enumSet);
		for(final EnumPrayers prayer:enumSet)
			switch(prayer){
			case ENHANCE_MAGIC_1:
			{
				amount *= 1.05;
				break;
			}
			case ENHANCE_MAGIC_2:
			{
				amount *= 1.1;
				break;
			}
			case ENHANCE_MAGIC_3:
			{
				amount *= 1.15;
				break;
			}
			case ENHANCE_MAGIC_4:
			{
				amount *= 1.25;
				break;
			}
			default:
				break;
			}
		return amount;
	}

}
