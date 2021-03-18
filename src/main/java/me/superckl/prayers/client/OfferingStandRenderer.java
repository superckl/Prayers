package me.superckl.prayers.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.OfferingStandTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class OfferingStandRenderer extends TileEntityRenderer<OfferingStandTileEntity>{

	public OfferingStandRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(final OfferingStandTileEntity tileEntityIn, final float partialTicks, final MatrixStack matrixStackIn,
			final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		if(tileEntityIn.getItem(0).isEmpty())
			return;
		matrixStackIn.pushPose();
		final Vector3f renderLoc = new Vector3f(0.5F, 7/16F, 0.5F);
		matrixStackIn.translate(renderLoc.x(), renderLoc.y(), renderLoc.z());
		matrixStackIn.scale(0.4F, 0.4F, 0.4F);
		RenderHelper.renderFloatingItemStack(matrixStackIn, bufferIn, partialTicks, combinedLightIn, combinedOverlayIn, tileEntityIn.getItem(0));
		matrixStackIn.popPose();
	}

}
