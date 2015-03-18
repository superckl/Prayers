package me.superckl.prayers.common.entity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.event.PortalSpawnEntityEvent;
import me.superckl.prayers.common.utility.GenericWeightedItem;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.NumberHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

@Getter
@Setter
public class EntityMonsterPortal extends Entity{

	private static final Map<Integer, List<GenericWeightedItem<Class<? extends Entity>>>> possibleSpawns = new HashMap<Integer, List<GenericWeightedItem<Class<? extends Entity>>>>();

	static{
		final List<GenericWeightedItem<Class<? extends Entity>>> levelOne = new ArrayList<GenericWeightedItem<Class<? extends Entity>>>();
		final List<GenericWeightedItem<Class<? extends Entity>>> levelTwo = new ArrayList<GenericWeightedItem<Class<? extends Entity>>>();
		final List<GenericWeightedItem<Class<? extends Entity>>> levelThree = new ArrayList<GenericWeightedItem<Class<? extends Entity>>>();
		final List<GenericWeightedItem<Class<? extends Entity>>> levelFour = new ArrayList<GenericWeightedItem<Class<? extends Entity>>>();
		levelOne.add(new GenericWeightedItem<Class<? extends Entity>>(2, EntityZombie.class));
		levelOne.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySkeleton.class));

		levelTwo.add(new GenericWeightedItem<Class<? extends Entity>>(3, EntityZombie.class));
		levelTwo.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySkeleton.class));
		levelTwo.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySpider.class));

		levelThree.add(new GenericWeightedItem<Class<? extends Entity>>(3, EntityZombie.class));
		levelThree.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySkeleton.class));
		levelThree.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySpider.class));
		levelThree.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntityCreeper.class));

		levelFour.add(new GenericWeightedItem<Class<? extends Entity>>(4, EntityZombie.class));
		levelFour.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySkeleton.class));
		levelFour.add(new GenericWeightedItem<Class<? extends Entity>>(1, EntitySpider.class));
		levelFour.add(new GenericWeightedItem<Class<? extends Entity>>(2, EntityCreeper.class));


		EntityMonsterPortal.possibleSpawns.put(1, levelOne);
		EntityMonsterPortal.possibleSpawns.put(2, levelTwo);
		EntityMonsterPortal.possibleSpawns.put(3, levelThree);
		EntityMonsterPortal.possibleSpawns.put(4, levelFour);
	}

	public EntityMonsterPortal(final World world) {
		super(world);
		this.setSize(1F, 1F);
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
			EntityMonsterPortal.possibleSpawns.put(level, new ArrayList<GenericWeightedItem<Class<? extends Entity>>>());
		EntityMonsterPortal.possibleSpawns.get(level).add(new GenericWeightedItem<Class<? extends Entity>>(weight, clazz));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.worldObj.isRemote)
			return;
		final int bound = (5-this.getLevel())*200;
		if(this.rand.nextInt(bound) == 0)
			this.trySpawnEntity();
	}

	public boolean trySpawnEntity(){
		final Class<? extends Entity> clazz = ((GenericWeightedItem<Class<? extends Entity>>) WeightedRandom.getRandomItem(this.rand, EntityMonsterPortal.possibleSpawns.get(this.getLevel()))).getItem();
		if(clazz == null)
			return false;
		try{
			final Constructor<? extends Entity> construct = clazz.getConstructor(World.class);
			Entity entity = construct.newInstance(this.worldObj);
			final PortalSpawnEntityEvent event = new PortalSpawnEntityEvent(this, entity);
			if(MinecraftForge.EVENT_BUS.post(event) || (event.getEntityToSpawn() == null))
				return false;
			if(entity == event.getEntityToSpawn()){
				if(entity instanceof EntityLiving)
					((EntityLiving) entity).onSpawnWithEgg(null);

				//TODO Apply random effects
			}else
				entity = event.getEntityToSpawn();
			final ForgeDirection dir = this.getDirection();
			entity.setLocationAndAngles(this.posX+dir.offsetX, this.posY+dir.offsetY, this.posZ+dir.offsetZ, NumberHelper.toYaw(dir), 0);
			this.worldObj.spawnEntityInWorld(entity);
		}catch(final Exception e){
			LogHelper.warn("Failed spawn entity from portal!");
			e.printStackTrace();
		}
		return false;
	}

}
