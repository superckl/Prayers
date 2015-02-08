package me.superckl.prayers.common.block;

import java.util.List;

import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

}
