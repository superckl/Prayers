package me.superckl.prayercraft.common.reference;

import me.superckl.prayercraft.common.block.BlockBasicAltar;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public class ModBlocks {

	public static final BlockBasicAltar basicAltar = new BlockBasicAltar();

	public static void init(){
		GameRegistry.registerBlock(ModBlocks.basicAltar, Names.BASIC_ALTAR);
	}

	public static final class Names{
		public static final String BASIC_ALTAR = "basicAltar";
	}
}
