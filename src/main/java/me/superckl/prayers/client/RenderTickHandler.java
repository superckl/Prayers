package me.superckl.prayers.client;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.block.AltarBlock;
import me.superckl.prayers.block.AltarTileEntity;
import me.superckl.prayers.block.CraftingStandBlock;
import me.superckl.prayers.block.CraftingStandTileEntity;
import me.superckl.prayers.block.OfferingStandBlock;
import me.superckl.prayers.block.OfferingStandTileEntity;
import me.superckl.prayers.client.gui.PrayerBar;
import me.superckl.prayers.init.ModBlocks;
import me.superckl.prayers.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

	private final PrayerBar widget = new PrayerBar(true, false);
	private final Minecraft mc = Minecraft.getInstance();
	private final ItemRenderer itemRender = this.mc.getItemRenderer();

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
			final Block hitBlock = this.mc.world.getBlockState(hit).getBlock();

			if(hitBlock instanceof AltarBlock) {
				final AltarTileEntity altar = (AltarTileEntity) this.mc.world.getTileEntity(hit);
				final double current = altar.getCurrentPoints();
				final double max = altar.getMaxPoints();
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
			}else{
				ItemStack toRender = ItemStack.EMPTY;
				if(hitBlock instanceof OfferingStandBlock) {
					final OfferingStandTileEntity offeringStand = (OfferingStandTileEntity) this.mc.world.getTileEntity(hit);
					toRender = offeringStand.getStackInSlot(0);
				}else if(hitBlock instanceof CraftingStandBlock) {
					final Vector3d hitVec = this.mc.objectMouseOver.getHitVec();
					final Vector3d dirVec = hitVec.subtract(Math.floor(hitVec.x)+0.5, hitVec.y, Math.floor(hitVec.z)+0.5);
					final Direction dir = CraftingStandBlock.directionFromVec(dirVec, 1.5F/16);

					final CraftingStandTileEntity te = (CraftingStandTileEntity) this.mc.world.getTileEntity(hit);
					toRender = te.getStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(dir));
				}
				if(!toRender.isEmpty()) {
					FontRenderer font = toRender.getItem().getFontRenderer(toRender);
					if(font == null)
						font = this.mc.fontRenderer;
					final int height = this.mc.getMainWindow().getScaledHeight();
					final int width = this.mc.getMainWindow().getScaledWidth();
					this.itemRender.renderItemAndEffectIntoGUI(toRender, width/2-8, height/2+4);
					this.itemRender.renderItemOverlayIntoGUI(font, toRender, width/2-8, height/2+4, null);
				}
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

	@SubscribeEvent
	public void onWorldRenderFinish(final RenderWorldLastEvent e) {
		if(this.mc.player.isSneaking() && this.mc.objectMouseOver.getType() == Type.BLOCK &&
				(this.mc.player.getHeldItemMainhand().getItem() == ModItems.CRAFTING_STAND.get() || this.mc.player.getHeldItemOffhand().getItem() == ModItems.CRAFTING_STAND.get())) {
			final BlockRayTraceResult raytrace = (BlockRayTraceResult)this.mc.objectMouseOver;
			Hand hand;
			if(this.mc.player.getHeldItemMainhand().getItem() == ModItems.CRAFTING_STAND.get())
				hand = Hand.MAIN_HAND;
			else
				hand = Hand.OFF_HAND;
			final BlockItemUseContext context = new BlockItemUseContext(this.mc.player, hand, this.mc.player.getHeldItem(hand), raytrace);
			if(context.canPlace()) {
				final BlockPos renderPos = context.getPos();
				final Vector3d proj = this.mc.gameRenderer.getActiveRenderInfo().getProjectedView();
				final BlockState oldState = this.mc.world.getBlockState(renderPos);
				BlockState newState = ModBlocks.CRAFTING_STAND.get().getStateForPlacement(context);
				if(oldState != newState) {
					if(oldState.isIn(ModBlocks.CRAFTING_STAND.get()))
						newState = ModBlocks.CRAFTING_STAND.get().subtractStands(newState, oldState);
					final MatrixStack matrix = e.getMatrixStack();
					matrix.push();
					matrix.translate(renderPos.getX()-proj.x, renderPos.getY()-proj.y, renderPos.getZ()-proj.z);
					final IRenderTypeBuffer.Impl buffer = this.mc.getRenderTypeBuffers().getBufferSource();
					this.mc.getBlockRendererDispatcher().renderBlock(newState, matrix, new AlphaMultipliedBlockVertexBuffer(buffer, 0.4F),
							WorldRenderer.getCombinedLight(this.mc.world, renderPos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
					matrix.pop();
					buffer.finish();
				}
			}
		}
	}

	public static void drawShape(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final VoxelShape shapeIn, final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha) {
		final Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
		shapeIn.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
			bufferIn.pos(matrix4f, (float)(x1 + x), (float)(y1 + y), (float)(z1 + z)).color(red, green, blue, alpha).endVertex();
			bufferIn.pos(matrix4f, (float)(x2 + x), (float)(y2 + y), (float)(z2 + z)).color(red, green, blue, alpha).endVertex();
		});
	}

	@RequiredArgsConstructor
	public static class AlphaMultipliedBlockVertexBuffer implements IRenderTypeBuffer{

		private final IRenderTypeBuffer wrapped;
		private final float alphaMultiplier;

		@Override
		public IVertexBuilder getBuffer(final RenderType type) {
			return new AlphaMultipliedVertexBuilder(this.wrapped.getBuffer(Atlases.getTranslucentCullBlockType()), this.alphaMultiplier);
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
		public IVertexBuilder pos(final double x, final double y, final double z) {
			return this.builder.pos(x, y, z);
		}

		@Override
		public IVertexBuilder tex(final float u, final float v) {
			return this.builder.tex(u, v);
		}

		@Override
		public IVertexBuilder overlay(final int u, final int v) {
			return this.builder.overlay(u, v);
		}

		@Override
		public IVertexBuilder lightmap(final int u, final int v) {
			return this.builder.lightmap(u, v);
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
