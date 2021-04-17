package me.superckl.prayers.effects;

import lombok.Getter;
import me.superckl.prayers.effects.entity.EntitySpecificEffect;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.IFormattableTextComponent;

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

}
