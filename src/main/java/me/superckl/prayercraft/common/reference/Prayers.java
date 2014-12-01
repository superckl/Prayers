package me.superckl.prayercraft.common.reference;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Prayers {

	PROTECT_MELEE(1),
	PROTECT_RANGE(2),
	PROTECT_MAGIC(4);

	@Getter
	private final int bit;

	public static Prayers getByBit(final int bit){
		for(final Prayers prayer:Prayers.values())
			if(prayer.bit == bit)
				return prayer;
		return null;
	}

}
