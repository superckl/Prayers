package me.superckl.prayers.common.prayer;

import me.superckl.prayers.common.utility.CalculationEffectType;
import net.minecraft.world.World;

public interface IAltarBlockInfoProvider extends Cloneable{

	public boolean affectsRechargeRate(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public boolean affectsMaxPoints(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public CalculationEffectType getRechargeRateEffectType(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public CalculationEffectType getMaxPointsEffectType(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public float getRechargeRateModifier(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public float getMaxPointsModifier(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public boolean isBlockValid(final World world, final int x, final int y, final int z, final int meta, final Altar altar);

	public IAltarBlockInfoProvider clone();

}
