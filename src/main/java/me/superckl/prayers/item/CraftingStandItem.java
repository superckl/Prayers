package me.superckl.prayers.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class CraftingStandItem extends BlockItem{

	public CraftingStandItem(final Block blockIn, final Properties builder) {
		super(blockIn, builder);
	}

	/*
	 * @Override protected BlockState getStateForPlacement(final BlockItemUseContext
	 * context) { final BlockState state =
	 * context.getWorld().getBlockState(context.getPos()); if(!(state.getBlock()
	 * instanceof CraftingStandBlock)) return super.getStateForPlacement(context);
	 * final CraftingStandBlock block = (CraftingStandBlock) state.getBlock();
	 * return block.findNextPlacement(state, context); }
	 */

}
