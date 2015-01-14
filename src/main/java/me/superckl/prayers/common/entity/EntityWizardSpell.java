package me.superckl.prayers.common.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityWizardSpell extends EntityThrowable{

	private int field_145795_e = -1;
	private int field_145793_f = -1;
	private int field_145794_g = -1;
	private Block field_145796_h;
	private int ticksAlive;
	private int ticksInAir;
	public double accelerationX;
	public double accelerationY;
	public double accelerationZ;
	@Setter
	@Getter
	private float baseDamage = 6F;

	public EntityWizardSpell(final World world, final EntityLivingBase thrower, double x, double y, double z){
		super(world, thrower);
		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(thrower.posX, thrower.posY, thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		x += this.rand.nextGaussian() * 0.4D;
		y += this.rand.nextGaussian() * 0.4D;
		z += this.rand.nextGaussian() * 0.4D;
		final double d3 = MathHelper.sqrt_double((x * x) + (y * y) + (z * z));
		this.accelerationX = (x / d3) * 0.1D;
		this.accelerationY = (y / d3) * 0.1D;
		this.accelerationZ = (z / d3) * 0.1D;
	}

	public EntityWizardSpell(final World world, final EntityLivingBase thrower, final double x, final double y, final double z, final float damage){
		this(world, thrower, x, y ,z);
		this.baseDamage = damage;
	}

	public EntityWizardSpell(final World world){
		super(world);
		this.setSize(0.5F, 0.5F);
		this.yOffset = 0.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote && (((this.getThrower() != null) && this.getThrower().isDead) || !this.worldObj.blockExists((int)this.posX, (int)this.posY, (int)this.posZ)))
			this.setDead();
		else
		{
			this.onEntityUpdate();
			this.setFire(1);

			if (this.inGround)
			{
				if (this.worldObj.getBlock(this.field_145795_e, this.field_145793_f, this.field_145794_g) == this.field_145796_h)
				{
					++this.ticksAlive;

					if (this.ticksAlive == 600)
						this.setDead();

					return;
				}

				this.inGround = false;
				this.motionX *= this.rand.nextFloat() * 0.2F;
				this.motionY *= this.rand.nextFloat() * 0.2F;
				this.motionZ *= this.rand.nextFloat() * 0.2F;
				this.ticksAlive = 0;
				this.ticksInAir = 0;
			} else
				++this.ticksInAir;

			Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			Vec3 vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
			vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if (movingobjectposition != null)
				vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);

			Entity entity = null;
			final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;

			for (int i = 0; i < list.size(); ++i)
			{
				final Entity entity1 = (Entity)list.get(i);

				if (entity1.canBeCollidedWith() && (!entity1.isEntityEqual(this.getThrower()) || (this.ticksInAir >= 25)))
				{
					final float f = 0.3F;
					final AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(f, f, f);
					final MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

					if (movingobjectposition1 != null)
					{
						final double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

						if ((d1 < d0) || (d0 == 0.0D))
						{
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null)
				movingobjectposition = new MovingObjectPosition(entity);

			if (movingobjectposition != null)
				this.onImpact(movingobjectposition);

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			final float f1 = MathHelper.sqrt_double((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
			this.rotationYaw = (float)((Math.atan2(this.motionZ, this.motionX) * 180.0D) / Math.PI) + 90.0F;

			for (this.rotationPitch = (float)((Math.atan2(f1, this.motionY) * 180.0D) / Math.PI) - 90.0F; (this.rotationPitch - this.prevRotationPitch) < -180.0F; this.prevRotationPitch -= 360.0F)
				;

			while ((this.rotationPitch - this.prevRotationPitch) >= 180.0F)
				this.prevRotationPitch += 360.0F;

			while ((this.rotationYaw - this.prevRotationYaw) < -180.0F)
				this.prevRotationYaw -= 360.0F;

			while ((this.rotationYaw - this.prevRotationYaw) >= 180.0F)
				this.prevRotationYaw += 360.0F;

			this.rotationPitch = this.prevRotationPitch + ((this.rotationPitch - this.prevRotationPitch) * 0.2F);
			this.rotationYaw = this.prevRotationYaw + ((this.rotationYaw - this.prevRotationYaw) * 0.2F);
			float f2 = this.getMotionFactor();

			if (this.isInWater())
			{
				for (int j = 0; j < 4; ++j)
				{
					final float f3 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX - (this.motionX * f3), this.posY - (this.motionY * f3), this.posZ - (this.motionZ * f3), this.motionX, this.motionY, this.motionZ);
				}

				f2 = 0.8F;
			}

			this.motionX += this.accelerationX;
			this.motionY += this.accelerationY;
			this.motionZ += this.accelerationZ;
			this.motionX *= f2;
			this.motionY *= f2;
			this.motionZ *= f2;
			this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	@Override
	protected void onImpact(final MovingObjectPosition pos) {
		if (!this.worldObj.isRemote)
		{
			if (pos.entityHit != null)
				pos.entityHit.attackEntityFrom(new EntityDamageSourceIndirect("wizardspell", this.getThrower(), this).setProjectile().setMagicDamage(), this.baseDamage);
			else
			{
				//TODO big poof or something
			}

			this.setDead();
		}
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound p_70037_1_)
	{
		this.field_145795_e = p_70037_1_.getShort("xTile");
		this.field_145793_f = p_70037_1_.getShort("yTile");
		this.field_145794_g = p_70037_1_.getShort("zTile");
		this.field_145796_h = Block.getBlockById(p_70037_1_.getByte("inTile") & 255);
		this.inGround = p_70037_1_.getByte("inGround") == 1;

		if (p_70037_1_.hasKey("direction", 9))
		{
			final NBTTagList nbttaglist = p_70037_1_.getTagList("direction", 6);
			this.motionX = nbttaglist.func_150309_d(0);
			this.motionY = nbttaglist.func_150309_d(1);
			this.motionZ = nbttaglist.func_150309_d(2);
		} else
			this.setDead();
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public float getCollisionBorderSize()
	{
		return 1.0F;
	}

	protected float getMotionFactor() {
		return 1F;
	}

	@Override
	public boolean attackEntityFrom(final DamageSource p_70097_1_, final float p_70097_2_) {
		return false; //TODO special wand or prayer to deflect?
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	@Override
	public float getBrightness(final float p_70013_1_)
	{
		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(final float p_70070_1_)
	{
		return 15728880;
	}

}
