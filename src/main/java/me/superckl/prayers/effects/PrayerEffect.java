package me.superckl.prayers.effects;

import lombok.Getter;
import me.superckl.prayers.Prayer;

public abstract class PrayerEffect{

	@Getter
	private Prayer owner;

	public void setOwner(final Prayer owner) {
		if(this.owner != null)
			throw new IllegalStateException("Owner has already been set!");
		this.owner = owner;
	}

	public abstract boolean hasListener();

	public abstract String getDescription();

}