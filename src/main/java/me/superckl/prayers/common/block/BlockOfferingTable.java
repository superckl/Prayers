package me.superckl.prayers.common.block;

import java.util.Random;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.prayer.Altar;
import me.superckl.prayers.common.reference.ModAchievements;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockOfferingTable extends BlockPrayers implements ITileEntityProvider{

	public BlockOfferingTable() {
		super(Material.rock);
		this.setBlockName("offeringtable").setStepSound(Block.soundTypeStone).setCreativeTab(ModTabs.TAB_PRAYER_BLOCKS);
		this.setHarvestLevel("pickaxe", 1, 0);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new TileEntityOfferingTable();
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int meta, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_)
	{
		final TileEntity te = world.getTileEntity(x, y, z);
		if((te == null) || ((te instanceof TileEntityOfferingTable) == false))
			return false;
		final TileEntityOfferingTable table = (TileEntityOfferingTable) te;
		if((player.getHeldItem() != null) && (player.getHeldItem().getItem() == ModItems.bottle) && FluidContainerRegistry.containsFluid(player.getHeldItem(), new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4))
				&& (table.getCurrentItem() != null) && (table.getCurrentItem().getItem() == ModItems.basicBone) && (table.getCurrentItem().getItemDamage() == 3)){
			if(table.getCurrentItem().hasTagCompound() && table.getCurrentItem().getTagCompound().getBoolean("soaked"))
				return false;
			NBTTagCompound comp;
			if(table.getCurrentItem().hasTagCompound())
				comp = table.getCurrentItem().getTagCompound();
			else{
				comp = new NBTTagCompound();
				table.getCurrentItem().setTagCompound(comp);
			}
			comp.setBoolean("soaked", true);
		}else if(table.getCurrentItem() != null){
			player.inventory.addItemStackToInventory(table.getCurrentItem());
			final ItemStack filledBottle = ModFluids.filledHolyBottle();
			if(!player.worldObj.isRemote && table.getCurrentItem().isItemEqual(filledBottle) && ItemStack.areItemStackTagsEqual(table.getCurrentItem(), filledBottle))
				player.addStat(ModAchievements.FIRST_STEPS, 1);
			table.setCurrentItem(null, player);
		}else if((player.getHeldItem() == null) && (table.getAltar() != null) && table.getAltar().isActivated()){
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			final float diff = prop.getMaxPrayerPoints()-prop.getPrayerPoints();
			float toRecharge = table.getAltar().onRechargePlayer(diff, player, false);
			if((diff <= 0) || (toRecharge <= 0))
				return false;
			toRecharge = table.getAltar().onRechargePlayer(diff, player, true);
			prop.setPrayerPoints(prop.getPrayerPoints()+toRecharge);
			return true;
		}else if((player.getHeldItem() == null) && (table.getAltar() == null)){
			final Altar altar = new Altar(table);
			if(altar.determineBlocks(world))
				altar.getContributors().put(player.getGameProfile().getId(), false);
		}else if(player.getHeldItem() != null){
			final ItemStack clone = player.getHeldItem().copy();
			clone.stackSize = 1;
			table.setCurrentItem(clone, player);
			if(!player.capabilities.isCreativeMode && (player.getHeldItem() != null))
				player.getHeldItem().stackSize--;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
		final TileEntity ent = world.getTileEntity(x, y, z);
		if((ent == null) || ((ent instanceof TileEntityOfferingTable) == false))
			return;
		final TileEntityOfferingTable te = (TileEntityOfferingTable) ent;
		if(te.getAltar() == null)
			return;
		if((te.getCurrentRecipe() != null) && te.getCurrentRecipe().isCrafting(te))
			Prayers.getProxy().renderEffect("waterBless", world, x ,y ,z, rand);
		if(te.getAltar().isInRitual() && (te.getCurrentItem() == null)){
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

	/*@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_)
	{
		final TileEntity ent = world.getTileEntity(x, y, z);
		if((ent == null) || ((ent instanceof TileEntityAltar) == false) || player.isSneaking())
			return false;
		final TileEntityAltar te = (TileEntityAltar) ent;
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
		}else if((player.getHeldItem() == null) && te.isActivated()){
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			final float diff = prop.getMaxPrayerPoints()-prop.getPrayerPoints();
			float toRecharge = te.onRechargePlayer(diff, player, false);
			if((diff <= 0) || (toRecharge <= 0))
				return false;
			toRecharge = te.onRechargePlayer(diff, player, true);
			prop.setPrayerPoints(prop.getPrayerPoints()+toRecharge);
			return true;
		}else if(player.getHeldItem() != null){
			final ItemStack clone = player.getHeldItem().copy();
			clone.stackSize = 1;
			te.setCurrentItem(clone, player);
			if(!player.capabilities.isCreativeMode && (player.getHeldItem() != null))
				player.getHeldItem().stackSize--;
		}
		return true;
	}

	@Override
	public void onBlockClicked(final World world, final int x, final int y, final int z, final EntityPlayer player) {
		final TileEntity ent = world.getTileEntity(x, y, z);
		if((ent == null) || ((ent instanceof TileEntityAltar) == false) || !player.isSneaking())
			return;
		final TileEntityAltar te = (TileEntityAltar) ent;
		if(te.getMaxPrayerPoints() <= te.getPrayerPoints())
			return;
		float diff = Math.min(100F, te.getMaxPrayerPoints()-te.getPrayerPoints());
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		if(prop.getPrayerPoints() <= 0)
			return;
		if(prop.getPrayerPoints() < diff)
			diff = prop.getPrayerPoints();
		te.setPrayerPoints(te.getPrayerPoints()+diff);
		prop.setPrayerPoints(prop.getPrayerPoints()-diff);
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
		return new TileEntityAltar(false);
	}*/

}
