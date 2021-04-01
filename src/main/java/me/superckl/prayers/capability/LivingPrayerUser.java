package me.superckl.prayers.capability;

import java.util.Iterator;

import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.LivingEntity;

public abstract class LivingPrayerUser<T extends LivingEntity> extends TickablePrayerProvider<T>{

	protected final T ref;

	public LivingPrayerUser(final T ref) {
		this.ref = ref;
	}

	@Override
	public boolean activatePrayer(final Prayer prayer) {
		if(super.activatePrayer(prayer)) {
			prayer.onActivate(this.ref);
			return true;
		}
		return false;
	}

	@Override
	public boolean deactivatePrayer(final Prayer prayer) {
		if(super.deactivatePrayer(prayer)) {
			prayer.onDeactivate(this.ref);
			return true;
		}
		return false;
	}

	@Override
	public boolean deactivateAllPrayers() {
		final Iterator<Prayer> it = this.getActivePrayers().iterator();
		boolean removed = false;
		while(it.hasNext())
			removed = removed || this.deactivatePrayer(it.next());
		return removed;
	}

}
