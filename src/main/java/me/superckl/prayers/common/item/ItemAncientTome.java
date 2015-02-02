package me.superckl.prayers.common.item;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
import me.superckl.prayers.common.reference.RenderData.GUIIDs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAncientTome extends ItemPrayers{

	public ItemAncientTome() {
		this.setMaxStackSize(1).setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setUnlocalizedName("ancienttome");
	}

	@Override
	public void registerIcons(final IIconRegister register) {
		this.itemIcon = register.registerIcon(ModData.MOD_ID+":ancienttome");
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
		if(!world.isRemote)
			player.openGui(Prayers.getInstance(), GUIIDs.ANCIENT_TOME, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return stack;
	}

}
