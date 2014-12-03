package me.superckl.prayercraft.common.utility;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.Prayers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PrayerHelper {

	public static List<Prayers> fromNBT(final NBTTagCompound comp){
		final List<Prayers> list = new ArrayList<Prayers>();
		final int i = 0;
		while(comp.hasKey(Integer.toString(i)))
			list.add(Prayers.getById(comp.getString(Integer.toString(i))));
		return list;
	}

	public static NBTTagCompound toNBT(final List<Prayers> list){
		final NBTTagCompound comp = new NBTTagCompound();
		for(int i = 0; i < list.size(); i++)
			comp.setString(Integer.toString(i), list.get(i).getId());
		return comp;
	}

	public static boolean hasConflictions(final List<Prayers> list){
		boolean hasOverhead = false;
		for(final Prayers prayer:list)
			if(prayer.isOverhead()){
				if(hasOverhead)
					return true;
				hasOverhead = true;
			}
		return false;
	}

	public static int calculateXP(final int level){
		int xp = 0;
		for(int i = 1; i < level; i++)
			xp += Math.pow(2D, (i)/7D);
		return xp*75;
	}

	public static List<Prayers> getActivePrayers(final EntityLivingBase entity){
		if(entity instanceof EntityPlayer)
			return ((PrayerExtendedProperties)((EntityPlayer)entity).getExtendedProperties("prayer")).getActivePrayers();
		return new ArrayList<Prayers>();
	}

}
