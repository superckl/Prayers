package me.superckl.prayers.common.entity.ai;

import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class PathNavigateFlying extends PathNavigate{

	private final World worldObj;
	private final EntityLiving theEntity;

	public PathNavigateFlying(final EntityLiving entity, final World world) {
		super(entity, world);
		this.theEntity = entity;
		this.worldObj = world;
	}

	@Override
	public boolean isSafeToStandAt(final int p_75483_1_, final int p_75483_2_, final int p_75483_3_, final int p_75483_4_, final int p_75483_5_, final int p_75483_6_, final Vec3 p_75483_7_, final double p_75483_8_, final double p_75483_10_)
	{
		final int k1 = p_75483_1_ - (p_75483_4_ / 2);
		final int l1 = p_75483_3_ - (p_75483_6_ / 2);

		return !this.isPositionClear(k1, p_75483_2_, l1, p_75483_4_, p_75483_5_, p_75483_6_, p_75483_7_, p_75483_8_, p_75483_10_);
	}

	@Override
	public void pathFollow()
	{
		final Vec3 vec3 = this.getEntityPosition();
		final int i = this.currentPath.getCurrentPathLength();

		/*for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j)
			if (this.currentPath.getPathPointFromIndex(j).yCoord != (int)vec3.yCoord)
			{
				i = j;
				break;
			}*/

		final float f = this.theEntity.width * this.theEntity.width;
		int k;

		for (k = this.currentPath.getCurrentPathIndex(); k < i; ++k){
			final Vec3 vec4 = this.currentPath.getVectorFromIndex(this.theEntity, k);
			final double d0 = vec4.xCoord - vec3.xCoord;
			final double d2 = vec4.zCoord - vec3.zCoord;
			if (((d0*d0)+(d2*d2)) < f)
				this.currentPath.setCurrentPathIndex(k + 1);
		}
		k = MathHelper.ceiling_float_int(this.theEntity.width);
		final int l = (int)this.theEntity.height + 1;
		final int i1 = k;

		for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1)
			if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.theEntity, j1), k, l, i1))
			{
				this.currentPath.setCurrentPathIndex(j1);
				break;
			}

		if ((this.totalTicks - this.ticksAtLastPos) > 100)
		{
			final double d0 = this.lastPosCheck.xCoord - vec3.xCoord;
			final double d2 = this.lastPosCheck.zCoord - vec3.zCoord;
			LogHelper.info(vec3+":"+":"+this.lastPosCheck+":"+(d0*d0)+(d2*d2));
			if (((d0*d0)+(d2*d2)) < 2.25D)
				this.clearPathEntity();

			this.ticksAtLastPos = this.totalTicks;
			this.lastPosCheck.xCoord = vec3.xCoord;
			this.lastPosCheck.yCoord = vec3.yCoord;
			this.lastPosCheck.zCoord = vec3.zCoord;
		}
	}

	@Override
	public boolean canNavigate()
	{
		return true;
	}

	@Override
	public PathEntity getPathToXYZ(final double p_75488_1_, final double p_75488_3_, final double p_75488_5_)
	{
		return !this.canNavigate() ? null : this.getEntityPathToXYZ(this.theEntity, MathHelper.floor_double(p_75488_1_), (int)p_75488_3_, MathHelper.floor_double(p_75488_5_), this.getPathSearchRange(), false, false, false, true);
	}

	public PathEntity getEntityPathToXYZ(final Entity p_72844_1_, final int p_72844_2_, final int p_72844_3_, final int p_72844_4_, final float p_72844_5_, final boolean p_72844_6_, final boolean p_72844_7_, final boolean p_72844_8_, final boolean p_72844_9_)
	{
		this.worldObj.theProfiler.startSection("pathfind");
		final int l = MathHelper.floor_double(p_72844_1_.posX);
		final int i1 = MathHelper.floor_double(p_72844_1_.posY);
		final int j1 = MathHelper.floor_double(p_72844_1_.posZ);
		final int k1 = (int)(p_72844_5_ + 8.0F);
		final int l1 = l - k1;
		final int i2 = i1 - k1;
		final int j2 = j1 - k1;
		final int k2 = l + k1;
		final int l2 = i1 + k1;
		final int i3 = j1 + k1;
		final ChunkCache chunkcache = new ChunkCache(this.worldObj, l1, i2, j2, k2, l2, i3, 0);
		final PathEntity pathentity = (new PathFinderFlying(chunkcache, p_72844_6_, p_72844_7_, p_72844_8_, p_72844_9_)).createEntityPathTo(p_72844_1_, p_72844_2_, p_72844_3_, p_72844_4_, p_72844_5_);
		this.worldObj.theProfiler.endSection();
		return pathentity;
	}

}
