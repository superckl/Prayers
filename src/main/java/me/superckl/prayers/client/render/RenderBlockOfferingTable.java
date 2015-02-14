package me.superckl.prayers.client.render;

import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.reference.RenderData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockOfferingTable implements ISimpleBlockRenderingHandler{

	private final TileEntityOfferingTable dummy = new TileEntityOfferingTable();

	@Override
	public void renderInventoryBlock(final Block block, final int metadata, final int modelId,
			final RenderBlocks renderer) {
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		GL11.glScalef(0.9F, 0.9F, 0.9F);
		TileEntityRendererDispatcher.instance.renderTileEntityAt(this.dummy, 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z,
			final Block block, final int modelId, final RenderBlocks renderer) {
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
