package me.superckl.prayers.effects;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.effects.entity.MovementSpeed;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

@RequiredArgsConstructor
public class MovementSpeedEffect extends PrayerEffect{

	private final float modifier;

	@Override
	public boolean hasListener() {
		return false;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "movement_speed")), Math.round(this.modifier*100));
	}

	@Override
	public boolean canAttachTo(final LivingEntity entity) {
		return true;
	}

	@Override
	public MovementSpeed attachTo(final LivingEntity entity) {
		return new MovementSpeed(entity, this.modifier);
	}

}
