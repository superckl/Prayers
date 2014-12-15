package me.superckl.prayers.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBucketPrayers extends ItemFluidContainerPrayers{

	private final Map<String, IIcon> icons = new HashMap<String, IIcon>();

	public ItemBucketPrayers() {
		super(0, FluidContainerRegistry.BUCKET_VOLUME);
		this.setMaxStackSize(1).setUnlocalizedName("bucketprayers").setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true);
	}

	@Override
	public Item getContainerItem() {
		return Items.bucket;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack itemStack, final World world, final EntityPlayer player)
	{
		final MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (movingobjectposition != null)
		{
			int i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;

			if (movingobjectposition.sideHit == 0)
				--j;

			if (movingobjectposition.sideHit == 1)
				++j;

			if (movingobjectposition.sideHit == 2)
				--k;

			if (movingobjectposition.sideHit == 3)
				++k;

			if (movingobjectposition.sideHit == 4)
				--i;

			if (movingobjectposition.sideHit == 5)
				++i;

			if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStack))
				return itemStack;

			if (this.tryPlaceContainedLiquid(itemStack, world, i, j, k) && !player.capabilities.isCreativeMode)
				return FluidContainerRegistry.EMPTY_BUCKET;
		}

		return itemStack;
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs creativeTabs, final List list)
	{
		final ItemStack fluid = new ItemStack(item);

		this.fill(fluid, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME), true);
		list.add(fluid);
	}

	@Override
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.icons.put("holywater", iconRegister.registerIcon(ModData.MOD_ID+":holywaterbucket"));
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

		return Items.bucket.getIconFromDamage(0);
	}

	public boolean tryPlaceContainedLiquid(final ItemStack itemStack, final World world, final int x, final int y, final int z)
	{
		final FluidStack fluid = this.getFluid(itemStack);

		if ((fluid == null) || (fluid.amount == 0))
			return false;
		else
		{
			final Material material = world.getBlock(x, y, z).getMaterial();
			final boolean isSolid = material.isSolid();

			if (!world.isAirBlock(x, y, z) && isSolid)
				return false;
			else
			{
				if (!world.isRemote && !isSolid && !material.isLiquid())
					world.func_147480_a(x, y, z, true);

				world.setBlock(x, y, z, fluid.getFluid().getBlock(), 0, 1 + 2);
			}

			return true;
		}
	}

	@Override
	public int fill(final ItemStack container, final FluidStack resource, final boolean doFill) {
		if(resource.amount < FluidContainerRegistry.BUCKET_VOLUME)
			return 0;
		return super.fill(container, resource, doFill);
	}

	@Override
	public FluidStack drain(final ItemStack container, final int maxDrain, final boolean doDrain) {
		if(maxDrain < FluidContainerRegistry.BUCKET_VOLUME)
			return null;
		return super.drain(container, maxDrain, doDrain);
	}

}
