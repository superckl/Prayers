package me.superckl.prayercraft.common.fluid;

import me.superckl.prayercraft.common.reference.ModData;
import net.minecraftforge.fluids.Fluid;

public abstract class FluidPrayerCraft extends Fluid{

	public FluidPrayerCraft(final String fluidName) {
		super(fluidName);
	}

	@Override
	public String getUnlocalizedName(){
		return String.format("fluid.%s%s%s", ModData.MOD_ID.toLowerCase(), ":", this.getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	private String getUnwrappedUnlocalizedName(final String unlocalizedName){
		return unlocalizedName.substring(unlocalizedName.indexOf(".")+1);
	}

}
