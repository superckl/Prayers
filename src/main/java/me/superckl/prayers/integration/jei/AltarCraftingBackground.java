package me.superckl.prayers.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.init.ModBlocks;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

public class AltarCraftingBackground implements IDrawable{

	public static final int WIDTH = 160;
	public static final int HEIGHT = 66;
	
	public static final BlockState altarState = ModBlocks.ALTARS.get(AltarTypes.SANDSTONE).get().defaultBlockState();
	
	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
		matrixStack.pushPose();
		AltarCraftingBackground.setupBlockRendering(matrixStack, xOffset, yOffset, altarState);

		IRenderTypeBuffer.Impl buffer = ClientHelper.getBufferSource();
		ClientHelper.getBlockRenderer().renderBlock(altarState, matrixStack, buffer,
				15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		buffer.endBatch();
		matrixStack.popPose();
	}

	public static void setupBlockRendering(MatrixStack matrixStack, int xOffset, int yOffset, BlockState state) {
		final float scale = 66;
		matrixStack.translate(xOffset+3*WIDTH/4+2, yOffset+19, 100+50);
		matrixStack.translate(scale/2, scale/2, 0.0F);
		matrixStack.scale(1.0F, -1.0F, 1.0F);
		matrixStack.scale(scale, scale, scale);
		IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
		ForgeHooksClient.handleCameraTransforms(matrixStack, model, TransformType.GUI, false);
	}
	
}
