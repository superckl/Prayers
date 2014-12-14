package me.superckl.prayercraft.common.utility;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.IPrayerAltar;
import me.superckl.prayercraft.common.prayer.Prayers;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PrayerHelper {

	public static List<Prayers> fromNBT(final NBTTagCompound comp){
		final List<Prayers> list = new ArrayList<Prayers>();
		int i = 0;
		while(comp.hasKey(Integer.toString(i)))
			list.add(Prayers.getById(comp.getString(Integer.toString(i++))));
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
			xp += Math.pow(2D, (i)/10D);
		return xp*35;
	}

	public static List<Prayers> getActivePrayers(final EntityLivingBase entity){
		if(entity instanceof EntityPlayer)
			return ((PrayerExtendedProperties)((EntityPlayer)entity).getExtendedProperties("prayer")).getActivePrayers();
		return new ArrayList<Prayers>();
	}

	public static float handlePotency(float amount, final List<Prayers> prayers){
		for(final Prayers prayer:prayers)
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

	public static float handleEnhanceMelee(float amount, final List<Prayers> prayers){
		amount = PrayerHelper.handlePotency(amount, prayers);
		for(final Prayers prayer:prayers)
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

	public static float handleEnhanceRange(float amount, final List<Prayers> prayers){
		amount = PrayerHelper.handlePotency(amount, prayers);
		for(final Prayers prayer:prayers)
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

	public static float handleEnhanceMagic(float amount, final List<Prayers> prayers){
		amount = PrayerHelper.handlePotency(amount, prayers);
		for(final Prayers prayer:prayers)
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
	
	/**
	 * Attempts to find a prayer altar at the given coordinates
	 */
	public static IPrayerAltar findAltar(World world, int x, int y, int z){
		Block block = world.getBlock(x, y, z);
		if(block instanceof IPrayerAltar)
			return (IPrayerAltar) block;
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IPrayerAltar)
			return (IPrayerAltar) te;
		return null;
	}

}
