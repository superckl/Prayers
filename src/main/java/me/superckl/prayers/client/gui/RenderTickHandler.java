package me.superckl.prayers.client.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import me.superckl.prayers.block.AltarBlock;
import me.superckl.prayers.block.TileEntityAltar;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.UsernameCache;
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
		if(this.mc.player.isSneaking() && this.mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
			final BlockPos hit = ((BlockRayTraceResult) this.mc.objectMouseOver).getPos();
			if(this.mc.world.getBlockState(hit).getBlock() instanceof AltarBlock) {
				final TileEntityAltar altar = (TileEntityAltar) this.mc.world.getTileEntity(hit);
				final List<TileEntityAltar> altars = altar.getConnected();
				final double current = altars.stream().mapToDouble(TileEntityAltar::getCurrentPoints).sum();
				final double max = altars.stream().mapToDouble(TileEntityAltar::getMaxPoints).sum();
				final List<String> toWrite = Lists.newArrayList();
				toWrite.add("Valid: "+altar.isValidMultiblock());
				toWrite.add("Owner: "+(altar.getOwner() == null ? "None":UsernameCache.getLastKnownUsername(altar.getOwner())));
				toWrite.add("Points: "+String.format("%.2f/%.2f", current, max));
				final int height = this.mc.getMainWindow().getScaledHeight();
				final int width = this.mc.getMainWindow().getScaledWidth();
				for (int i = 0; i < toWrite.size(); i++)
					this.mc.fontRenderer.drawString(e.getMatrixStack(), toWrite.get(i),
							width/2-this.mc.fontRenderer.getStringWidth(toWrite.get(i))/2,
							height/2+5*(i+1)+this.mc.fontRenderer.FONT_HEIGHT*i, TextFormatting.WHITE.getColor());
			}
		}
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(final DrawHighlightEvent.HighlightBlock e) {
		final BlockPos pos = e.getTarget().getPos();
		final BlockState renderBlock = this.mc.world.getBlockState(pos);
		if(renderBlock.getBlock() instanceof AltarBlock) {
			e.setCanceled(true);
			final Vector3d proj = e.getInfo().getProjectedView();
			final VoxelShape connected = AltarBlock.connectAltars(renderBlock, this.mc.world, pos);
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
