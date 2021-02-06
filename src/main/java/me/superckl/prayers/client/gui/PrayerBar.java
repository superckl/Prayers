package me.superckl.prayers.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lombok.AllArgsConstructor;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;

@AllArgsConstructor
public class PrayerBar {

	private static final ResourceLocation POINT_ICON = new ResourceLocation(Prayers.MOD_ID, "textures/misc/prayericon.png");
	private static final ResourceLocation POINT_BAR = new ResourceLocation(Prayers.MOD_ID, "textures/gui/prayer_bar.png");

	private final Minecraft mc = Minecraft.getInstance();

	public static final int HEIGHT = 16;

	public final boolean leftJustified;

	public void renderAt(final MatrixStack matrixStack, final int x, final int y) {
		final IPrayerUser user = IPrayerUser.getUser(this.mc.player);

		//Bar is 40x12, icon is 16x16
		//Determine positioning of text and icon (defaults to bottom right)
		final float current = user.getCurrentPrayerPoints();
		final float max = user.getMaxPrayerPoints();
		Color color = Color.fromTextFormatting(TextFormatting.WHITE);
		if (current >= max)
			color = Color.fromTextFormatting(TextFormatting.AQUA);
		else if(current <= 0)
			color = Color.fromTextFormatting(TextFormatting.RED);
		else if(current/max <= 0.2)
			color = Color.fromTextFormatting(TextFormatting.YELLOW);
		final String points = Integer.toString(MathHelper.ceil(user.getCurrentPrayerPoints()));
		final int width = 16+3+40+4+this.mc.fontRenderer.getStringWidth(Integer.toString(MathHelper.ceil(max)));
		final int startX = this.leftJustified ? x : x-width;
		//We have to add (or subtract) 1 here to creates gaps of the desired size
		final float textOffset = 8+1-(this.mc.fontRenderer.FONT_HEIGHT/2F-1); //Half of the texture height because font renderer takes in y for middle of text
		//This is 2+1 to create a gap of size 2
		final int barOffset = 3;

		final float prayerPercentage = Math.min(user.getCurrentPrayerPoints()/user.getMaxPrayerPoints(), 1F);
		final int barWidth = Math.round(prayerPercentage*40);

		//Bind texture and render text and icon
		this.mc.textureManager.bindTexture(PrayerBar.POINT_ICON);
		RenderSystem.enableBlend();
		AbstractGui.blit(matrixStack, startX, y, 0, 0, 16, 16, 16, 16);
		this.mc.textureManager.bindTexture(PrayerBar.POINT_BAR);
		//Render the last two extra columns of the on with semi-transparency?
		AbstractGui.blit(matrixStack, startX+16+3, y+barOffset, barWidth, 0, 40-barWidth, 12, 40, 24);
		AbstractGui.blit(matrixStack, startX+16+3, y+barOffset, 0, 12, barWidth, 12, 40, 24);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		this.mc.fontRenderer.drawString(matrixStack, points, startX+16+3+40+4, y+textOffset, color.getColor());
	}

}
