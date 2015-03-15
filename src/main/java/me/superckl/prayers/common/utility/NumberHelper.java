package me.superckl.prayers.common.utility;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class NumberHelper {

	public static final double SIN_90 = Math.sin(Math.toRadians(90D));
	public static final double COS_90 = Math.cos(Math.toRadians(90D));

	public static final double SIN_180 = Math.sin(Math.toRadians(180D));
	public static final double COS_180 = Math.cos(Math.toRadians(180D));

	public static final double SIN_270 = Math.sin(Math.toRadians(270D));
	public static final double COS_270 = Math.cos(Math.toRadians(270D));

	public static boolean equals(final Vec3 vec0, final Vec3 vec1){
		return (vec0.xCoord == vec1.xCoord) && (vec0.yCoord == vec1.yCoord) && (vec0.zCoord == vec1.zCoord);
	}

	public static int[] fillIncreasing(final int max){
		final int[] array = new int[max+1];
		for(int i = 0; i <= max; i++)
			array[i] = i;
		return array;
	}

	public static float toYaw(final ForgeDirection dir){
		switch(dir){
		case SOUTH:
			return 0;
		case WEST:
			return 90;
		case NORTH:
			return 180;
		case EAST:
			return 270;
		default:
			return 0;
		}
	}

}
