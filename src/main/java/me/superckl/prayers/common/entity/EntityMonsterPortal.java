package me.superckl.prayers.common.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Getter
@Setter
public class EntityMonsterPortal extends Entity{

	@Getter
	private static final Map<Integer, List<Class<? extends Entity>>> possibleSpawns = new HashMap<Integer, List<Class<? extends Entity>>>();

	public EntityMonsterPortal(final World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(5, new Integer(1));
		this.dataWatcher.addObject(6, new Integer(ForgeDirection.NORTH.ordinal()));
	}

	public void setLevel(final int level){
		this.dataWatcher.updateObject(5, new Integer(level));
	}

	public int getLevel(){
		return this.dataWatcher.getWatchableObjectInt(5);
	}

	public void setDirection(final ForgeDirection dir){
		this.dataWatcher.updateObject(6, new Integer(dir.ordinal()));
	}

	public ForgeDirection getDirection(){
		return ForgeDirection.values()[this.dataWatcher.getWatchableObjectInt(6)];
	}

	@Override
	protected void readEntityFromNBT(final NBTTagCompound tag) {
		if(tag.hasKey("portalLevel"))
			this.setLevel(tag.getInteger("portalLevel"));
		if(tag.hasKey("portalDirection"))
			this.setDirection(ForgeDirection.values()[tag.getInteger("portalDirection")]);
	}

	@Override
	protected void writeEntityToNBT(final NBTTagCompound tag) {
		tag.setInteger("portalLevel", this.getLevel());
		tag.setInteger("portalDirection", this.getDirection().ordinal());
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	@Override
	public boolean isEntityInvulnerable()
	{
		return true;
	}

	@Override
	public boolean isPushedByWater()
	{
		return false;
	}

	public static void registerPossibleSpawn(final Class<? extends Entity> clazz, final int weight, final int level){
		if(!EntityMonsterPortal.possibleSpawns.containsKey(level))
			EntityMonsterPortal.possibleSpawns.put(level, new ArrayList<Class<? extends Entity>>());
		final List<Class<? extends Entity>> list = EntityMonsterPortal.possibleSpawns.get(level);
		for(int i = 0; i < weight; i++)
			list.add(clazz);
	}

}
