package me.superckl.prayers.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.TileEntityAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class AltarRenderer extends TileEntityRenderer<TileEntityAltar>{

	private final ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();

	public AltarRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(final TileEntityAltar tileEntityIn, final float partialTicks, final MatrixStack matrixStackIn,
			final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		if(tileEntityIn.getAltarItem().isEmpty())
			return;
		matrixStackIn.push();
		final Vector3f renderLoc = new Vector3f(0.5F, 1, 0.5F);
		matrixStackIn.translate(renderLoc.getX(), renderLoc.getY(), renderLoc.getZ());

		final Direction dir = tileEntityIn.getItemDirection();
		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90F));
		matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-90F));

		matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90+dir.getHorizontalAngle()));
		final float current = tileEntityIn.getItemTicks();
		final float max = tileEntityIn.getReqTicks();
		final float scale = 0.6F*(current == 0? 1:MathHelper.lerp(partialTicks, 1-(current-1)/max, 1-current/max));
		matrixStackIn.scale(scale, scale, scale);
		this.itemRender.renderItem(tileEntityIn.getAltarItem(), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
		matrixStackIn.pop();
	}

}
