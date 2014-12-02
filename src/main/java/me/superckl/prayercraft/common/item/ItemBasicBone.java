package me.superckl.prayercraft.common.item;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.IBuryable;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBasicBone extends ItemPrayerCraft implements IBuryable{

	private final Random random = new Random();
	
	public ItemBasicBone() {
		this.setMaxStackSize(16).setUnlocalizedName("basicbone").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS);
	}

	@Override
	protected boolean isNameDamageReliant() {
		return true;
	}

	@Override
	public IIcon getIconFromDamage(final int meta) {
		return this.icons[meta];
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		list.add(new ItemStack(item, 1, 1));
		super.getSubItems(item, tab, list);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	@Override
	public void registerIcons(final IIconRegister register) {
		this.icons = new IIcon[] {register.registerIcon(ModData.MOD_ID+":smallbones"),
				register.registerIcon(ModData.MOD_ID+":largebones")};
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(player.isSneaking())
			return stack;
		int xp = this.getXPFromStack(stack);
		PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		prop.addXP(xp);
		stack.stackSize--;
		return stack.stackSize <= 0 ? null:stack;
	}

	@Override
	public int getXPFromStack(ItemStack stack) {
		switch(stack.getItemDamage()){
			case 0:
				return this.random.nextInt(4)+6;
			case 1:
				return this.random.nextInt(6)+15;
		}
		return 0;
	}



}
