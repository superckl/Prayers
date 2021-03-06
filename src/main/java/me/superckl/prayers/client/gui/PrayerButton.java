package me.superckl.prayers.client.gui;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.capability.PlayerPrayerUser.Result;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketActivatePrayer;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import me.superckl.prayers.prayer.Prayer;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IRegistryDelegate;

public class PrayerButton extends ImageButton{

	private final IRegistryDelegate<Prayer> prayer;
	private final ItemStack talisman = new ItemStack(ModItems.TALISMAN::get);

	public PrayerButton(final Prayer prayer, final int x, final int y, final int width, final int height) {
		super(x, y, width, height, 0, 0, 0, prayer.getTexture(), 16, 16, button -> {});
		this.prayer = prayer.delegate;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void renderButton(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer());
		if(user.isPrayerActive(this.prayer.get(), false))
			PrayerButton.drawOpenRect(matrixStack, this.x-2, this.y-2, 1, 20, 20);
		final Result res = user.canActivatePrayer(this.prayer.get());
		final float alpha = user.isPrayerActive(this.prayer.get()) ? 1:res.getRenderAlpha();
		RenderSystem.color3f(alpha, alpha, alpha);
		super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		RenderSystem.color3f(1, 1, 1);
		if(res == Result.NO_ITEM) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef(this.x+8, this.y+9, 0);
			RenderSystem.scalef(0.6F, 0.6F, 1);
			ClientHelper.getItemRenderer().renderAndDecorateItem(this.talisman, 0, 0);
			RenderSystem.popMatrix();
		}
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
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer());
		final Prayer prayer = this.prayer.get();
		final Result res = user.isPrayerActive(prayer, false) ? Result.YES:user.canActivatePrayer(prayer);
		final List<ITextComponent> tooltip = Lists.newArrayList();
		switch(res) {
		case NO_DISABLED:
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("disabled")).withStyle(TextFormatting.DARK_RED));
			break;
		case NO_EXLCUDE:
			tooltip.add(prayer.getName());
			tooltip.addAll(prayer.getTooltipDescription());
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.excluded")).withStyle(TextFormatting.RED));
			break;
		case NO_LEVEL:
			tooltip.add(new StringTextComponent("Unknown").withStyle(TextFormatting.OBFUSCATED, TextFormatting.GRAY));
			tooltip.addAll(this.withStyle(prayer.getTooltipDescription(), TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.obfuscated")).withStyle(TextFormatting.DARK_GRAY));
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.require_level"),prayer.getLevel()).withStyle(TextFormatting.DARK_GRAY));
			break;
		case NO_POINTS:
			tooltip.add(prayer.getName());
			tooltip.addAll(prayer.getTooltipDescription());
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.no_points")).withStyle(TextFormatting.RED));
			break;
		case NO_TOME:
			tooltip.add(new StringTextComponent("Unknown").withStyle(TextFormatting.OBFUSCATED, TextFormatting.GRAY));
			tooltip.addAll(this.withStyle(prayer.getTooltipDescription(), TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.obfuscated")).withStyle(TextFormatting.DARK_GRAY));
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.tome")).withStyle(TextFormatting.DARK_GRAY));
			if(prayer.getLevel() > user.getPrayerLevel())
				tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.require_level"),prayer.getLevel()).withStyle(TextFormatting.DARK_GRAY));
			break;
		case NO_ITEM:
			tooltip.add(prayer.getName());
			tooltip.addAll(prayer.getTooltipDescription());
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("prayer.talisman")).withStyle(TextFormatting.GREEN));
			break;
		case YES:
			tooltip.add(prayer.getName());
			tooltip.addAll(prayer.getTooltipDescription());
			break;
		case NO_LOCKED:
			break;
		default:
			break;
		}
		ClientHelper.getScreen().renderWrappedToolTip(matrixStack, tooltip, mouseX, mouseY, ClientHelper.getFontRenderer());
	}

	@Override
	public void onPress() {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer());
		if(user.isPrayerActive(this.prayer.get(), false)) {
			user.deactivatePrayer(this.prayer.get());
			super.playDownSound(ClientHelper.getSoundManager());
			PrayersPacketHandler.INSTANCE.sendToServer(PacketDeactivatePrayer.builder().entityID(ClientHelper.getPlayer().getId()).prayer(this.prayer.get()).build());
		}else if(user.canActivatePrayer(this.prayer.get()) != Result.YES)
			ClientHelper.getSoundManager().play(SimpleSound.forUI(SoundEvents.SHIELD_BREAK, 1.0F));
		else {
			user.activatePrayer(this.prayer.get());
			super.playDownSound(ClientHelper.getSoundManager());
			PrayersPacketHandler.INSTANCE.sendToServer(PacketActivatePrayer.builder().entityID(ClientHelper.getPlayer().getId()).prayer(this.prayer.get()).build());
		}
	}

	@Override
	public void playDownSound(final SoundHandler handler) {
		//Override so it doesn't play the sound
	}

	private final List<ITextComponent> withStyle(final List<ITextComponent> components, final TextFormatting style){
		return components.stream().map(text-> text.copy().withStyle(style)).collect(Collectors.toList());
	}

}
