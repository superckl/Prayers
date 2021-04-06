package me.superckl.prayers.client;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.ClientHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
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

	@SuppressWarnings("deprecation")
	public static void renderEntityInInventory(final int x, final int y, final int scale, final MatrixStack matrixstack, final float mouseVecX, final float mouseVecY, final LivingEntity entity) {
		final float f = (float)Math.atan(mouseVecX / 40.0F);
		final float f1 = (float)Math.atan(mouseVecY / 40.0F);
		matrixstack.pushPose();
		matrixstack.translate(x, y, 1050.0F);
		matrixstack.scale(1.0F, 1.0F, -1.0F);
		matrixstack.translate(0.0D, 0.0D, 1000.0D);
		matrixstack.scale(scale, scale, scale);
		final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		final Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(quaternion1);
		matrixstack.mulPose(quaternion);
		final float yBodyRot0 = entity.yBodyRot;
		final float yRot0 = entity.yRot;
		final float xRot0 = entity.xRot;
		final float yHeadRot00 = entity.yHeadRotO;
		final float yHeadRot0 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + f * 20.0F;
		entity.yRot = 180.0F + f * 40.0F;
		entity.xRot = -f1 * 20.0F;
		entity.yHeadRot = entity.yRot;
		entity.yHeadRotO = entity.yRot;
		final EntityRendererManager entityrenderermanager = ClientHelper.getEntityRenderer();
		quaternion1.conj();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		final IRenderTypeBuffer.Impl buffer = ClientHelper.getBufferSource();
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.render(entity, 0, 0, 0, 0, 1, matrixstack, buffer, 15728880);
		});
		buffer.endBatch();
		entityrenderermanager.setRenderShadow(true);
		entity.yBodyRot = yBodyRot0;
		entity.yRot = yRot0;
		entity.xRot = xRot0;
		entity.yHeadRotO = yHeadRot00;
		entity.yHeadRot = yHeadRot0;
		matrixstack.popPose();
	}

	@SuppressWarnings("deprecation")
	public static void renderEntityInInventory(final int x, final int y, final int scale, final float partialTicks, final MatrixStack matrixstack, final float rot, final LivingEntity entity) {
		matrixstack.pushPose();
		matrixstack.translate(x, y, 1050.0F);
		matrixstack.scale(1.0F, 1.0F, -1.0F);
		matrixstack.translate(0.0D, 0.0D, 1000.0D);
		matrixstack.scale(scale, scale, scale);
		final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		final Quaternion quaternion1 = Vector3f.XP.rotationDegrees(0);
		quaternion.mul(quaternion1);
		matrixstack.mulPose(quaternion);
		//		final float yBodyRot0 = entity.yBodyRot;
		//		final float yRot0 = entity.yRot;
		//		final float xRot0 = entity.xRot;
		//		final float yHeadRot00 = entity.yHeadRotO;
		//		final float yHeadRot0 = entity.yHeadRot;
		entity.yBodyRot = 180.0F+rot;
		entity.yBodyRotO = entity.yBodyRot;
		entity.yRot = 180.0F+rot;
		entity.yRotO = entity.yRot;
		entity.xRot = 0;
		entity.xRotO = entity.xRot;
		entity.yHeadRot = entity.yRot;
		entity.yHeadRotO = entity.yRot;
		final EntityRendererManager entityrenderermanager = ClientHelper.getEntityRenderer();
		quaternion1.conj();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		final IRenderTypeBuffer.Impl buffer = ClientHelper.getBufferSource();
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.render(entity, 0, 0, 0, 0, partialTicks, matrixstack, buffer, 15728880);
		});
		buffer.endBatch();
		entityrenderermanager.setRenderShadow(true);
		//		entity.yBodyRot = yBodyRot0;
		//		entity.yRot = yRot0;
		//		entity.xRot = xRot0;
		//		entity.yHeadRotO = yHeadRot00;
		//		entity.yHeadRot = yHeadRot0;
		matrixstack.popPose();
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
