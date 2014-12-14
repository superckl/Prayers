package me.superckl.prayercraft.common.item;

import java.util.List;
import java.util.Random;

import me.superckl.prayercraft.common.entity.item.EntityCleaningDirtyBone;
import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.IBuryable;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBasicBone extends ItemPrayerCraft implements IBuryable{

	private final Random random = new Random();

	public ItemBasicBone() {
		this.setMaxStackSize(16).setUnlocalizedName("basicbone").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true);
	}



	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean bool) {
		if(stack.hasTagCompound()){
			final NBTTagCompound comp = stack.getTagCompound();
			if(comp.hasKey("progress"))
				list.add("Cleaning progress: "+(int)(((comp.getInteger("progress"))/1200F)*100F)+"%");
		}
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
	public int getMetadata(final int meta) {
		return meta;
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		super.getSubItems(item, tab, list);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@Override
	public void registerIcons(final IIconRegister register) {
		this.icons = new IIcon[] {register.registerIcon(ModData.MOD_ID+":smallbones"),
				register.registerIcon(ModData.MOD_ID+":largebones"), register.registerIcon(ModData.MOD_ID+":dirtybones"),
				register.registerIcon(ModData.MOD_ID+":exquisitebones")};
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
		if(player.isSneaking())
			return stack;
		int xp = this.getXPFromStack(stack);
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		xp/=Math.max(1, Math.log10(Math.max(1, prop.getPrayerLevel()-15))*1.17D); //The gods grow tired of your lazy offerings!!!
		prop.addXP(xp);
		stack.stackSize--;
		return stack;
	}

	@Override
	public int getXPFromStack(final ItemStack stack) {
		switch(stack.getItemDamage()){
		case 0:
			return this.random.nextInt(4)+6;
		case 1:
			return this.random.nextInt(6)+15;
		case 2:
			return this.random.nextInt(4)+6;
		case 3:
			return 30;
		}
		return 0;
	}

	@Override
	public Entity createEntity(final World world, final Entity location, final ItemStack itemstack) {
		if(itemstack.getItemDamage() == 2){
			final EntityCleaningDirtyBone entity = new EntityCleaningDirtyBone(world, location.posX, location.posY, location.posZ, new ItemStack(this, itemstack.stackSize, 2));
			entity.motionX = location.motionX;
			entity.motionY = location.motionY;
			entity.motionZ = location.motionZ;
			final NBTTagCompound comp = itemstack.hasTagCompound() ? itemstack.getTagCompound():new NBTTagCompound();
			entity.getEntityItem().setTagCompound(comp);
			if(comp.hasKey("progress"))
				entity.setProgress(comp.getInteger("progress"));
			if(location instanceof EntityItem)
				entity.delayBeforeCanPickup = ((EntityItem)location).delayBeforeCanPickup;
			return entity;
		}
		return super.createEntity(world, location, itemstack);
	}

	@Override
	public boolean hasCustomEntity(final ItemStack stack) {
		return stack.getItemDamage() == 2;
	}

}
