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
		super(id, group, NonNullList.of(Ingredient.EMPTY, Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)),
				Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY), new IntArrayList(new int[] {1, 0, 0, 0}), new ItemStack(ModItems.BLESSED_WATER::get), points);
	}

	@Override
	public int[] findMapping(final List<ItemStack> inventory) {
		if(inventory.size() != 4)
			return null;
		int slot = -1;
		final ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
		for (int i = 0; i < inventory.size(); i++)
			if(ItemStack.isSame(inventory.get(i), waterBottle) && ItemStack.tagMatches(inventory.get(i), waterBottle))
				if(slot == -1)
					slot = i;
				else
					return null;
		if(slot == -1)
			return null;
		else {
			final int[] mapping = new int[] {0,1,2,3};
			mapping[0] = slot;
			mapping[slot] = 0;
			return mapping;
		}
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<HolyWaterRecipe>{

		@Override
		public HolyWaterRecipe fromJson(final ResourceLocation recipeId, final JsonObject json) {
			final String group = JSONUtils.getAsString(json, "group", "");
			final float points = JSONUtils.getAsFloat(json, "points");
			return new HolyWaterRecipe(recipeId, group, points);
		}

		@Override
		public HolyWaterRecipe fromNetwork(final ResourceLocation recipeId, final PacketBuffer buffer) {
			final String group = buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH);
			final float points = buffer.readFloat();
			return new HolyWaterRecipe(recipeId, group, points);
		}

		@Override
		public void toNetwork(final PacketBuffer buffer, final HolyWaterRecipe recipe) {
			buffer.writeUtf(recipe.group, PrayerUserPacket.BUFFER_STRING_LENGTH);
			buffer.writeFloat(recipe.points);
		}

	}

}
