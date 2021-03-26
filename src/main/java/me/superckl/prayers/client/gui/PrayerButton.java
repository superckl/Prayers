package me.superckl.prayers.client.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.capability.ILivingPrayerUser;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketActivatePrayer;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.IRegistryDelegate;

public class PrayerButton extends ImageButton{

	private final IRegistryDelegate<Prayer> prayer;
	private final Minecraft mc = Minecraft.getInstance();

	public PrayerButton(final Prayer prayer, final int x, final int y, final int width, final int height) {
		super(x, y, width, height, 0, 0, 0, prayer.getTexture(), 16, 16, button -> {});
		this.prayer = prayer.delegate;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void renderButton(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		final ILivingPrayerUser user = ILivingPrayerUser.getUser(this.mc.player);
		if(user.isPrayerActive(this.prayer.get()))
			PrayerButton.drawOpenRect(matrixStack, this.x-2, this.y-2, 1, 20, 20);
		if(!user.canActivatePrayer(this.prayer.get()))
			RenderSystem.color3f(0.2F, 0.2F, 0.2F);
		super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		RenderSystem.color3f(1, 1, 1);
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
		final ILivingPrayerUser user = ILivingPrayerUser.getUser(this.mc.player);
		final Prayer prayer = this.prayer.get();
		if(!user.canActivatePrayer(prayer)) {
			final List<ITextComponent> tooltip = Lists.newArrayList();
			tooltip.add(new StringTextComponent("Unknown").withStyle(TextFormatting.OBFUSCATED, TextFormatting.GRAY));
			tooltip.add(new StringTextComponent("This prayer eludes you...").withStyle(TextFormatting.GRAY));
			if(prayer.isRequiresTome() && !user.isUnlocked(prayer))
				tooltip.add(new StringTextComponent("Requires tome to unlock").withStyle(TextFormatting.DARK_GRAY));
			else if(user.getPrayerLevel() < prayer.getLevel())
				tooltip.add(new StringTextComponent("Requires level "+prayer.getLevel()).withStyle(TextFormatting.DARK_GRAY));
			this.mc.screen.renderWrappedToolTip(matrixStack, tooltip, mouseX, mouseY, this.mc.font);
		} else
			this.mc.screen.renderWrappedToolTip(matrixStack, this.prayer.get().getTooltipDescription(), mouseX, mouseY, this.mc.font);
	}

	@Override
	public void onPress() {
		final ILivingPrayerUser user = ILivingPrayerUser.getUser(this.mc.player);
		if(user.isPrayerActive(this.prayer.get())) {
			user.deactivatePrayer(this.prayer.get());
			super.playDownSound(this.mc.getSoundManager());
			PrayersPacketHandler.INSTANCE.sendToServer(PacketDeactivatePrayer.builder().entityID(this.mc.player.getId()).prayer(this.prayer.get()).build());
		}else if(!user.canActivatePrayer(this.prayer.get()))
			this.mc.getSoundManager().play(SimpleSound.forUI(SoundEvents.SHIELD_BREAK, 1.0F));
		else {
			user.activatePrayer(this.prayer.get());
			super.playDownSound(this.mc.getSoundManager());
			PrayersPacketHandler.INSTANCE.sendToServer(PacketActivatePrayer.builder().entityID(this.mc.player.getId()).prayer(this.prayer.get()).build());
		}
	}

	@Override
	public void playDownSound(final SoundHandler handler) {
		//Override so it doesn't play the sound
	}

}
