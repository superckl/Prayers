package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lombok.Getter;
import me.superckl.prayers.Prayer;
import net.minecraftforge.registries.IRegistryDelegate;

public class DefaultPrayerUser implements IPrayerUser{

	@Getter
	private float currentPrayerPoints;
	@Getter
	private int prayerLevel;
	@Getter
	private float maxPointsBoost;
	private float maxPoints;
	private float xp;

	private final Set<IRegistryDelegate<Prayer>> activePrayers = Sets.newIdentityHashSet();
	private final Set<IRegistryDelegate<Prayer>> unlockedPrayers = Sets.newHashSet();

	public DefaultPrayerUser() {
		this.prayerLevel = 1;
		this.maxPointsBoost = 0;
		this.maxPoints = this.computeMaxPoints();
		this.currentPrayerPoints = this.maxPoints;
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
	public float getMaxPrayerPoints() {
		return this.maxPoints;
	}

	@Override
	public float addMaxPointsBoost(final float boost) {
		this.maxPointsBoost += boost;
		if(this.maxPointsBoost < 0)
			this.maxPointsBoost = 0;
		this.maxPoints = this.computeMaxPoints();
		return this.maxPointsBoost;
	}

	@Override
	public float setMaxPointsBoost(final float boost) {
		if(boost < 0)
			this.maxPointsBoost = 0;
		else
			this.maxPointsBoost = boost;
		this.maxPoints = this.computeMaxPoints();
		return this.maxPointsBoost;
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
	public int setPrayerLevel(final int level) {
		if(level < 1)
			this.prayerLevel = 1;
		else
			this.prayerLevel = level;
		this.maxPoints = this.computeMaxPoints();
		return this.prayerLevel;
	}

	@Override
	public Collection<Prayer> getActivePrayers() {
		return this.activePrayers.stream().map(IRegistryDelegate::get).collect(Collectors.toSet());
	}

	protected float computeMaxPoints() {
		return 10*this.prayerLevel+this.maxPointsBoost;
	}

	@Override
	public int giveXP(final float xp) {
		this.xp += xp;
		this.computeLevel();
		return this.prayerLevel;
	}

	@Override
	public void setXP(final float xp) {
		this.xp = xp;
	}

	@Override
	public float getXP() {
		return this.xp;
	}

	@Override
	public boolean unlockPrayer(final Prayer prayer) {
		if(!prayer.isRequiresTome())
			return false;
		return this.unlockedPrayers.add(prayer.delegate);
	}

	@Override
	public boolean isUnlocked(final Prayer prayer) {
		return this.unlockedPrayers.contains(prayer.delegate);
	}

	@Override
	public Collection<Prayer> getUnlockedPrayers() {
		return this.unlockedPrayers.stream().map(IRegistryDelegate::get).collect(Collectors.toSet());
	}

}
