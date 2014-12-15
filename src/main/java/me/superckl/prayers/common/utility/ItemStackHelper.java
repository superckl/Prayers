package me.superckl.prayers.common.utility;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemStackHelper {

	public static ItemStack makeBlankFirework(final byte flight){
		final ItemStack rocket = new ItemStack(Items.fireworks);
		final NBTTagCompound base = new NBTTagCompound();
		final NBTTagCompound fireTag = new NBTTagCompound();
		final NBTTagList expList = new NBTTagList();

		fireTag.setTag("Explosions", expList);
		fireTag.setByte("Flight", flight);
		base.setTag("Fireworks", fireTag);
		rocket.setTagCompound(base);
		return rocket;
	}

	public static ItemStack addExplosion(final ItemStack rocket, final byte twinkle, final byte trail, final byte type, final int ... colors){
		if(!rocket.hasTagCompound())
			return rocket;
		NBTTagCompound base = rocket.getTagCompound().getCompoundTag("Fireworks");
		if(base == null)
			base = new NBTTagCompound();
		NBTTagList list = base.getTagList("Explosions", NBT.TAG_COMPOUND);
		if(list == null)
			list = new NBTTagList();

		final NBTTagCompound exp = new NBTTagCompound();
		exp.setByte("Flicker", twinkle);
		exp.setByte("Trail", trail);
		exp.setByte("Type", type);
		exp.setIntArray("Colors", colors);
		list.appendTag(exp);

		base.setTag("Explosions", list);
		rocket.getTagCompound().setTag("Fireworks", base);
		return rocket;
	}

}
