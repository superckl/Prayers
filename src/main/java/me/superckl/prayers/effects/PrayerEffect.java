package me.superckl.prayers.effects;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.effects.entity.EntitySpecificEffect;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class PrayerEffect{

	@Getter
	private Prayer owner;

	public void setOwner(final Prayer owner) {
		if(this.owner != null)
			throw new IllegalStateException("Owner has already been set!");
		this.owner = owner;
	}

	public boolean hasListener() {return false;}
	public boolean canAttachTo(final LivingEntity entity) {return false;}
	public EntitySpecificEffect<?> attachTo(final LivingEntity entity) {return EntitySpecificEffect.NONE;}

	public abstract IFormattableTextComponent getDescription();

	public interface ApplyEffectConsumer extends Consumer<LivingEntity>{

		ITextComponent getDescription();

	}

	@RequiredArgsConstructor
	public static class ApplyPotionEffect implements ApplyEffectConsumer{

		private final Supplier<EffectInstance> effect;

		@Override
		public void accept(final LivingEntity entity) {
			final EffectInstance instance = this.effect.get();
			if(entity.canBeAffected(instance) && !entity.hasEffect(instance.getEffect()))
				entity.addEffect(instance);
		}

		@Override
		public ITextComponent getDescription() {
			return new TranslationTextComponent(this.effect.get().getDescriptionId());
		}
	}

	@RequiredArgsConstructor
	public static class ApplyBurningEffect implements ApplyEffectConsumer{

		private final int duration;

		@Override
		public void accept(final LivingEntity entity) {
			if(!entity.fireImmune() && entity.getRemainingFireTicks() < this.duration)
				entity.setRemainingFireTicks(this.duration);
		}

		@Override
		public ITextComponent getDescription() {
			return new TranslationTextComponent("subtitles.entity.generic.burn");
		}

	}

}
