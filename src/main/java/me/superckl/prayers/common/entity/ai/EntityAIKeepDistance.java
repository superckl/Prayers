package me.superckl.prayers.common.entity.ai;

import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIKeepDistance extends EntityAIBase{

	private final EntityCreature entity;
	private final int distanceToRun;
	private final double speed;
	private final double distanceToKeep;
	private final int delay;
	private int delayCounter;

	private Vec3 target;

	public EntityAIKeepDistance(final EntityCreature creature, final int distanceToRun, final double distanceToKeep, final double speed, final int delay) {
		this.entity = creature;
		this.distanceToRun = distanceToRun;
		this.distanceToKeep = distanceToKeep*distanceToKeep;
		this.speed = speed;
		this.delay = delay;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if((this.entity.getAttackTarget() == null) || ((this.entity instanceof EntityCreature) == false))
			return false;
		final double distX = this.entity.posX - this.entity.getAttackTarget().posX;
		final double distZ = this.entity.posZ - this.entity.getAttackTarget().posZ;
		final double dist = (distX*distX)+(distZ*distZ);
		if(dist > this.distanceToKeep)
			return false;
		final Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, this.distanceToRun, 1, Vec3.createVectorHelper(this.entity.getAttackTarget().posX, this.entity.getAttackTarget().posY, this.entity.getAttackTarget().posZ));
		if (vec3 == null)
			return false;
		else
		{
			this.target = vec3;
			return true;
		}
	}

	@Override
	public boolean continueExecuting()
	{
		/*final double distX = this.target.xCoord - this.entity.getAttackTarget().posX;
		final double distZ = this.target.zCoord - this.entity.getAttackTarget().posZ;
		final double dist = (distX*distX)+(distZ*distZ);*/
		return !this.entity.getNavigator().noPath()/* && dist > this.distanceToKeep*.8D*/;
	}

	@Override
	public void startExecuting(){
		LogHelper.info(this.entity.getNavigator().tryMoveToXYZ(this.target.xCoord, this.target.yCoord, this.target.zCoord, this.speed));
	}

}
