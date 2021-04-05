package me.superckl.prayers.client;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.ClientHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class RenderHelper {

	private static final Random random = new Random();

	public static void renderFloatingItemStack(final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn,
			final float partialTicks, final int combinedLightIn, final int combinedOverlayIn, final ItemStack itemstack) {
		final float f1 = MathHelper.sin((ClientHelper.getLevel().getGameTime() + partialTicks) / 10.0F) * 0.1F + 0.1F;
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.0D, f1 + 0.25F * .1, 0.0D);
		final float itemRotation = (ClientHelper.getLevel().getGameTime() + partialTicks) / 20.0F;
		matrixStackIn.mulPose(Vector3f.YP.rotation(itemRotation));

		final int j = RenderHelper.getModelCount(itemstack);
		final float f7 = -0.0F * (j - 1) * 0.5F;
		final float f8 = -0.0F * (j - 1) * 0.5F;
		final float f9 = -0.09375F * (j - 1) * 0.5F;
		matrixStackIn.translate(f7, f8, f9);

		final int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
		RenderHelper.random.setSeed(i);

		for(int k = 0; k < j; ++k) {
			matrixStackIn.pushPose();
			if (k > 0) {
				final float f12 = (RenderHelper.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
				final float f14 = (RenderHelper.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
				matrixStackIn.translate(f12, f14, 0.0D);
			}

			ClientHelper.getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
			matrixStackIn.popPose();
			matrixStackIn.translate(0.0, 0.0, 0.09375F);
		}
		matrixStackIn.popPose();
	}

	public static int getModelCount(final ItemStack stack) {
		int i = 1;
		if (stack.getCount() > 48)
			i = 5;
		else if (stack.getCount() > 32)
			i = 4;
		else if (stack.getCount() > 16)
			i = 3;
		else if (stack.getCount() > 1)
			i = 2;

		return i;
	}

	@RequiredArgsConstructor
	public static class AlphaMultipliedVertexBuffer implements IRenderTypeBuffer{

		private final IRenderTypeBuffer wrapped;
		private final RenderType type;
		private final float alphaMultiplier;

		@Override
		public IVertexBuilder getBuffer(RenderType type) {
			if(this.type != null)
				type = this.type;
			return new AlphaMultipliedVertexBuilder(this.wrapped.getBuffer(type), this.alphaMultiplier);
		}

	}

	@RequiredArgsConstructor
	public static class AlphaMultipliedVertexBuilder implements IVertexBuilder{

		private final IVertexBuilder builder;
		private final float alphaMultiplier;

		@Override
		public IVertexBuilder color(final int red, final int green, final int blue, final int alpha) {
			return this.builder.color(red, green, blue, (int) (this.alphaMultiplier*alpha));
		}

		@Override
		public IVertexBuilder vertex(final double x, final double y, final double z) {
			return this.builder.vertex(x, y, z);
		}

		@Override
		public IVertexBuilder uv(final float u, final float v) {
			return this.builder.uv(u, v);
		}

		@Override
		public IVertexBuilder overlayCoords(final int u, final int v) {
			return this.builder.overlayCoords(u, v);
		}

		@Override
		public IVertexBuilder uv2(final int u, final int v) {
			return this.builder.uv2(u, v);
		}

		@Override
		public IVertexBuilder normal(final float x, final float y, final float z) {
			return this.builder.normal(x, y, z);
		}

		@Override
		public void endVertex() {
			this.builder.endVertex();
		}

	}

}
