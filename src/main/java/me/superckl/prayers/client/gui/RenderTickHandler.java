package me.superckl.prayers.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

	private static final ResourceLocation POINT_ICON = new ResourceLocation(Prayers.MOD_ID, "textures/misc/prayericon.png");

	private final Minecraft mc = Minecraft.getInstance();

	//This event renders the player's prayer points
	@SubscribeEvent
	public void onRenderOverlay(final RenderGameOverlayEvent.Post e) {
		//Render after all HUD elements have been rendered
		if (e.getType() != null && e.getType() == ElementType.ALL) {
			//Make sure the player has prayer points
			final LazyOptional<IPrayerUser> opt = this.mc.player.getCapability(Prayers.PRAYER_USER_CAPABILITY);
			if (!opt.isPresent())
				return;
			final IPrayerUser user = opt.orElse(null);

			final int width = e.getWindow().getScaledWidth();
			final int height = e.getWindow().getScaledHeight();


			//Determine positioning of text and icon (defaults to bottom right)
			final String points = new StringBuilder().append(MathHelper.ceil(user.getCurrentPrayerPoints())).append('/').append(MathHelper.ceil(user.getMaxPrayerPoints())).toString();
			final int textWidth = this.mc.fontRenderer.getStringWidth(points);
			final int startX = width - 8 - 16 - 4 - textWidth;
			final int startY = height - 4 - 16;
			final float textOffset = (16-this.mc.fontRenderer.FONT_HEIGHT)/2F; //Half of the texture height because font renderer takes in y for middle of text

			//Bind texture and render text and icon
			this.mc.textureManager.bindTexture(RenderTickHandler.POINT_ICON);
			RenderSystem.enableBlend();
			AbstractGui.blit(e.getMatrixStack(), startX+textWidth+4, startY, 0, 0, 16, 16, 16, 16);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			this.mc.fontRenderer.drawString(e.getMatrixStack(), points, startX, startY+textOffset, Color.fromTextFormatting(TextFormatting.WHITE).getColor());

		}
	}

}
