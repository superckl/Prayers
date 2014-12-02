package me.superckl.prayercraft.common.utility;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayercraft.common.reference.Prayers;

public class PrayerHelper {

	public static List<Prayers> transateBits(int bits){
		final List<Prayers> prayers = new ArrayList<Prayers>();
		if(bits == 0)
			return prayers;
		int ordinal = 0;
		final Prayers[] values = Prayers.values();
		while(bits != 0){
			if((bits & 1) == 1)
				prayers.add(values[ordinal]);
			bits >>= 1;
			ordinal++;
		}
		return prayers;
	}

	public static int calculateXP(final int level){
		int xp = 0;
		for(int i = 1; i < level; i++)
			xp += Math.pow(2D, (i)/7D);
		return xp*75;
	}

}
