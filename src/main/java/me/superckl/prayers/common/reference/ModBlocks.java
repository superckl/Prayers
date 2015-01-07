package me.superckl.prayers.common.reference;

import me.superckl.prayers.common.block.BlockOfferingTable;
import me.superckl.prayers.common.fluid.block.BlockFluidHolyWater;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public class ModBlocks {

	public static final BlockOfferingTable altarBase = new BlockOfferingTable();
	public static BlockFluidHolyWater holyWater;

	public static void init(){
		GameRegistry.registerBlock(ModBlocks.altarBase, Names.ALTAR_BASE);
	}

	public static final class Names{
		public static final String ALTAR_BASE = "altarBase";
		public static final String HOLY_WATER = "holyWater";
	}
}
