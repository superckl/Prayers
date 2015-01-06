package me.superckl.prayers.common.utility;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.EnumPrayers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PrayerHelper {

	public static List<EnumPrayers> fromNBT(final NBTTagCompound comp){
		final List<EnumPrayers> list = new ArrayList<EnumPrayers>();
		int i = 0;
		while(comp.hasKey(Integer.toString(i)))
			list.add(EnumPrayers.getById(comp.getString(Integer.toString(i++))));
		return list;
	}

	public static NBTTagCompound toNBT(final List<EnumPrayers> list){
		final NBTTagCompound comp = new NBTTagCompound();
		for(int i = 0; i < list.size(); i++)
			comp.setString(Integer.toString(i), list.get(i).getId());
		return comp;
	}

	public static boolean hasConflictions(final List<EnumPrayers> list){
		boolean hasOverhead = false;
		for(final EnumPrayers prayer:list)
			if(prayer.isOverhead()){
				if(hasOverhead)
					return true;
				hasOverhead = true;
			}
		return false;
	}

	public static List<EnumPrayers> getActivePrayers(final EntityLivingBase entity){
		if(entity instanceof EntityPlayer)
			return ((PrayerExtendedProperties)((EntityPlayer)entity).getExtendedProperties("prayer")).getActivePrayers();
		return new ArrayList<EnumPrayers>();
	}

	public static float handlePotency(float amount, final List<EnumPrayers> prayers){
		for(final EnumPrayers prayer:prayers)
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

	public static float handleEnhanceMelee(float amount, final List<EnumPrayers> prayers){
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

	public static float handleEnhanceRange(float amount, final List<EnumPrayers> prayers){
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

	public static float handleEnhanceMagic(float amount, final List<EnumPrayers> prayers){
		amount = PrayerHelper.handlePotency(amount, prayers);
		for(final EnumPrayers prayer:prayers)
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
