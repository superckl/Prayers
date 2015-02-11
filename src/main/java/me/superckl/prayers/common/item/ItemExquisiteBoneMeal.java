package me.superckl.prayers.common.item;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemExquisiteBoneMeal extends ItemPrayers{

	public ItemExquisiteBoneMeal() {
		this.setUnlocalizedName("exquisitebonemeal").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setMaxDamage(0);
	}

	@Override
	public boolean onItemUse(final ItemStack stack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int meta, final float p_77648_8_, final float p_77648_9_, final float p_77648_10_)
	{
		if (!player.canPlayerEdit(x, y, z, meta, stack))
			return false;
		else if(ItemDye.applyBonemeal(stack, world, x, y, z, player) && ItemDye.applyBonemeal(stack, world, x, y, z, player))
		{
			stack.stackSize++;
			if (!world.isRemote)
				world.playAuxSFX(2005, x, y, z, 0);
			return true;
		}
		return false;
	}

	@Override
	public void registerIcons(final IIconRegister register) {
		this.itemIcon = register.registerIcon(ModData.MOD_ID+":exquisitebonemeal");
	}

}
