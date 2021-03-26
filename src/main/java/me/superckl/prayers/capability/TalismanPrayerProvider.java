package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lombok.Getter;
import me.superckl.prayers.Prayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.registries.IRegistryDelegate;

public class TalismanPrayerProvider implements IInventoryPrayerProvider{

	@Getter
	private float currentPrayerPoints;
	private final Set<IRegistryDelegate<Prayer>> activePrayers = Sets.newIdentityHashSet();

	@Override
	public void activatePrayer(final Prayer prayer) {
		this.activePrayers.add(prayer.delegate);
	}

	@Override
	public void deactivatePrayer(final Prayer prayer) {
		this.activePrayers.remove(prayer.delegate);
	}

	@Override
	public boolean isPrayerActive(final Prayer prayer) {
		return this.activePrayers.contains(prayer.delegate);
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
	public float getMaxPrayerPoints() {
		return 200;
	}

	@Override
	public Collection<Prayer> getActivePrayers() {
		return this.activePrayers.stream().map(IRegistryDelegate::get).collect(Collectors.toSet());
	}

	@Override
	public void tick(final PlayerEntity player) {
		//TODO apply drain
	}

}
