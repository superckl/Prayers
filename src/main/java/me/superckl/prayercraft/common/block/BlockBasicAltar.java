package me.superckl.prayercraft.common.block;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.entity.tile.TileEntityBasicAltar;
import me.superckl.prayercraft.common.prayer.IBuryable;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModTabs;
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

public class BlockBasicAltar extends BlockPrayerCraft implements ITileEntityProvider{

	public BlockBasicAltar() {
		super(Material.rock);
		this.setBlockName("basicaltar").setStepSound(Block.soundTypeStone).setCreativeTab(ModTabs.TAB_PRAYER_BLOCKS);
		this.setHarvestLevel("pickaxe", 1, 0);
	}



	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_)
	{
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if ((tileEntity == null) || !(tileEntity instanceof TileEntityBasicAltar) || player.isSneaking())
			return false;
		if(((TileEntityBasicAltar)tileEntity).isActivated()){
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			prop.setPrayerPoints(prop.getMaxPrayerPoints());
			final ItemStack stack = player.getHeldItem();
			if((stack != null) && (stack.getItem() instanceof IBuryable)){
				final int xp = ((IBuryable)stack.getItem()).getXPFromStack(stack);
				prop.addXP((int) (xp*1.5));
				stack.stackSize--;
			}
		}
		return true;
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