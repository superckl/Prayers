package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.superckl.prayers.effects.entity.EntitySpecificEffect;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.registries.IRegistryDelegate;

public abstract class LivingPrayerUser<T extends LivingEntity> extends TickablePrayerProvider<T>{

	protected final T ref;

	protected final Multimap<IRegistryDelegate<Prayer>, EntitySpecificEffect<?>> effects = ArrayListMultimap.create();

	public LivingPrayerUser(final T ref) {
		this.ref = ref;
	}

	@Override
	public boolean activatePrayer(final Prayer prayer) {
		if(super.activatePrayer(prayer)) {
			this.attachEffects(prayer);
			return true;
		}
		return false;
	}

	@Override
	public boolean deactivatePrayer(final Prayer prayer) {
		if(super.deactivatePrayer(prayer)) {
			this.detachEffects(prayer);
			return true;
		}
		return false;
	}

	@Override
	public boolean deactivateAllPrayers() {
		final Iterator<Prayer> it = this.getActivePrayers().iterator();
		boolean removed = false;
		while(it.hasNext())
			removed = this.deactivatePrayer(it.next()) || removed;
		return removed;
	}

	public boolean attachEffects(final Prayer prayer) {
		if(!this.effects.containsKey(prayer.delegate)) {
			final List<EntitySpecificEffect<?>> effects = prayer.attachEffects(this.ref);
			if(!effects.isEmpty()) {
				effects.forEach(EntitySpecificEffect::onActivate);
				this.effects.putAll(prayer.delegate, effects);
				return true;
			}
		}
		return false;
	}

	public boolean detachEffects(final Prayer prayer) {
		final Collection<EntitySpecificEffect<?>> effects = this.effects.removeAll(prayer.delegate);
		if(!effects.isEmpty()) {
			effects.forEach(EntitySpecificEffect::onDeactivate);
			return true;
		}
		return false;
	}

}
