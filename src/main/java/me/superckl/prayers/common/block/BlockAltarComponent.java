package me.superckl.prayers.common.block;

import java.util.List;

import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAltarComponent extends BlockPrayers{

	public BlockAltarComponent() {
		super(Material.rock);
		this.setBlockName("altarcomponent").setStepSound(Block.soundTypeStone).setCreativeTab(ModTabs.TAB_PRAYER_BLOCKS).setHarvestLevel("pickaxe", 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(final Item item, final CreativeTabs tab, final List list)
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
	}

	@Override
	public int damageDropped(final int meta) {
		return meta;
	}

	private IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(final IIconRegister register){
		/*this.icons = new IIcon[2][3];
		this.icons[0][0] = register.registerIcon(ModData.MOD_ID+":basicbenchbottom");
		this.icons[0][1] = register.registerIcon(ModData.MOD_ID+":basicbenchtop");
		this.icons[0][2] = register.registerIcon(ModData.MOD_ID+":basicbenchside");

		this.icons[1][0] = register.registerIcon(ModData.MOD_ID+":interbenchbottom");
		this.icons[1][1] = register.registerIcon(ModData.MOD_ID+":interbenchtop");
		this.icons[1][2] = register.registerIcon(ModData.MOD_ID+":interbenchside");*/
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta)
	{
		return this.icons[meta][Math.min(side, 2)];
	}*/

}
