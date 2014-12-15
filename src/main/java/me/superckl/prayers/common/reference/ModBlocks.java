package me.superckl.prayers.common.reference;

import me.superckl.prayers.common.block.BlockBasicAltar;
import me.superckl.prayers.common.fluid.block.BlockFluidHolyWater;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public class ModBlocks {

	public static final BlockBasicAltar basicAltar = new BlockBasicAltar();
	public static BlockFluidHolyWater holyWater;

	public static void init(){
		GameRegistry.registerBlock(ModBlocks.basicAltar, Names.BASIC_ALTAR);
	}

	public static final class Names{
		public static final String BASIC_ALTAR = "basicAltar";
		public static final String HOLY_WATER = "holyWater";
	}
}
