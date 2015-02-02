package me.superckl.prayers.client.render;

import me.superckl.prayers.common.reference.RenderData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockOfferingTable implements ISimpleBlockRenderingHandler{

	@Override
	public void renderInventoryBlock(final Block block, final int metadata, final int modelId,
			final RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z,
			final Block block, final int modelId, final RenderBlocks renderer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(final int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return RenderData.BlockIDs.OFFERING_TABLE;
	}

}
