package me.superckl.prayers.effects;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.effects.entity.PlayerFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FlightEffect extends PrayerEffect{

	@Override
	public boolean hasListener() {
		return false;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "flight")));
	}

	@Override
	public boolean canAttachTo(final LivingEntity entity) {
		return entity instanceof PlayerEntity;
	}

	@Override
	public PlayerFlight attachTo(final LivingEntity entity) {
		return new PlayerFlight((PlayerEntity) entity);
	}

}
