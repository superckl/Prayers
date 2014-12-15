package me.superckl.prayers.common.fluid.block;

import me.superckl.prayers.common.reference.ModData;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public abstract class BlockFluidClassicPrayers extends BlockFluidClassic{

	public BlockFluidClassicPrayers(final Fluid fluid, final Material material) {
		super(fluid, material);
	}

	@Override
	public String getUnlocalizedName(){
		return String.format("tile.%s%s%s", ModData.MOD_ID.toLowerCase(), ":", this.getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	private String getUnwrappedUnlocalizedName(final String unlocalizedName){
		return unlocalizedName.substring(unlocalizedName.indexOf(".")+1);
	}

}
