package me.superckl.prayers.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.CraftingStandTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class CraftingStandRenderer extends TileEntityRenderer<CraftingStandTileEntity>{

	public CraftingStandRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(final CraftingStandTileEntity tileEntityIn, final float partialTicks, final MatrixStack matrixStackIn,
			final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		for (final int i:CraftingStandTileEntity.slotToDir.keySet()) {
			final ItemStack stack = tileEntityIn.getStackInSlot(i);
			if(stack.isEmpty())
				continue;
			matrixStackIn.push();
			final Direction dir = CraftingStandTileEntity.slotToDir.get(i);
			final Vector3f offset = new Vector3f(0.5F+dir.getXOffset()*5.5F/16F, 2F/16F, 0.5F+dir.getZOffset()*5.5F/16F);
			matrixStackIn.translate(offset.getX(), offset.getY(), offset.getZ());
			matrixStackIn.scale(0.3F, 0.3F, 0.3F);
			RenderHelper.renderFloatingItemStack(matrixStackIn, bufferIn, partialTicks, combinedLightIn, combinedOverlayIn, stack);
			matrixStackIn.pop();
		}

	}

}
