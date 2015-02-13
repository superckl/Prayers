package me.superckl.prayers.common.item;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHolyWaterBottle extends ItemPrayers{

	public ItemHolyWaterBottle() {
		this.setMaxStackSize(1).setUnlocalizedName("bottleprayers").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS);
	}

	@Override
	public Item getContainerItem() {
		return Items.glass_bottle;
	}

	@Override
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(ModData.MOD_ID+":holywaterbottle");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@Override
	public boolean hasEffect(final ItemStack i){
		return true;
	}

	@Override
	public boolean itemInteractionForEntity(final ItemStack stack, final EntityPlayer player, final EntityLivingBase living) {
		if(living.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD)
			return super.itemInteractionForEntity(stack, player, living);
		if(living.attackEntityFrom(new EntityDamageSource("holywater", player).setMagicDamage().setDamageIsAbsolute(), 20F)){
			stack.stackSize--;
			return true;
		}
		return false;
	}

}
