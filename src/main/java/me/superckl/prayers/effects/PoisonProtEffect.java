package me.superckl.prayers.effects;

import me.superckl.prayers.Prayers;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PoisonProtEffect extends PrayerEffect{

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPoisonCheck(final PotionApplicableEvent e) {
		if(e.getPotionEffect().getEffect() == Effects.POISON && this.getOwner().isActive(e.getEntityLiving()))
			e.setResult(Result.DENY);
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "poison_prot")));
	}

}
