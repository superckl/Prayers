package me.superckl.prayercraft.common.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderHelper {

	public static void drawTexturedRect(final ResourceLocation texture, final double x, final double y, final int u, final int v, final int width, final int height, final int imageWidth, final int imageHeight, final double scale) {
		RenderHelper.drawTexturedRect(texture, x, y, 0D, u, v, width, height, imageWidth, imageHeight, scale);
	}

	public static int extrapolateWidth(final IIcon icon){
		final float diff = icon.getMaxU()-icon.getMinU();
		return Math.round(icon.getIconWidth()/diff);
	}

	public static int extrapolateHeight(final IIcon icon){
		final float diff = icon.getMaxV()-icon.getMinV();
		return Math.round(icon.getIconHeight()/diff);
	}

	public static void drawTexturedRect(final ResourceLocation texture, final double x, final double y, final double z, final int u, final int v, final int width, final int height, final int imageWidth, final int imageHeight, final double scale) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		final double minU = (double)u / (double)imageWidth;
		final double maxU = (double)(u + width) / (double)imageWidth;
		final double minV = (double)v / (double)imageHeight;
		final double maxV = (double)(v + height) / (double)imageHeight;
		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + (scale*width), y + (scale*height), z, maxU, maxV);
		tessellator.addVertexWithUV(x + (scale*width), y, z, maxU, minV);
		tessellator.addVertexWithUV(x, y, z, minU, minV);
		tessellator.addVertexWithUV(x, y + (scale*height), z, minU, maxV);
		tessellator.draw();
	}

	public static void drawTexturedRectFromIcon(final IIcon icon, final double x, final double y, final double z, final int u, final int v, final int width, final int height, final double scale) {
		final int imageWidth = RenderHelper.extrapolateWidth(icon);
		final int imageHeight = RenderHelper.extrapolateHeight(icon);

		final double minU = icon.getMinU() + ((double)u / (double)imageWidth);
		final double maxU = icon.getMinU() + ((double)(u + width) / (double)imageWidth);
		final double minV = icon.getMinV() + ((double)v / (double)imageHeight);
		final double maxV = icon.getMinV() + ((double)(v + height) / (double)imageHeight);
		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + (scale*width), y + (scale*height), z, maxU, maxV);
		tessellator.addVertexWithUV(x + (scale*width), y, z, maxU, minV);
		tessellator.addVertexWithUV(x, y, z, minU, minV);
		tessellator.addVertexWithUV(x, y + (scale*height), z, minU, maxV);
		tessellator.draw();
	}

	public static void renderEntityToGUI(final Entity entity, final int x, final int y, final float scale){
		// Draw the Entity Model

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 50.0F);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		final float var7 = entity.rotationYaw;
		final float var8 = entity.rotationPitch;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		entity.rotationYaw = var7+1;
		entity.rotationPitch = var8+1;
		GL11.glPopMatrix();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static void drawTexturedCircle(final int x, final int y, final int radius, final int steps, final ResourceLocation texture){
		RenderHelper.drawTexturedCircle(x, y, radius, x+radius, x-radius, y+radius, y-radius, steps, texture);
	}

	public static void drawTexturedCircle(final int x, final int y, final int radius, final int maxX, final int minX, final int maxY, final int minY, final int steps, final ResourceLocation texture){
		//We will assume the texture is radius*2 by radius*2 pixels
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		final int cornerX = x-radius;
		final int cornerY = y-radius;
		final Tessellator tessellator = Tessellator.instance;
		//tessellator.startDrawingQuads();
		final int increment = (int) ((2*Math.PI)/steps);
		for(int step = 0; step < (2*Math.PI); step +=increment){
			final double newX = Math.max(Math.min((radius*Math.cos(step))+x, maxX), minX);
			final double newY = Math.max(Math.min((radius*Math.cos(step))+y, maxY), minY);
			tessellator.addVertexWithUV(newX, newY, 0, newX-cornerX, newY-cornerY);
		}
		tessellator.draw();
	}

	public static void drawFluid(final Minecraft mc, final int x, final int y, final int width, final int height, final FluidTankInfo tankInfo){
		final float perc = (float) tankInfo.fluid.amount/(float) tankInfo.capacity;
		int fluidHeight = (int) (height*perc);
		RenderHelper.setGLColorFromInt(tankInfo.fluid.getFluid().getColor(tankInfo.fluid));
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		final int stepHeight = height/(tankInfo.capacity/FluidContainerRegistry.BUCKET_VOLUME);
		int tempY = y;
		while(fluidHeight > 0){
			final int drawHeight = fluidHeight >= stepHeight ? stepHeight:fluidHeight;
			if(drawHeight < stepHeight)
				tempY += stepHeight-fluidHeight;
			mc.currentScreen.drawTexturedModelRectFromIcon(x, tempY -= stepHeight, tankInfo.fluid.getFluid().getIcon(), width, drawHeight);
			fluidHeight -= stepHeight;
		}
		//mc.currentScreen.drawTexturedModelRectFromIcon(x, 78 - height, tankInfo.fluid.getFluid().getIcon(), width, height);
	}

	public static void setGLColorFromInt(final int color) {
		final float red = ((color >> 16) & 255) / 255.0F;
		final float green = ((color >> 8) & 255) / 255.0F;
		final float blue = (color & 255) / 255.0F;
		GL11.glColor4f(red, green, blue, 1.0F);
	}

}
