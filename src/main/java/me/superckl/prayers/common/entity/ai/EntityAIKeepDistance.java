package me.superckl.prayers.common.entity.ai;

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

	private boolean reached;

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
			this.reached = false;
			return true;
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return !this.reached && !this.entity.isCollidedHorizontally;
	}

	@Override
	public void updateTask() {
		if(this.delayCounter++ < this.delay)
			return;
		final Vec3 thisPos = Vec3.createVectorHelper(this.entity.posX, this.entity.posY, this.entity.posZ);
		final Vec3 diff = thisPos.subtract(Vec3.createVectorHelper(this.target.xCoord, this.target.yCoord, this.target.zCoord));
		final Vec3 motionVec = diff.normalize();
		this.entity.motionX = motionVec.xCoord*this.speed;
		this.entity.motionY = motionVec.yCoord*this.speed;
		this.entity.motionZ = motionVec.zCoord*this.speed;
		this.reached = diff.lengthVector() < 1D;
		if(this.reached)
			this.delayCounter = 0;
	}

}
