package me.superckl.prayers.effects;

import java.util.List;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RequiredArgsConstructor
public class ApplyToNearbyEffect extends PrayerEffect{

	private final ApplyEffectConsumer consumer;
	private final int radius;
	private final float chance;

	@SubscribeEvent
	public void entityTick(final LivingUpdateEvent e) {
		if(e.getEntityLiving().isAlive() && this.getOwner().isActive(e.getEntityLiving())) {
			final List<LivingEntity> entities = e.getEntity().level.getEntitiesOfClass(LivingEntity.class, e.getEntityLiving().getBoundingBox().inflate(this.radius));
			for(final LivingEntity entity:entities)
				if(entity != e.getEntityLiving() && e.getEntityLiving().getRandom().nextFloat() < this.chance)
					this.consumer.accept(entity);
		}
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect",
				new ResourceLocation(Prayers.MOD_ID, "nearby_effect")), this.consumer.getDescription());
	}

}
