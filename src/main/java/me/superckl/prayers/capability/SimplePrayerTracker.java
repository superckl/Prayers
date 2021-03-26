package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lombok.Getter;
import me.superckl.prayers.Prayer;
import net.minecraftforge.registries.IRegistryDelegate;

public abstract class SimplePrayerTracker<T> implements ITickablePrayerProvider<T>{

	@Getter
	protected float currentPrayerPoints;
	private final Set<IRegistryDelegate<Prayer>> activePrayers = Sets.newIdentityHashSet();

	public SimplePrayerTracker() {
		this.currentPrayerPoints = this.getMaxPrayerPoints();
	}

	@Override
	public float setCurrentPrayerPoints(final float currentPoints) {
		if(currentPoints < 0)
			this.currentPrayerPoints = 0;
		else
			this.currentPrayerPoints = currentPoints;
		return this.currentPrayerPoints;
	}

	@Override
	public void activatePrayer(final Prayer prayer) {
		this.activePrayers.add(prayer.delegate);
	}

	@Override
	public void deactivatePrayer(final Prayer prayer) {
		this.activePrayers.remove(prayer.delegate);
	}

	@Override
	public void deactivateAllPrayers() {
		this.activePrayers.clear();
	}

	@Override
	public boolean isPrayerActive(final Prayer prayer) {
		return this.activePrayers.contains(prayer.delegate);
	}

	@Override
	public Collection<Prayer> getActivePrayers() {
		return this.activePrayers.stream().map(IRegistryDelegate::get).collect(Collectors.toSet());
	}

}
