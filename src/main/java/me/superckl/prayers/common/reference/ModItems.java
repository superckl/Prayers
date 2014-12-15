package me.superckl.prayers.common.reference;

import me.superckl.prayers.common.item.ItemBasicBone;
import me.superckl.prayers.common.item.ItemBottlePrayers;
import me.superckl.prayers.common.item.ItemBucketPrayers;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public class ModItems {

	public static final ItemBasicBone basicBone = new ItemBasicBone();
	public static final ItemBucketPrayers bucket = new ItemBucketPrayers();
	public static final ItemBottlePrayers bottle = new ItemBottlePrayers();

	public static void init(){
		GameRegistry.registerItem(ModItems.basicBone, Names.BASIC_BONE);
		GameRegistry.registerItem(ModItems.bucket, Names.BUCKET);
		GameRegistry.registerItem(ModItems.bottle, Names.BOTTLE);
	}

	public static void addChestLoot(){
		//Small bones
		WeightedRandomChestContent content = new WeightedRandomChestContent(ModItems.basicBone, 0, 0, 12, 20);
		ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, content);
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, content);
		ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, content);
		ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, content);

		//Large bones
		content = new WeightedRandomChestContent(ModItems.basicBone, 1, 0, 6, 20);
		ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, content);
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, content);
		ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, content);
		ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, content);
		ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, content);
	}

	public static final class Names{
		public static final String BASIC_BONE = "basicBone";
		public static final String BUCKET = "bucket";
		public static final String BOTTLE = "bottle";
	}

}
