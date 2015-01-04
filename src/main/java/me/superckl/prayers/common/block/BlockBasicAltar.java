package me.superckl.prayers.common.block;

import java.util.Random;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.entity.tile.TileEntityBasicAltar;
import me.superckl.prayers.common.prayer.IPrayerAltar;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.reference.ModTabs;
import me.superckl.prayers.common.utility.PrayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasicAltar extends BlockPrayers implements ITileEntityProvider{

	public BlockBasicAltar() {
		super(Material.rock);
		this.setBlockName("basicaltar").setStepSound(Block.soundTypeStone).setCreativeTab(ModTabs.TAB_PRAYER_BLOCKS);
		this.setHarvestLevel("pickaxe", 1, 0);
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_)
	{
		final IPrayerAltar altar = PrayerHelper.findAltar(world, x, y, z);
		if((altar == null) || player.isSneaking())
			return false;
		final TileEntityBasicAltar te = (TileEntityBasicAltar) altar;
		if((player.getHeldItem() != null) && (player.getHeldItem().getItem() == ModItems.bottle) && FluidContainerRegistry.containsFluid(player.getHeldItem(), new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4))
				&& (te.getCurrentItem() != null) && (te.getCurrentItem().getItem() == ModItems.basicBone) && (te.getCurrentItem().getItemDamage() == 3)){
			if(te.getCurrentItem().hasTagCompound() && te.getCurrentItem().getTagCompound().getBoolean("soaked"))
				return false;
			NBTTagCompound comp;
			if(te.getCurrentItem().hasTagCompound())
				comp = te.getCurrentItem().getTagCompound();
			else{
				comp = new NBTTagCompound();
				te.getCurrentItem().setTagCompound(comp);
			}
			comp.setBoolean("soaked", true);
		}else if(te.getCurrentItem() != null){
			player.inventory.addItemStackToInventory(te.getCurrentItem());
			te.setCurrentItem(null, player);
		}else if((player.getHeldItem() == null) && altar.isActivated()){
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			final float diff = prop.getMaxPrayerPoints()-prop.getPrayerPoints();
			float toRecharge = altar.onRechargePlayer(diff, player, false);
			if((diff <= 0) || (toRecharge <= 0))
				return false;
			toRecharge = altar.onRechargePlayer(diff, player, true);
			prop.setPrayerPoints(prop.getPrayerPoints()+toRecharge);
			return true;
		}else if(te.isItemValid(player.getHeldItem())){

			final ItemStack clone = player.getHeldItem() == null ? null:player.getHeldItem().copy();
			if(clone != null)
				clone.stackSize = 1;
			te.setCurrentItem(clone, player);
			if(!player.capabilities.isCreativeMode && (player.getHeldItem() != null))
				player.getHeldItem().stackSize--;

		}
		return true;
	}

	@Override
	public void onBlockClicked(final World world, final int x, final int y, final int z, final EntityPlayer player) {
		final IPrayerAltar altar = PrayerHelper.findAltar(world, x, y, z);
		if((altar == null) || !player.isSneaking())
			return;
		if(altar.getMaxPrayerPoints() <= altar.getPrayerPoints())
			return;
		float diff = Math.min(100F, altar.getMaxPrayerPoints()-altar.getPrayerPoints());
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		if(prop.getPrayerPoints() <= 0)
			return;
		if(prop.getPrayerPoints() < diff)
			diff = prop.getPrayerPoints();
		altar.setPrayerPoints(altar.getPrayerPoints()+diff);
		prop.setPrayerPoints(prop.getPrayerPoints()-diff);

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
		final IPrayerAltar altar = PrayerHelper.findAltar(world, x, y, z);
		if((altar == null) || ((altar instanceof TileEntityBasicAltar) == false))
			return;
		final TileEntityBasicAltar te = (TileEntityBasicAltar) altar;
		if(te.isBlessingWater())
			Prayers.getProxy().renderEffect("waterBless", world, x ,y ,z, rand);
		if(te.isInRitual() && (te.getCurrentItem() == null)){
			final double d0 = 0.0625D;

			for (int l = 0; l < 6; ++l)
			{
				double d1 = x + rand.nextFloat();
				double d2 = y + rand.nextFloat();
				double d3 = z + rand.nextFloat();

				if ((l == 0) && !world.getBlock(x, y + 1, z).isOpaqueCube())
					d2 = y + 1 + d0;

				if ((l == 1) && !world.getBlock(x, y - 1, z).isOpaqueCube())
					d2 = (y + 0) - d0;

				if ((l == 2) && !world.getBlock(x, y, z + 1).isOpaqueCube())
					d3 = z + 1 + d0;

				if ((l == 3) && !world.getBlock(x, y, z - 1).isOpaqueCube())
					d3 = (z + 0) - d0;

				if ((l == 4) && !world.getBlock(x + 1, y, z).isOpaqueCube())
					d1 = x + 1 + d0;

				if ((l == 5) && !world.getBlock(x - 1, y, z).isOpaqueCube())
					d1 = (x + 0) - d0;

				if ((d1 < x) || (d1 > (x + 1)) || (d2 < 0.0D) || (d2 > (y + 1)) || (d3 < z) || (d3 > (z + 1)))
					world.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	private IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(final IIconRegister register){
		this.icons = new IIcon[1][3];
		this.icons[0][0] = register.registerIcon(ModData.MOD_ID+":basicaltarbottom");
		this.icons[0][1] = register.registerIcon(ModData.MOD_ID+":basicaltartop");
		this.icons[0][2] = register.registerIcon(ModData.MOD_ID+":basicaltarside");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta)
	{
		return this.icons[meta][Math.min(side, 2)];
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new TileEntityBasicAltar(false);
	}

}
