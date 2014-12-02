package me.superckl.prayercraft.common.reference;

import me.superckl.prayercraft.common.item.ItemBasicBone;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public class ModItems {

	public static final ItemBasicBone basicBone = new ItemBasicBone();

	public static void init(){
		GameRegistry.registerItem(ModItems.basicBone, Names.BASIC_BONE);
	}

	public static final class Names{
		public static final String BASIC_BONE = "basicBone";
	}

}
