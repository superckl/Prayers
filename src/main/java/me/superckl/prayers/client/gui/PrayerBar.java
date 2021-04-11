package me.superckl.prayers.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lombok.AllArgsConstructor;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.client.ClientHelper;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

@AllArgsConstructor
public class PrayerBar {

	public static final int HEIGHT = 16;

	public final boolean leftJustified;
	public final boolean inludeMax;

	public void renderAt(final MatrixStack matrixStack, final int x, final int y) {
		if(!ClientHelper.getPlayer().isAlive())
			return;
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer());

		//Bar is 40x12, icon is 16x16
		//Determine positioning of text and icon (defaults to bottom right)
		final double current = user.getCurrentPrayerPoints();
		final double max = user.getMaxPrayerPoints();
		TextFormatting color = TextFormatting.WHITE;
		if (current >= max)
			color = TextFormatting.AQUA;
		else if(current <= 0)
			color = TextFormatting.RED;
		else if(current/max <= 0.2)
			color = TextFormatting.YELLOW;
		final StringBuilder builder= new StringBuilder().append(MathHelper.ceil(user.getCurrentPrayerPoints()));
		if(this.inludeMax)
			builder.append('/').append(MathHelper.ceil(user.getMaxPrayerPoints()));
		final String points = builder.toString();
		final int width = 16+3+40+4+ClientHelper.getFontRenderer().width(points);
		final int startX = this.leftJustified ? x : x-width;
		//We have to add (or subtract) 1 here to creates gaps of the desired size
		final float textOffset = 8+1-(ClientHelper.getFontRenderer().lineHeight/2F-1); //Half of the texture height because font renderer takes in y for middle of text
		//This is 2+1 to create a gap of size 2
		final int barOffset = 3;

		final double prayerPercentage = Math.min(user.getCurrentPrayerPoints()/user.getMaxPrayerPoints(), 1F);
		final int barWidth = (int) Math.round(prayerPercentage*40);

		//Bind texture and render text and icon
		ClientHelper.getTextureManager().bind(PrayerSelectGUI.PRAYER_GUI_TEXTURE);
		RenderSystem.enableBlend();
		AbstractGui.blit(matrixStack, startX, y, 0, 140, 16, 16, 256, 256);
		//Render the last two extra columns of the on with semi-transparency?
		AbstractGui.blit(matrixStack, startX+16+3+barWidth, y+barOffset, barWidth, 116, 40-barWidth, 12, 256, 256);
		AbstractGui.blit(matrixStack, startX+16+3, y+barOffset, 0, 116+12, barWidth, 12, 256, 256);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		ClientHelper.getFontRenderer().draw(matrixStack, points, startX+16+3+40+4, y+textOffset, color.getColor());
	}

}
