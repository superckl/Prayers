package me.superckl.prayers.common.fluid.block;

import java.util.Random;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModFluids;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidHolyWater extends BlockFluidClassicPrayers{

	public BlockFluidHolyWater(final Fluid fluid, final Material material) {
		super(fluid, material);
		this.setBlockName("holywater");
	}

	@Override
	public IIcon getIcon(final int side, final int meta)
	{
		return (side != 0) && (side != 1) ? this.icons[1]:this.icons[0];
	}

	private final IIcon[] icons = new IIcon[2];

	@Override
	public void registerBlockIcons(final IIconRegister register) {
		this.icons[0] = register.registerIcon(ModData.MOD_ID+":holywater_still");
		this.icons[1] = register.registerIcon(ModData.MOD_ID+":holywater_flow");
		ModFluids.holyWater.setIcons(this.icons[0], this.icons[1]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(final IBlockAccess access, final int x, final int y, final int z, final int side)
	{
		final Material material = access.getBlock(x, y, z).getMaterial();
		return material == this.blockMaterial ? false : side == 1 ? true : super.shouldSideBeRendered(access, x, y, z, side);
	}

	@Override
	public boolean displaceIfPossible(final World world, final int x, final int y, final int z){
		if(world.getBlock(x, y, z).getMaterial() == Material.water)
			return false;
		return super.displaceIfPossible(world, x, y, z);
	}

	//Emulate vanilla water particles
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random random)
	{
		super.randomDisplayTick(world, x, y, z, random);
		int l;

		if (this.blockMaterial == Material.water)
		{
			if (random.nextInt(10) == 0)
			{
				l = world.getBlockMetadata(x, y, z);

				if ((l <= 0) || (l >= 8))
					world.spawnParticle("suspended", x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.0D, 0.0D);
			}

			for (l = 0; l < 0; ++l)
			{
				final int i1 = random.nextInt(4);
				int j1 = x;
				int k1 = z;

				if (i1 == 0)
					j1 = x - 1;

				if (i1 == 1)
					++j1;

				if (i1 == 2)
					k1 = z - 1;

				if (i1 == 3)
					++k1;

				if ((world.getBlock(j1, y, k1).getMaterial() == Material.air) && (world.getBlock(j1, y - 1, k1).getMaterial().blocksMovement() || world.getBlock(j1, y - 1, k1).getMaterial().isLiquid()))
				{
					final float f = 0.0625F;
					double d0 = x + random.nextFloat();
					final double d1 = y + random.nextFloat();
					double d2 = z + random.nextFloat();

					if (i1 == 0)
						d0 = x - f;

					if (i1 == 1)
						d0 = x + 1 + f;

					if (i1 == 2)
						d2 = z - f;

					if (i1 == 3)
						d2 = z + 1 + f;

					double d3 = 0.0D;
					double d4 = 0.0D;

					if (i1 == 0)
						d3 = -f;

					if (i1 == 1)
						d3 = f;

					if (i1 == 2)
						d4 = -f;

					if (i1 == 3)
						d4 = f;

					world.spawnParticle("splash", d0, d1, d2, d3, 0.0D, d4);
				}
			}
		}

		if ((this.blockMaterial == Material.water) && (random.nextInt(64) == 0))
		{
			l = world.getBlockMetadata(x, y, z);

			if ((l > 0) && (l < 8))
				world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "liquid.water", (random.nextFloat() * 0.25F) + 0.75F, (random.nextFloat() * 1.0F) + 0.5F, false);
		}

		double d5;
		double d6;
		double d7;

		if ((random.nextInt(10) == 0) && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement())
		{
			d5 = x + random.nextFloat();
			d6 = y - 1.05D;
			d7 = z + random.nextFloat();

			if (this.blockMaterial == Material.water)
				world.spawnParticle("dripWater", d5, d6, d7, 0.0D, 0.0D, 0.0D);
		}
	}

}
