package me.superckl.prayers.common.utility;

import java.util.Comparator;

import me.superckl.prayers.common.prayer.EnumPrayers;

public class PrayerDrainComparator implements Comparator<EnumPrayers>{

	@Override
	public int compare(final EnumPrayers o1, final EnumPrayers o2) {
		final float d1 = o1.getDrain(), d2 = o2.getDrain();
		return d1 > d2 ? 1:d1 < d2 ? -1:0;
	}

}
