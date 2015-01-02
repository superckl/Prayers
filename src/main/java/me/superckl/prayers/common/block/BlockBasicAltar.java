package me.superckl.prayers.common.block;

import java.util.Random;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.entity.tile.TileEntityBasicAltar;
import me.superckl.prayers.common.prayer.IPrayerAltar;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModTabs;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PrayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
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
		LogHelper.info(te.isItemValid(player.getHeldItem()));
		if(te.getCurrentItem() != null){
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
