package me.superckl.prayers.item;

import me.superckl.prayers.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;

public class CraftingStandItem extends BlockItem{

	public CraftingStandItem(Block blockIn, Properties builder) {
		super(blockIn, builder);
	}

	@Override
	protected BlockState getStateForPlacement(BlockItemUseContext context) {
		LogHelper.info(context.getHitVec());
		return super.getStateForPlacement(context);
	}
	
	
	
}
