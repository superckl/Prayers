package me.superckl.prayers.client.render;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import me.superckl.prayers.block.AltarBlock;
import me.superckl.prayers.block.CraftingStandBlock;
import me.superckl.prayers.block.OfferingStandBlock;
import me.superckl.prayers.block.entity.AltarTileEntity;
import me.superckl.prayers.block.entity.CraftingStandTileEntity;
import me.superckl.prayers.block.entity.OfferingStandTileEntity;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.client.gui.PrayerBar;
import me.superckl.prayers.init.ModBlocks;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderEventHandler {

	private final PrayerBar widget = new PrayerBar(true, false);

	//This event renders the player's prayer points and overlays from looking at blocks
	@SubscribeEvent
	public void onRenderOverlay(final RenderGameOverlayEvent.Post e) {
		//Render after all HUD elements have been rendered
		final PlayerPrayerUser user = ClientHelper.getPlayer().isAlive() ? CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer()):null;
		if (e.getType() == ElementType.ALL && user != null && user.isUnlocked()) {
			final int height = e.getWindow().getGuiScaledHeight();
			final int startY = height - 21 + (20 - PrayerBar.HEIGHT)/2;
			this.widget.renderAt(e.getMatrixStack(), 8, startY);
		}
		if(ClientHelper.getPlayer().isCrouching() && ClientHelper.getRayTrace().getType() == RayTraceResult.Type.BLOCK) {
			final BlockPos hit = ((BlockRayTraceResult) ClientHelper.getRayTrace()).getBlockPos();
			final Block hitBlock = ClientHelper.getLevel().getBlockState(hit).getBlock();

			if(hitBlock instanceof AltarBlock) {
				final AltarTileEntity altar = (AltarTileEntity) ClientHelper.getLevel().getBlockEntity(hit);
				final double current = altar.getCurrentPoints();
				final double max = altar.getMaxPoints();
				final List<String> toWrite = Lists.newArrayList();
				toWrite.add("Valid: "+altar.isValidMultiblock());
				toWrite.add("Owner: "+(altar.getOwner() == null ? "None":UsernameCache.getLastKnownUsername(altar.getOwner())));
				toWrite.add("Points: "+String.format("%.2f/%.2f", current, max));
				final int height = ClientHelper.getWindow().getGuiScaledHeight();
				final int width = ClientHelper.getWindow().getGuiScaledWidth();
				for (int i = 0; i < toWrite.size(); i++)
					ClientHelper.getFontRenderer().draw(e.getMatrixStack(), toWrite.get(i),
							width/2-ClientHelper.getFontRenderer().width(toWrite.get(i))/2,
							height/2+5*(i+1)+ClientHelper.getFontRenderer().lineHeight*i, TextFormatting.WHITE.getColor());
			}else{
				ItemStack toRender = ItemStack.EMPTY;
				if(hitBlock instanceof OfferingStandBlock) {
					final OfferingStandTileEntity offeringStand = (OfferingStandTileEntity) ClientHelper.getLevel().getBlockEntity(hit);
					toRender = offeringStand.getInternalItemHandler().getStackInSlot(0);
				}else if(hitBlock instanceof CraftingStandBlock) {
					final Vector3d hitVec = ClientHelper.getRayTrace().getLocation();
					final Vector3d dirVec = hitVec.subtract(Math.floor(hitVec.x)+0.5, hitVec.y, Math.floor(hitVec.z)+0.5);
					final Direction dir = CraftingStandBlock.directionFromVec(dirVec, 1.5F/16);

					final CraftingStandTileEntity te = (CraftingStandTileEntity) ClientHelper.getLevel().getBlockEntity(hit);
					toRender = te.getItemHandlerForSide(dir).getStackInSlot(0);
					if(te.isCrafting()) {
						final int height = ClientHelper.getWindow().getGuiScaledHeight();
						final int width = ClientHelper.getWindow().getGuiScaledWidth();
						final String progress = String.format("Progress: %2.0f", 100*te.getCraftingProgress()).concat("%");
						final int offset = toRender.isEmpty() ? 5: 24;
						ClientHelper.getFontRenderer().draw(e.getMatrixStack(), progress, width/2-ClientHelper.getFontRenderer().width(progress)/2,
								height/2+offset, TextFormatting.WHITE.getColor());
					}
				}
				if(!toRender.isEmpty()) {
					FontRenderer font = toRender.getItem().getFontRenderer(toRender);
					if(font == null)
						font = ClientHelper.getFontRenderer();
					final int height = ClientHelper.getWindow().getGuiScaledHeight();
					final int width = ClientHelper.getWindow().getGuiScaledWidth();
					ClientHelper.getItemRenderer().renderAndDecorateItem(toRender, width/2-8, height/2+4);
					ClientHelper.getItemRenderer().renderGuiItemDecorations(font, toRender, width/2-8, height/2+4, null);
				}
			}
		}
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(final DrawHighlightEvent.HighlightBlock e) {
		final BlockPos pos = e.getTarget().getBlockPos();
		final BlockState renderBlock = ClientHelper.getLevel().getBlockState(pos);
		if(renderBlock.getBlock() instanceof AltarBlock) {
			e.setCanceled(true);
			final Vector3d proj = e.getInfo().getPosition();
			final VoxelShape connected = AltarBlock.connectAltars(renderBlock, ClientHelper.getLevel(), pos);
			RenderEventHandler.drawShape(e.getMatrix(), e.getBuffers().getBuffer(RenderType.lines()), connected,
					pos.getX()-proj.x, pos.getY()-proj.y, pos.getZ()-proj.z, 0, 0, 0, 0.4F);
		}
	}

	@SubscribeEvent
	public void onWorldRenderFinish(final RenderWorldLastEvent e) {
		if(ClientHelper.getPlayer().isCrouching() && ClientHelper.getRayTrace().getType() == Type.BLOCK &&
				(ClientHelper.getPlayer().getMainHandItem().getItem() == ModItems.CRAFTING_STAND.get() || ClientHelper.getPlayer().getOffhandItem().getItem() == ModItems.CRAFTING_STAND.get())) {
			final BlockRayTraceResult raytrace = (BlockRayTraceResult)ClientHelper.getRayTrace();
			Hand hand;
			if(ClientHelper.getPlayer().getMainHandItem().getItem() == ModItems.CRAFTING_STAND.get())
				hand = Hand.MAIN_HAND;
			else
				hand = Hand.OFF_HAND;
			final BlockItemUseContext context = new BlockItemUseContext(ClientHelper.getPlayer(), hand, ClientHelper.getPlayer().getItemInHand(hand), raytrace);
			if(context.canPlace()) {
				final BlockPos renderPos = context.getClickedPos();
				final Vector3d proj = ClientHelper.getGameRenderer().getMainCamera().getPosition();
				final BlockState oldState = ClientHelper.getLevel().getBlockState(renderPos);
				BlockState newState = ModBlocks.CRAFTING_STAND.get().getStateForPlacement(context);
				if(oldState != newState) {
					if(oldState.is(ModBlocks.CRAFTING_STAND.get()))
						newState = ModBlocks.CRAFTING_STAND.get().subtractStands(newState, oldState);
					final MatrixStack matrix = e.getMatrixStack();
					matrix.pushPose();
					matrix.translate(renderPos.getX()-proj.x, renderPos.getY()-proj.y, renderPos.getZ()-proj.z);
					final IRenderTypeBuffer.Impl buffer = ClientHelper.getBufferSource();
					ClientHelper.getBlockRenderer().renderBlock(newState, matrix, new RenderHelper.AlphaMultipliedVertexBuffer(buffer, Atlases.translucentCullBlockSheet(), 0.4F),
							WorldRenderer.getLightColor(ClientHelper.getLevel(), renderPos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
					matrix.popPose();
					buffer.endBatch();
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderPlayer(final RenderPlayerEvent e) {
		this.renderOverheadPrayers(e.getPlayer(), e.getRenderer().getDispatcher().camera, e.getMatrixStack(), e.getLight(), e.getPartialRenderTick());
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public <T extends LivingEntity> void onRenderLiving(final RenderLivingEvent<T,?> e) {
		this.renderOverheadPrayers((T) e.getEntity(), e.getRenderer().getDispatcher().camera, e.getMatrixStack(), e.getLight(), e.getPartialRenderTick());
	}

	private static float overheadOffset = 0.5F;
	private static float prayerScale = 0.15F;

	@SuppressWarnings("deprecation")
	public <T extends LivingEntity> void renderOverheadPrayers(final T entity, final ActiveRenderInfo camera, final MatrixStack matrix, final int light, final float partialTicks) {
		final Collection<Prayer> prayers = CapabilityHandler.getPrayerCapability(entity).getActivePrayers();
		prayers.removeIf(prayer -> !prayer.isOverhead());
		if(prayers.isEmpty())
			return;
		int i = 0;
		final Iterator<Prayer> it = prayers.iterator();
		final float[][] spacings = this.buildSpacings(prayers.size());
		final float bbSize = (float) entity.getBoundingBox().getSize();
		//Setup correct render state (taken from ParticleManager)
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.enableFog();
		RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
		RenderSystem.enableTexture();
		RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
		while(it.hasNext()) {
			final float[] spacing = spacings[i++];
			float offset = RenderEventHandler.overheadOffset;
			if(entity.hasCustomName())
				offset += 0.5F;
			final Vector3f[] vertices = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
			for (final Vector3f vertex:vertices) {
				//Setup correct orientation
				vertex.transform(Vector3f.ZP.rotationDegrees(180F));
				//Apply spacing relative to other prayers
				vertex.add(spacing[0], spacing[1], 0);
				//Transform to world coordinates
				vertex.transform(camera.rotation());
				//Scale to appropriate size, considering general size of entity
				vertex.mul(RenderEventHandler.prayerScale*bbSize);
				//Move to above entity (we're in world coordinates now)
				vertex.add(0, entity.getBbHeight()+offset+RenderEventHandler.prayerScale*(bbSize-1), 0);
				final Vector4f toTransform = new Vector4f(vertex);
				//Transform by current matrix pose
				toTransform.transform(matrix.last().pose());
				vertex.set(toTransform.x(), toTransform.y(), toTransform.z());
			}
			final BufferBuilder builder = Tessellator.getInstance().getBuilder();
			builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
			ClientHelper.getTextureManager().bind(it.next().getTexture());
			//The ordering of these calls is very important, it must match that defined in the vertex format
			builder.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z()).color(1F, 1F, 1F, 1F).uv(0, 0).uv2(light).endVertex();
			builder.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z()).color(1F, 1F, 1F, 1F).uv(0, 1).uv2(light).endVertex();
			builder.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z()).color(1F, 1F, 1F, 1F).uv(1, 1).uv2(light).endVertex();
			builder.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z()).color(1F, 1F, 1F, 1F).uv(1, 0).uv2(light).endVertex();
			Tessellator.getInstance().end();
		}
	}

	private static float overheadSpacing = 0.25F;
	private static float[][][] spacingCache = new float[][][] {};

	private float[][] buildSpacings(final int prayers){
		if(RenderEventHandler.spacingCache.length < prayers) {
			if (prayers - 1 > 0)
				this.buildSpacings(prayers - 1);
			final float[][] spacings = new float[prayers][2];
			int i = 0;
			int j = 0;
			while(i < prayers) {
				if(prayers - i >= 2) {
					spacings[i++] = new float[] {-1-RenderEventHandler.overheadSpacing/2, 2.2F*j};
					spacings[i++] = new float[] {1+RenderEventHandler.overheadSpacing/2, 2.2F*j};
				} else
					spacings[i++] = new float[] {0,2.2F*j};
				j++;
			}
			RenderEventHandler.spacingCache = ArrayUtils.add(RenderEventHandler.spacingCache, spacings);
		}
		return RenderEventHandler.spacingCache[prayers - 1];
	}

	public static void drawShape(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final VoxelShape shapeIn, final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha) {
		final Matrix4f matrix4f = matrixStackIn.last().pose();
		shapeIn.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
			bufferIn.vertex(matrix4f, (float)(x1 + x), (float)(y1 + y), (float)(z1 + z)).color(red, green, blue, alpha).endVertex();
			bufferIn.vertex(matrix4f, (float)(x2 + x), (float)(y2 + y), (float)(z2 + z)).color(red, green, blue, alpha).endVertex();
		});
	}

}
