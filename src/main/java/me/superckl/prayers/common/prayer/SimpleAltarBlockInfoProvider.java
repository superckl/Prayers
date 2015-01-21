package me.superckl.prayers.common.prayer;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.superckl.prayers.common.utility.CalculationEffectType;
import net.minecraft.world.World;

@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class SimpleAltarBlockInfoProvider implements IAltarBlockInfoProvider{

	private boolean affectsRechargeRate;
	private boolean affectsMaxPoints;
	private CalculationEffectType rechargeRateEffectType;
	private CalculationEffectType maxPointsEffectType;
	private float rechargeRateModifier;
	private float maxPointsModifier;


	@Override
	public boolean affectsRechargeRate(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return this.affectsRechargeRate;
	}

	@Override
	public boolean affectsMaxPoints(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return this.affectsMaxPoints;
	}

	@Override
	public CalculationEffectType getRechargeRateEffectType(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return this.rechargeRateEffectType;
	}

	@Override
	public CalculationEffectType getMaxPointsEffectType(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return this.maxPointsEffectType;
	}

	@Override
	public float getRechargeRateModifier(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return this.rechargeRateModifier;
	}

	@Override
	public float getMaxPointsModifier(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return this.maxPointsModifier;
	}

	@Override
	public SimpleAltarBlockInfoProvider clone(){
		return new SimpleAltarBlockInfoProvider(this.affectsRechargeRate, this.affectsMaxPoints, this.rechargeRateEffectType, this.maxPointsEffectType, this.rechargeRateModifier, this.maxPointsModifier);
	}

	@Override
	public boolean isBlockValid(final World world, final int x, final int y, final int z, final int meta, final Altar altar) {
		return true;
	}

}
