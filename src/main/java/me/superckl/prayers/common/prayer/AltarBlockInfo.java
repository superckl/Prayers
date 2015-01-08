package me.superckl.prayers.common.prayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.superckl.prayers.common.utility.CalculationEffectType;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class AltarBlockInfo implements Cloneable{

	private boolean affectsRechargeRate;
	private boolean affectsMaxPoints;
	private CalculationEffectType rechargeEffectType;
	private CalculationEffectType maxPointsEffectType;
	private float rechargeRateModifier;
	private float maxPointsModifier;

	public AltarBlockInfo(){};

	@Override
	protected Object clone(){
		return new AltarBlockInfo(this.affectsRechargeRate, this.affectsMaxPoints, this.rechargeEffectType, this.maxPointsEffectType, this.rechargeRateModifier, this.maxPointsModifier);
	}

}
