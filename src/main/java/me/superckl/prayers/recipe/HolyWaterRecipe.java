package me.superckl.prayers.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class HolyWaterRecipe extends AltarCraftingRecipe{

	public HolyWaterRecipe(final ResourceLocation id, final String group, final float points) {
		super(id, group, NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER)),
				Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY), new IntArrayList(new int[] {1, 0, 0, 0}), new ItemStack(ModItems.BLESSED_WATER::get), points);
	}

	@Override
	public int[] findMapping(final List<ItemStack> inventory) {
		if(inventory.size() != 4)
			return null;
		int slot = -1;
		final ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
		for (int i = 0; i < inventory.size(); i++)
			if(ItemStack.areItemsEqual(inventory.get(i), waterBottle) && ItemStack.areItemStackTagsEqual(inventory.get(i), waterBottle))
				if(slot == -1)
					slot = i;
				else
					return null;
		if(slot == -1)
			return null;
		else {
			final int[] mapping = new int[] {1,2,3,4};
			mapping[1] = slot;
			mapping[slot] = 1;
			return mapping;
		}
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<HolyWaterRecipe>{

		@Override
		public HolyWaterRecipe read(final ResourceLocation recipeId, final JsonObject json) {
			final String group = JSONUtils.getString(json, "group", "");
			final float points = JSONUtils.getFloat(json, "points");
			return new HolyWaterRecipe(recipeId, group, points);
		}

		@Override
		public HolyWaterRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
			final String group = buffer.readString(PrayerUserPacket.BUFFER_STRING_LENGTH);
			final float points = buffer.readFloat();
			return new HolyWaterRecipe(recipeId, group, points);
		}

		@Override
		public void write(final PacketBuffer buffer, final HolyWaterRecipe recipe) {
			buffer.writeString(recipe.group, PrayerUserPacket.BUFFER_STRING_LENGTH);
			buffer.writeFloat(recipe.points);
		}

	}

}
