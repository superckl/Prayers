package me.superckl.prayers.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.block.OfferingStandTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class OfferingStandRenderer extends TileEntityRenderer<OfferingStandTileEntity>{

	private final ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();

	public OfferingStandRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(final OfferingStandTileEntity tileEntityIn, final float partialTicks, final MatrixStack matrixStackIn,
			final IRenderTypeBuffer bufferIn, final int combinedLightIn, final int combinedOverlayIn) {
		// TODO Auto-generated method stub

	}

}
