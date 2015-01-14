package me.superckl.prayers.client.handler;

import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.utility.PrayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderLivingEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityRenderHandler {

	private final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void onLivingRender(final RenderLivingEvent.Post e){
		for(final EnumPrayers prayer:PrayerHelper.getActivePrayers(e.entity)){
			if(!prayer.isOverhead())
				continue;
			final double dist = e.entity.getDistanceSqToEntity(e.renderer.renderManager.livingPlayer);

			if (dist <= (4096D))
			{
				final float scale = 0.016666668F * 1.6F;
				GL11.glPushMatrix();
				GL11.glTranslatef((float)e.x + 0.0F, (float)e.y + e.entity.height + 1.5F, (float)e.z);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-e.renderer.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(e.renderer.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(-scale, -scale, scale);
				GL11.glDisable(GL11.GL_LIGHTING);
				//GL11.glDepthMask(false);
				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				final Tessellator tessellator = Tessellator.instance;
				final byte b0 = 10;

				//GL11.glDisable(GL11.GL_TEXTURE_2D);
				tessellator.startDrawingQuads();
				final int j = 7;
				this.mc.renderEngine.bindTexture(prayer.getTexture());
				//tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
				tessellator.addVertexWithUV(-j - 1, -1 + b0, 0.0D, 0D, 0D);
				tessellator.addVertexWithUV(-j - 1, 14 + b0, 0.0D, 0D, 1D);
				tessellator.addVertexWithUV(j + 1, 14 + b0, 0.0D, 1D, 1D);
				tessellator.addVertexWithUV(j + 1, -1 + b0, 0.0D, 1D, 0D);
				tessellator.draw();
				//GL11.glEnable(GL11.GL_TEXTURE_2D);
				//fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, 553648127);
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
				//GL11.glDepthMask(true);
				//fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, -1);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
			}
		}
	}

}
