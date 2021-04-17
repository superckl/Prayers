package me.superckl.prayers.effects;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.effects.entity.Glowing;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GlowingEffect extends PrayerEffect{

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "glowing")));
	}

	@Override
	public boolean canAttachTo(final LivingEntity entity) {
		return true;
	}

	@Override
	public Glowing attachTo(final LivingEntity entity) {
		return new Glowing(entity);
	}

}
