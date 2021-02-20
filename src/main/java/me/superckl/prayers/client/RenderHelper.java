package me.superckl.prayers.client;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class RenderHelper {

	private final static Minecraft mc = Minecraft.getInstance();
	private final static ItemRenderer itemRenderer = RenderHelper.mc.getItemRenderer();
	private static final Random random = new Random();

	public static void renderFloatingItemStack(final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn,
			final float partialTicks, final int combinedLightIn, final int combinedOverlayIn, final ItemStack itemstack) {
		final float f1 = MathHelper.sin((RenderHelper.mc.world.getGameTime() + partialTicks) / 10.0F) * 0.1F + 0.1F;
		matrixStackIn.translate(0.0D, f1 + 0.25F * .1, 0.0D);
		final float itemRotation = (RenderHelper.mc.world.getGameTime() + partialTicks) / 20.0F;
		matrixStackIn.rotate(Vector3f.YP.rotation(itemRotation));

		final int j = RenderHelper.getModelCount(itemstack);
		final float f7 = -0.0F * (j - 1) * 0.5F;
		final float f8 = -0.0F * (j - 1) * 0.5F;
		final float f9 = -0.09375F * (j - 1) * 0.5F;
		matrixStackIn.translate(f7, f8, f9);

		final int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
		RenderHelper.random.setSeed(i);

		for(int k = 0; k < j; ++k) {
			matrixStackIn.push();
			if (k > 0) {
				final float f12 = (RenderHelper.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
				final float f14 = (RenderHelper.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
				matrixStackIn.translate(f12, f14, 0.0D);
			}

			RenderHelper.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
			matrixStackIn.pop();
			matrixStackIn.translate(0.0, 0.0, 0.09375F);
		}
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

}
