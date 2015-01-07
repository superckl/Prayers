package me.superckl.prayers.common.utility;

import net.minecraft.util.Vec3;

public class NumberHelper {

	public static boolean equals(final Vec3 vec0, final Vec3 vec1){
		return (vec0.xCoord == vec1.xCoord) && (vec0.yCoord == vec1.yCoord) && (vec0.zCoord == vec1.zCoord);
	}

}
