package me.superckl.prayers.effects;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RequiredArgsConstructor
public class DigSpeedEffect extends PrayerEffect{

	private final float modifier;

	@SubscribeEvent
	public void onDigSpeedCheck(final PlayerEvent.BreakSpeed e) {
		if(e.getPlayer().isAlive() && this.getOwner().isActive(e.getPlayer()))
			e.setNewSpeed(e.getNewSpeed()*(1+this.modifier));
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "dig_speed")), MathHelper.floor(this.modifier*100));
	}

}
