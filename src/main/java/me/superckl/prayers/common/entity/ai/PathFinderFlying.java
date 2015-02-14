package me.superckl.prayers.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;

public class PathFinderFlying extends PathFinder{

	public PathFinderFlying(final IBlockAccess p_i2137_1_, final boolean p_i2137_2_, final boolean p_i2137_3_, final boolean p_i2137_4_, final boolean p_i2137_5_) {
		super(p_i2137_1_, p_i2137_2_, p_i2137_3_, p_i2137_4_, p_i2137_5_);
	}

	@Override
	public PathPoint getSafePoint(final Entity p_75858_1_, final int p_75858_2_, int p_75858_3_, final int p_75858_4_, final PathPoint p_75858_5_, final int p_75858_6_)
	{
		PathPoint pathpoint1 = null;
		final int i1 = this.getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_, p_75858_4_, p_75858_5_);

		if (i1 == 2)
			return this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
		else
		{
			if (i1 == 1)
				pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_);

			if ((pathpoint1 == null) && (p_75858_6_ > 0) && (i1 != -3) && (i1 != -4) && (this.getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_, p_75858_5_) == 1))
			{
				pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_);
				p_75858_3_ += p_75858_6_;
			}

			/*if (pathpoint1 != null)
            {
                int j1 = 0;
                int k1 = 0;

                while (p_75858_3_ > 0)
                {
                    k1 = this.getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ - 1, p_75858_4_, p_75858_5_);

                    if (this.isPathingInWater && k1 == -1)
                    {
                        return null;
                    }

                    if (k1 != 1)
                    {
                        break;
                    }

                    if (j1++ >= p_75858_1_.getMaxSafePointTries())
                    {
                        return null;
                    }

                    --p_75858_3_;

                    if (p_75858_3_ > 0)
                    {
                        pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
                    }
                }

                if (k1 == -2)
                {
                    return null;
                }
            }*/

			return pathpoint1;
		}
	}

}
