package me.superckl.prayers.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
import me.superckl.prayers.common.utility.ChatHelper;
import me.superckl.prayers.network.MessageUpdatePrayers;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemPrayerTome extends ItemPrayers{

	public ItemPrayerTome() {
		this.setMaxStackSize(1).setCreativeTab(ModTabs.TAB_PRAYER_TOMES).setHasSubtypes(true).setUnlocalizedName("prayertome");
	}

	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean bool) {
		if(stack.hasTagCompound()){
			final NBTTagCompound comp = stack.getTagCompound();
			if(comp.hasKey("prayer"))
				list.add(EnumPrayers.getById(comp.getString("prayer")).getDisplayName());
		}
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack itemStack, final World world, final EntityPlayer player)
	{
		if(world.isRemote || !itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("prayer"))
			return itemStack;
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		final String id = itemStack.getTagCompound().getString("prayer");
		if(prop.getUnlockedPrayers().contains(id))
			return itemStack;
		prop.getUnlockedPrayers().add(id);
		ChatHelper.sendFormattedDoubleMessage(player, "msg.whisper.text", ChatHelper.createTranslatedChatWithStyle("msg.newprayer.text", new ChatStyle().setItalic(false), EnumChatFormatting.getTextWithoutFormattingCodes(EnumPrayers.getById(id).getDisplayName())),
				new ChatStyle().setColor(EnumChatFormatting.RED).setItalic(true));
		if (!player.capabilities.isCreativeMode)
			--itemStack.stackSize;
		if(player instanceof EntityPlayerMP){
			final NBTTagCompound comp = new NBTTagCompound();
			prop.saveNBTData(comp);
			ModData.PRAYER_UPDATE_CHANNEL.sendTo(new MessageUpdatePrayers(comp), (EntityPlayerMP) player);
		}
		return itemStack;
	}

	@Override
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(ModData.MOD_ID+":prayertome");
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs creativeTabs, final List list)
	{
		final List<EnumPrayers> prayers = new ArrayList<EnumPrayers>(Arrays.asList(EnumPrayers.values()));
		final Iterator<EnumPrayers> it = prayers.iterator();
		while(it.hasNext())
			if(!it.next().isRequiresTome())
				it.remove();
		final ItemStack stack = new ItemStack(item);
		ItemStack temp;
		for(final EnumPrayers prayer:prayers){
			temp = stack.copy();
			final NBTTagCompound comp = new NBTTagCompound();
			comp.setString("prayer", prayer.getId());
			temp.setTagCompound(comp);
			list.add(temp);
		}
	}



}
