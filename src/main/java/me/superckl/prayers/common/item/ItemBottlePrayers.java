package me.superckl.prayers.common.item;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBottlePrayers extends ItemFluidContainerPrayers{

	public ItemBottlePrayers() {
		super(0, FluidContainerRegistry.BUCKET_VOLUME/4);
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

}
