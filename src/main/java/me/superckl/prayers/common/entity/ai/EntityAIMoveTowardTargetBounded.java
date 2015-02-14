package me.superckl.prayers.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardTargetBounded extends EntityAIBase{

	private final EntityCreature entity;
	private final double distance;
	private final double speed;
	private EntityLivingBase targetEntity;

	private Vec3 target;

	private boolean reached;

	public EntityAIMoveTowardTargetBounded(final EntityCreature entity, final double distance, final double speed){
		this.entity = entity;
		this.distance = distance;
		this.speed = speed;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		this.targetEntity = this.entity.getAttackTarget();

		if (this.targetEntity == null)
			return false;
		else
		{
			final float dist = this.entity.getDistanceToEntity(this.targetEntity);
			if(dist <= (this.distance+1))
				return false;
			final Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, (int) (dist-this.distance), 1, Vec3.createVectorHelper(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));

			if (vec3 == null)
				return false;
			else
			{
				this.target = vec3;
				this.reached = false;
				return true;
			}
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return !this.reached && !this.entity.isCollidedHorizontally;
	}

	@Override
	public void updateTask() {
		final Vec3 thisPos = Vec3.createVectorHelper(this.entity.posX, this.entity.posY, this.entity.posZ);
		final Vec3 diff = thisPos.subtract(Vec3.createVectorHelper(this.target.xCoord, this.target.yCoord, this.target.zCoord));
		final Vec3 motionVec = diff.normalize();
		this.entity.motionX = motionVec.xCoord*this.speed;
		this.entity.motionY = motionVec.yCoord*this.speed;
		this.entity.motionZ = motionVec.zCoord*this.speed;
		this.reached = diff.lengthVector() < 1D;
	}

}
