package me.superckl.prayers.common.entity.ai;

import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAIBurstShot extends EntityAIBase{

	/** The entity the AI instance has been applied to */
	private final EntityLiving entity;
	/** The entity (as a RangedAttackMob) the AI instance has been applied to. */
	private final IRangedAttackMob rangedAttackEntity;
	private EntityLivingBase attackTarget;
	/**
	 * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
	 * maxRangedAttackTime.
	 */
	private int rangedAttackTime;
	private double speed;
	private int field_75318_f;
	private int field_96561_g;
	/** The maximum time the AI has to wait before peforming another ranged attack. */
	private int maxRangedAttackTime;
	private float field_96562_i;
	private float field_82642_h;
	private final int shots;
	private int shotCounter;

	public EntityAIBurstShot(final IRangedAttackMob entity, final double speeed, final int interval, final float p_i1649_5_, final int shots)
	{
		this(entity, speeed, interval, interval, p_i1649_5_, shots);
	}

	public EntityAIBurstShot(final IRangedAttackMob entity, final double speed, final int p_i1650_4_, final int interval, final float p_i1650_6_, final int shots)
	{
		this.rangedAttackTime = -1;

		if (!(entity instanceof EntityLivingBase))
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		else
		{
			this.shots = shots;
			this.rangedAttackEntity = entity;
			this.entity = (EntityLiving)entity;
			this.speed = speed;
			this.field_96561_g = p_i1650_4_;
			this.maxRangedAttackTime = interval;
			this.field_96562_i = p_i1650_6_;
			this.field_82642_h = p_i1650_6_ * p_i1650_6_;
			this.setMutexBits(3);
		}
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute()
	{
		final EntityLivingBase entitylivingbase = this.entity.getAttackTarget();

		if (entitylivingbase == null)
			return false;
		else
		{
			this.attackTarget = entitylivingbase;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting()
	{
		return this.shouldExecute() || !this.entity.getNavigator().noPath();
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask()
	{
		this.attackTarget = null;
		this.field_75318_f = 0;
		this.rangedAttackTime = -1;
		this.shotCounter = 0;
	}

	/**
	 * Updates the task
	 */
	@Override
	public void updateTask()
	{
		final double d0 = this.entity.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
		final boolean flag = this.entity.getEntitySenses().canSee(this.attackTarget);

		if (flag)
			++this.field_75318_f;
		else
			this.field_75318_f = 0;

		/*if (d0 <= (double)this.field_82642_h && this.field_75318_f >= 20)
        {
            this.entity.getNavigator().clearPathEntity();
        }
        else
        {
            this.entity.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.speed);
        }*/
		if(this.rangedAttackTime > 0){
			this.rangedAttackTime--;
			return;
		}

		this.entity.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
		float f;

		if (((this.entity.worldObj.getWorldTime() % 5) == 0) && (this.shotCounter < this.shots))
		{
			if ((d0 > this.field_82642_h) || !flag)
				return;

			f = MathHelper.sqrt_double(d0) / this.field_96562_i;
			float f1 = f;

			if (f < 0.1F)
				f1 = 0.1F;

			if (f1 > 1.0F)
				f1 = 1.0F;

			this.rangedAttackEntity.attackEntityWithRangedAttack(this.attackTarget, f1);
			this.shotCounter++;
		}
		else if (this.shotCounter >= this.shots)
		{
			f = MathHelper.sqrt_double(d0) / this.field_96562_i;
			this.rangedAttackTime = MathHelper.floor_float((f * (this.maxRangedAttackTime - this.field_96561_g)) + this.field_96561_g);
			this.shotCounter = 0;

			if(this.entity instanceof EntityCreature){

				final Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards((EntityCreature) this.entity, 4, 1, Vec3.createVectorHelper(this.entity.getAttackTarget().posX, this.entity.getAttackTarget().posY, this.entity.getAttackTarget().posZ));
				if(vec3 != null)
					LogHelper.info(this.entity.getNavigator().tryMoveToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.speed));
			}

		}
	}

}
