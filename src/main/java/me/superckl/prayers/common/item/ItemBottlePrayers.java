package me.superckl.prayers.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBottlePrayers extends ItemFluidContainerPrayers{

	private final Map<String, IIcon> icons = new HashMap<String, IIcon>();

	public ItemBottlePrayers() {
		super(0, FluidContainerRegistry.BUCKET_VOLUME/4);
		this.setMaxStackSize(1).setUnlocalizedName("bottleprayers").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true);
	}

	@Override
	public Item getContainerItem() {
		return Items.glass_bottle;
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs creativeTabs, final List list)
	{
		list.add(ModFluids.filledHolyBottle());
	}

	@Override
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.icons.put("holywater", iconRegister.registerIcon(ModData.MOD_ID+":holywaterbottle"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@Override
	public IIcon getIcon(final ItemStack itemStack, final int renderPass)
	{
		final FluidStack fluid = this.getFluid(itemStack);

		if ((fluid != null) && (fluid.amount != 0))
		{
			final IIcon icon = this.icons.get(fluid.getFluid().getName());

			if (icon != null) return icon;
		}

		return Items.glass_bottle.getIconFromDamage(0);
	}

}
