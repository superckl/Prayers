package me.superckl.prayercraft.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModFluids;
import me.superckl.prayercraft.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ItemBottlePrayerCraft extends ItemFluidContainerPrayerCraft{

	private final Map<String, IIcon> icons = new HashMap<String, IIcon>();
	
	public ItemBottlePrayerCraft() {
		super(0, FluidContainerRegistry.BUCKET_VOLUME/4);
		this.setMaxStackSize(1).setUnlocalizedName("bottleprayercraft").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true);
	}

	@Override
	public Item getContainerItem() {
		return Items.glass_bottle;
	}
	
	@Override
	public void getSubItems(final Item item, final CreativeTabs creativeTabs, final List list)
	{
		ItemStack fluid = new ItemStack(item);

		this.fill(fluid, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4), true);
		list.add(fluid);
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

		if (fluid != null && fluid.amount != 0)
		{
			final IIcon icon = this.icons.get(fluid.getFluid().getName());

			if (icon != null) return icon;
		}

		return Items.glass_bottle.getIconFromDamage(0);
	}
	
}
