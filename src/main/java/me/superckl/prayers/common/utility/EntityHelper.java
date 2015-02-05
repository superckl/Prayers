package me.superckl.prayers.common.utility;

import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class EntityHelper {

	public static BlockLocation tryFindSpawnLoc(final EnumCreatureType type, final World world, BlockLocation loc, final Random random){
		for(int i = 0; i < 10; i++){
			loc = loc.add(random.nextInt(4)-random.nextInt(4), random.nextInt(2)-random.nextInt(2), random.nextInt(4)-random.nextInt(4));
			if(SpawnerAnimals.canCreatureTypeSpawnAtLocation(type, world, loc.getX(), loc.getY(), loc.getZ()))
				return loc;
		}
		return null;
	}

}
