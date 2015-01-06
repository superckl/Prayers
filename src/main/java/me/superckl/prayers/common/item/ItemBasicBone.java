package me.superckl.prayers.common.item;

import java.util.List;
import java.util.Random;

import me.superckl.prayers.common.entity.item.EntityCleaningDirtyBone;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
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

public class ItemBasicBone extends ItemPrayers{

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
			if(comp.getBoolean("soaked"))
				list.add("Soaked");
		}
	}

	@Override
	protected boolean isNameDamageReliant() {
		return true;
	}

	@Override
	public IIcon getIconFromDamage(final int meta) {
		return this.icons[meta+1];
	}



	@Override
	public IIcon getIcon(final ItemStack stack, final int pass) {
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("soaked"))
			return this.icons[0];
		else
			return super.getIcon(stack, pass);
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
		this.icons = new IIcon[] {register.registerIcon(ModData.MOD_ID+":wetbones"), register.registerIcon(ModData.MOD_ID+":smallbones"),
				register.registerIcon(ModData.MOD_ID+":largebones"), register.registerIcon(ModData.MOD_ID+":dirtybones"),
				register.registerIcon(ModData.MOD_ID+":exquisitebones")};
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
