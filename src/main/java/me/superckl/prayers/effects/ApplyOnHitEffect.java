package me.superckl.prayers.effects;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RequiredArgsConstructor
public class ApplyOnHitEffect extends PrayerEffect{

	private final ApplyEffectConsumer consumer;
	private final boolean direct;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(final LivingDamageEvent e) {
		if(e.getEntityLiving().isAlive() && this.getOwner().isActive(e.getEntityLiving())) {
			final Entity source = this.direct ? e.getSource().getDirectEntity():e.getSource().getEntity();
			if(source instanceof LivingEntity)
				this.consumer.accept((LivingEntity) source);
		}
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect",
				new ResourceLocation(Prayers.MOD_ID, "hit_effect")), this.consumer.getDescription());
	}

}
