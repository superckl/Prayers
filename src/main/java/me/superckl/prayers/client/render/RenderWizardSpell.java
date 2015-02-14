package me.superckl.prayers.client.render;

import me.superckl.prayers.common.reference.RenderData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderWizardSpell extends Render{

	@Override
	public void doRender(final Entity p_76986_1_, final double p_76986_2_, final double p_76986_4_, final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
		GL11.glPushMatrix();
		this.bindEntityTexture(p_76986_1_);
		GL11.glTranslatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		final IIcon iicon = RenderData.MAGIC_BURST;
		final Tessellator tessellator = Tessellator.instance;
		final float f3 = iicon.getMinU();
		final float f4 = iicon.getMaxU();
		final float f5 = iicon.getMinV();
		final float f6 = iicon.getMaxV();
		final float f7 = 1.0F;
		final float f8 = 0.5F;
		final float f9 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0F - f8, 0.0F - f9, 0.0D, f3, f6);
		tessellator.addVertexWithUV(f7 - f8, 0.0F - f9, 0.0D, f4, f6);
		tessellator.addVertexWithUV(f7 - f8, 1.0F - f9, 0.0D, f4, f5);
		tessellator.addVertexWithUV(0.0F - f8, 1.0F - f9, 0.0D, f3, f5);
		tessellator.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();

	}

	@Override
	protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
		return TextureMap.locationItemsTexture;
	}

}
