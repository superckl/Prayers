package me.superckl.prayers.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.entity.CraftingStandTileEntity;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
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
			ItemStack stack = tileEntityIn.getItemHandlerForSide(CraftingStandTileEntity.slotToDir.get(i)).getStackInSlot(0);
			final Direction dir = CraftingStandTileEntity.slotToDir.get(i);
			float alpha = 1;
			if(stack.isEmpty()) {
				if(dir != Direction.UP)
					continue;
				if(tileEntityIn.isCrafting()) {
					alpha = tileEntityIn.getCraftingProgress();
					stack = tileEntityIn.getActiveRecipe().getResultItem();
				}
			}
			if(dir != Direction.UP && tileEntityIn.isCrafting() && tileEntityIn.willCraftingConsume(i))
				alpha = 1-tileEntityIn.getCraftingProgress();
			this.renderStandItem(stack, matrixStackIn, dir, alpha, partialTicks, bufferIn, combinedLightIn, combinedOverlayIn);
		}
	}
	
	public void renderStandItem(ItemStack stack, MatrixStack matrixStackIn, Direction dir, float progress,
			final float partialTicks, final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		matrixStackIn.pushPose();
		final Vector3f offset = new Vector3f(0.5F+dir.getStepX()*5.5F/16F, 2F/16F, 0.5F+dir.getStepZ()*5.5F/16F);
		matrixStackIn.translate(offset.x(), offset.y(), offset.z());
		matrixStackIn.scale(0.3F, 0.3F, 0.3F);
		RenderType type = stack.getItem() instanceof BlockItem ? Atlases.translucentCullBlockSheet():null;
		RenderHelper.renderFloatingItemStack(matrixStackIn, new RenderHelper.AlphaMultipliedVertexBuffer(bufferIn, type, progress),
				partialTicks, combinedLightIn, combinedOverlayIn, stack);
		matrixStackIn.popPose();
	}

}
