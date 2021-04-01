package me.superckl.prayers.effects;

import java.text.DecimalFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RequiredArgsConstructor
public class DamageEffect extends PrayerEffect{

	private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
	private final DamageType type;
	private final boolean isIncoming;
	@Getter
	private final boolean isPercentage;
	private final float amount;

	private ITextComponent description;

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		if(this.description == null) {
			char modifier;
			if(this.amount < 0)
				modifier = '-';
			else
				modifier = '+';
			this.description = new TranslationTextComponent(this.type.buildId(this.isIncoming),
					new StringBuilder().append(modifier).append(DamageEffect.FORMAT.format(Math.abs(this.amount)*100)).toString());
		}
		return this.description.copy();
	}

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent e) {
		final DamageSource s = e.getSource();
		if(s.isBypassArmor())
			return;
		boolean apply = this.type.matches(s);
		if(!apply)
			return;
		if(this.isIncoming)
			apply = this.getOwner().isActive(e.getEntityLiving());
		else if(s.getEntity() instanceof LivingEntity)
			apply = this.getOwner().isActive((LivingEntity) s.getEntity());
		if(!apply)
			return;
		if(this.isPercentage)
			e.setAmount(e.getAmount()*(1+this.amount));
		else
			e.setAmount(e.getAmount()+this.amount);
	}

	@RequiredArgsConstructor
	public enum DamageType{

		ALL("all"), MELEE("melee"), MAGIC("magic"), RANGE("range"), FIRE("fire"), NONE("none");

		@Getter
		private final String key;

		public static DamageType getType(final DamageSource source) {
			if(source.isMagic())
				return DamageType.MAGIC;
			if(source.isProjectile())
				return DamageType.RANGE;
			if(source.isFire())
				return DamageType.FIRE;
			if(source instanceof EntityDamageSource && !source.isExplosion())
				return DamageType.MELEE;
			return DamageType.NONE;
		}

		public boolean matches(final DamageSource source) {
			return this == DamageType.ALL || this == DamageType.getType(source);
		}

		public String buildId(final boolean damageIn) {
			final StringBuilder builder = new StringBuilder(this.getKey()).append("_damage");
			if(damageIn)
				builder.append("_in");
			else
				builder.append("_out");
			return Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, builder.toString()));
		}

	}

}
