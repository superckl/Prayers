package me.superckl.prayers.effects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RequiredArgsConstructor
public class DamageEffect extends PrayerEffect{

	private final DamageType type;
	private final boolean isIncoming;
	@Getter
	private final boolean isPercentage;
	private final float amount;

	private String description;

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public String getDescription() {
		if(this.description == null) {
			final StringBuilder builder = new StringBuilder(this.type.getName()).append(" Damage ");
			if(this.isIncoming)
				builder.append("In ");
			else
				builder.append("Out ");
			if(this.amount < 0)
				builder.append('-');
			else
				builder.append('+');
			if(this.isPercentage)
				builder.append(Math.round(Math.abs(this.amount)*100)).append('%');
			else
				builder.append(String.format("%.2f", Math.abs(this.amount)));
			this.description = builder.toString();
		}
		return this.description;
	}

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent e) {
		final DamageSource s = e.getSource();
		if(s.isDamageAbsolute())
			return;
		boolean apply = this.type.matches(s);
		if(!apply)
			return;
		if(this.isIncoming)
			apply = apply && this.getOwner().isActive(e.getEntity());
		else if(s.getTrueSource() != null)
			apply = apply && this.getOwner().isActive(s.getTrueSource());
		if(!apply)
			return;
		if(this.isPercentage)
			e.setAmount(e.getAmount()*(1+this.amount));
		else
			e.setAmount(e.getAmount()+this.amount);
	}

	@RequiredArgsConstructor
	public enum DamageType{

		ALL("All"), MELEE("Melee"), MAGIC("Magic"), RANGE("Range"), FIRE("Fire"), NONE("None");

		@Getter
		private final String name;

		public static DamageType getType(final DamageSource source) {
			if(source.isMagicDamage())
				return DamageType.MAGIC;
			else if(source.isProjectile())
				return DamageType.RANGE;
			else if(source.isFireDamage())
				return DamageType.FIRE;
			else if(source instanceof EntityDamageSource && !source.isExplosion())
				return DamageType.MELEE;
			else
				return DamageType.NONE;
		}

		public boolean matches(final DamageSource source) {
			return this == DamageType.ALL || this == DamageType.getType(source);
		}

	}

}
