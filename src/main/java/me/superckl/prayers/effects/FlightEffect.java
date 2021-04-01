package me.superckl.prayers.effects;

import me.superckl.prayers.Prayers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FlightEffect extends PrayerEffect{

	@Override
	public boolean hasListener() {
		return false;
	}

	@Override
	public ITextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "flight")));
	}

}
