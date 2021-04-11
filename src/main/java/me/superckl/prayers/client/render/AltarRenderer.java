package me.superckl.prayers.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.entity.AltarTileEntity;
import me.superckl.prayers.client.ClientHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class AltarRenderer extends TileEntityRenderer<AltarTileEntity>{

	public AltarRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(final AltarTileEntity tileEntityIn, final float partialTicks, final MatrixStack matrixStackIn,
			final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		if(tileEntityIn.getAltarItem().isEmpty())
			return;
		matrixStackIn.pushPose();
		final Vector3f renderLoc = new Vector3f(0.5F, 1, 0.5F);
		matrixStackIn.translate(renderLoc.x(), renderLoc.y(), renderLoc.z());

		final Direction dir = tileEntityIn.getItemDirection();
		matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90F));
		matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-90F));

		matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90+dir.toYRot()));
		final float current = tileEntityIn.getItemTicks();
		final float previous = tileEntityIn.getItemTicks0();
		final float max = tileEntityIn.getReqTicks();
		final float scale = 0.6F*(current == 0? 1:MathHelper.lerp(partialTicks, 1-previous/max, 1-current/max));
		matrixStackIn.scale(scale, scale, scale);
		ClientHelper.getItemRenderer().renderStatic(tileEntityIn.getAltarItem(), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
		matrixStackIn.popPose();
	}

}
