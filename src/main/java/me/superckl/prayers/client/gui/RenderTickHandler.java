package me.superckl.prayers.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import me.superckl.prayers.block.AltarBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

	private final PrayerBar widget = new PrayerBar(true, false);
	private final Minecraft mc = Minecraft.getInstance();

	//This event renders the player's prayer points
	@SubscribeEvent
	public void onRenderOverlay(final RenderGameOverlayEvent.Post e) {
		//Render after all HUD elements have been rendered
		if (e.getType() != null && e.getType() == ElementType.ALL) {
			final int height = e.getWindow().getScaledHeight();
			final int startY = height - 21 + (20 - PrayerBar.HEIGHT)/2;
			this.widget.renderAt(e.getMatrixStack(), 8, startY);
		}
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(final DrawHighlightEvent.HighlightBlock e) {
		final BlockPos pos = e.getTarget().getPos();
		final BlockState renderBlock = this.mc.world.getBlockState(pos);
		if(renderBlock.getBlock() instanceof AltarBlock) {
			e.setCanceled(true);
			final Vector3d proj = e.getInfo().getProjectedView();
			final VoxelShape connected = AltarBlock.connectAltars(renderBlock, this.mc.world, pos, ISelectionContext.forEntity(e.getInfo().getRenderViewEntity()));
			RenderTickHandler.drawShape(e.getMatrix(), e.getBuffers().getBuffer(RenderType.getLines()), connected,
					pos.getX()-proj.x, pos.getY()-proj.y, pos.getZ()-proj.z, 0, 0, 0, 0.4F);
		}
	}

	public static void drawShape(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final VoxelShape shapeIn, final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha) {
		final Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
		shapeIn.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
			bufferIn.pos(matrix4f, (float)(x1 + x), (float)(y1 + y), (float)(z1 + z)).color(red, green, blue, alpha).endVertex();
			bufferIn.pos(matrix4f, (float)(x2 + x), (float)(y2 + y), (float)(z2 + z)).color(red, green, blue, alpha).endVertex();
		});
	}

}
