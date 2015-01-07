package me.superckl.prayers.common.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import me.superckl.prayers.common.item.ItemBasicBone;
import me.superckl.prayers.common.item.ItemBottlePrayers;
import me.superckl.prayers.common.item.ItemBucketPrayers;
import me.superckl.prayers.common.item.ItemPotionPrayers;
import me.superckl.prayers.common.item.ItemPrayerTome;
import me.superckl.prayers.common.prayer.EnumPrayers;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public class ModItems {

	public static final ItemBasicBone basicBone = new ItemBasicBone();
	public static final ItemBucketPrayers bucket = new ItemBucketPrayers();
	public static final ItemBottlePrayers bottle = new ItemBottlePrayers();
	public static final ItemPrayerTome tome = new ItemPrayerTome();
	public static ItemPotionPrayers potion;

	public static void init(){
		GameRegistry.registerItem(ModItems.basicBone, Names.BASIC_BONE);
		GameRegistry.registerItem(ModItems.bucket, Names.BUCKET);
		GameRegistry.registerItem(ModItems.bottle, Names.BOTTLE);
		GameRegistry.registerItem(ModItems.potion, Names.POTION);
		GameRegistry.registerItem(ModItems.tome, Names.TOME);
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

		final Random random = new Random();
		final List<EnumPrayers> prayers = new ArrayList<EnumPrayers>(Arrays.asList(EnumPrayers.values()));
		final Iterator<EnumPrayers> it = prayers.iterator();
		while(it.hasNext())
			if(!it.next().isRequiresTome())
				it.remove();
		final float modifier = 15F/EnumPrayers.MAX_DRAIN;
		final ItemStack stack = new ItemStack(ModItems.tome);
		ItemStack temp;
		for(final EnumPrayers prayer:prayers){
			temp = stack.copy();
			final NBTTagCompound comp = new NBTTagCompound();
			comp.setString("prayer", prayer.getId());
			temp.setTagCompound(comp);
			final int weight = (int) (15F-(prayer.getDrain()*modifier));
			content = new WeightedRandomChestContent(temp, 1, 1, weight);
			ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, content);
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, content);
			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, content);
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, content);
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, content);
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, content);
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, content);
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(temp, 1, 1, weight+2));
		}
	}

	public static final class Names{
		public static final String BASIC_BONE = "basicBone";
		public static final String BUCKET = "bucket";
		public static final String BOTTLE = "bottle";
		public static final String POTION = "potion";
		public static final String TOME = "tome";
	}

}
