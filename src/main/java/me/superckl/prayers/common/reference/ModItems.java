package me.superckl.prayers.common.reference;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;

import me.superckl.prayers.common.item.ItemAncientTome;
import me.superckl.prayers.common.item.ItemBasicBone;
import me.superckl.prayers.common.item.ItemHolyWaterBottle;
import me.superckl.prayers.common.item.ItemPotionPrayers;
import me.superckl.prayers.common.item.ItemPrayerTome;
import me.superckl.prayers.common.prayer.EnumPrayers;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModData.MOD_ID)
public final class ModItems {

	public static final ItemBasicBone basicBone = new ItemBasicBone();
	public static final ItemHolyWaterBottle bottle = new ItemHolyWaterBottle();
	public static final ItemPrayerTome tome = new ItemPrayerTome();
	public static final ItemAncientTome aTome = new ItemAncientTome();
	public static ItemPotionPrayers potion;

	public static void init(){
		GameRegistry.registerItem(ModItems.basicBone, Names.BASIC_BONE);
		GameRegistry.registerItem(ModItems.bottle, Names.BOTTLE);
		GameRegistry.registerItem(ModItems.potion, Names.POTION);
		GameRegistry.registerItem(ModItems.tome, Names.TOME);
		GameRegistry.registerItem(ModItems.aTome, Names.ANCIENT_TOME);
	}

	public static void addChestLoot(){
		//Small bones
		WeightedRandomChestContent content = new WeightedRandomChestContent(ModItems.basicBone, 0, 1, 12, 8);
		ModItems.addLootToAll(content);

		//Large bones
		content = new WeightedRandomChestContent(ModItems.basicBone, 1, 1, 6, 8);
		ModItems.addLootToAll(content);

		//Prayer tomes
		final Random random = new Random();
		final EnumSet<EnumPrayers> prayers = EnumSet.allOf(EnumPrayers.class);
		final Iterator<EnumPrayers> it = prayers.iterator();
		while(it.hasNext())
			if(!it.next().isRequiresTome())
				it.remove();
		final float modifier = 5F/EnumPrayers.MAX_DRAIN;
		ItemStack stack = new ItemStack(ModItems.tome);
		ItemStack temp;
		for(final EnumPrayers prayer:prayers){
			temp = stack.copy();
			final NBTTagCompound comp = new NBTTagCompound();
			comp.setString("prayer", prayer.getId());
			temp.setTagCompound(comp);
			final int weight = (int) (5F-(prayer.getDrain()*modifier));
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

		//Potions
		stack = new ItemStack(ModItems.potion);
		temp = stack.copy();
		final PotionEffect basic = new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 0);
		final PotionEffect adv = new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 1);
		final PotionEffect uber = new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 3);
		ItemPotionPrayers.withEffects(temp, basic);
		content = new WeightedRandomChestContent(temp, 1, 3, 8);
		ModItems.addLootToAll(content);
		temp = stack.copy();
		ItemPotionPrayers.withEffects(temp, adv);
		content = new WeightedRandomChestContent(temp, 1, 2, 5);
		ModItems.addLootToAll(content);
		temp = stack.copy();
		ItemPotionPrayers.withEffects(temp, uber);
		content = new WeightedRandomChestContent(temp, 1, 1, 3);
		ModItems.addLootToAll(content);
	}

	private static void addLootToAll(final WeightedRandomChestContent content){
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
		public static final String BOTTLE = "bottle";
		public static final String POTION = "potion";
		public static final String TOME = "tome";
		public static final String ANCIENT_TOME = "aTome";
	}

}
