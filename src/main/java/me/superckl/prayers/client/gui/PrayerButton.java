package me.superckl.prayers.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.capability.IPrayerUser;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketActivatePrayer;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.SoundEvents;

public class PrayerButton extends ImageButton{

	private final Prayer prayer;
	private final Minecraft mc = Minecraft.getInstance();

	public PrayerButton(final Prayer prayer, final int x, final int y, final int width, final int height) {
		super(x, y, width, height, 0, 0, 0, prayer.getTexture(), 16, 16, button -> {});
		this.prayer = prayer;
	}

	@Override
	public void renderButton(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		if(this.prayer.isActive(this.mc.player))
			PrayerButton.drawOpenRect(matrixStack, this.x-2, this.y-2, 1, 20, 20);
		super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	public static void drawOpenRect(final MatrixStack matrixStack, final int x, final int y, int thickness, final int width, final int height) {
		thickness = Math.min(thickness, Math.min(width, height));
		final int white = 0xFFFFFFFF;
		AbstractGui.fill(matrixStack, x, y, x+width, y+thickness, white);
		AbstractGui.fill(matrixStack, x, y, x+thickness, y+height, white);
		AbstractGui.fill(matrixStack, x, y+height-thickness, x+width, y+height, white);
		AbstractGui.fill(matrixStack, x+width-thickness, y, x+width, y+height, white);
	}

	@Override
	public void renderToolTip(final MatrixStack matrixStack, final int mouseX, final int mouseY) {
		this.mc.screen.renderWrappedToolTip(matrixStack, this.prayer.getTooltipDescription(), mouseX, mouseY, this.mc.font);
	}

	@Override
	public void onPress() {
		final IPrayerUser user = IPrayerUser.getUser(this.mc.player);
		if(user.isPrayerActive(this.prayer)) {
			user.deactivatePrayer(this.prayer);
			super.playDownSound(this.mc.getSoundManager());
			PrayersPacketHandler.INSTANCE.sendToServer(PacketDeactivatePrayer.builder().entityID(this.mc.player.getId()).prayer(this.prayer).build());
		}else if(!user.canActivatePrayer(this.prayer))
			this.mc.getSoundManager().play(SimpleSound.forUI(SoundEvents.SHIELD_BREAK, 1.0F));
		else {
			user.activatePrayer(this.prayer);
			super.playDownSound(this.mc.getSoundManager());
			PrayersPacketHandler.INSTANCE.sendToServer(PacketActivatePrayer.builder().entityID(this.mc.player.getId()).prayer(this.prayer).build());
		}
	}

	@Override
	public void playDownSound(final SoundHandler handler) {
		//Override so it doesn't play the sound
	}

}
