package me.superckl.prayers.common.reference;

import me.superckl.prayers.common.block.BlockAltarComponent;
import me.superckl.prayers.common.block.BlockOfferingTable;
import me.superckl.prayers.common.fluid.block.BlockFluidHolyWater;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public final class ModBlocks {

	public static final BlockOfferingTable offeringTable = new BlockOfferingTable();
	public static final BlockAltarComponent altarComponent = new BlockAltarComponent();
	public static BlockFluidHolyWater holyWater;

	public static void init(){
		GameRegistry.registerBlock(ModBlocks.offeringTable, Names.OFFERING_TABLE);
		GameRegistry.registerBlock(ModBlocks.altarComponent, Names.ALTAR_COMPONENT);
	}

	public static final class Names{
		public static final String OFFERING_TABLE = "offeringTable";
		public static final String ALTAR_COMPONENT = "altarComponent";
		public static final String HOLY_WATER = "holyWater";
	}
}
