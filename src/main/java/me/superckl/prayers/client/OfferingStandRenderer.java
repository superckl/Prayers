package me.superckl.prayers.client;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.OfferingStandTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class OfferingStandRenderer extends TileEntityRenderer<OfferingStandTileEntity>{

	private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
	private final Random random = new Random();

	public OfferingStandRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(final OfferingStandTileEntity tileEntityIn, final float partialTicks, final MatrixStack matrixStackIn,
			final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		if(tileEntityIn.getItem().isEmpty())
			return;
		final ItemStack itemstack = tileEntityIn.getItem();
		matrixStackIn.push();
		final Vector3f renderLoc = new Vector3f(0.5F, 7/16F, 0.5F);
		matrixStackIn.translate(renderLoc.getX(), renderLoc.getY(), renderLoc.getZ());
		matrixStackIn.scale(0.4F, 0.4F, 0.4F);
		final float f1 = MathHelper.sin((tileEntityIn.getItemAge() + partialTicks) / 10.0F) * 0.1F + 0.1F;
		matrixStackIn.translate(0.0D, f1 + 0.25F * .1, 0.0D);
		final float itemRotation = (tileEntityIn.getItemAge() + partialTicks) / 20.0F;
		matrixStackIn.rotate(Vector3f.YP.rotation(itemRotation));

		final int j = this.getModelCount(itemstack);
		final boolean flag = false;
		if (!flag) {
			final float f7 = -0.0F * (j - 1) * 0.5F;
			final float f8 = -0.0F * (j - 1) * 0.5F;
			final float f9 = -0.09375F * (j - 1) * 0.5F;
			matrixStackIn.translate(f7, f8, f9);
		}

		final int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
		this.random.setSeed(i);

		for(int k = 0; k < j; ++k) {
			matrixStackIn.push();
			if (k > 0)
				if (flag) {
					final float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					final float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					final float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrixStackIn.translate(f11, f13, f10);
				} else {
					final float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					final float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					matrixStackIn.translate(f12, f14, 0.0D);
				}

			this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
			matrixStackIn.pop();
			if (!flag)
				matrixStackIn.translate(0.0, 0.0, 0.09375F);
		}
		matrixStackIn.pop();
	}

	protected int getModelCount(final ItemStack stack) {
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
