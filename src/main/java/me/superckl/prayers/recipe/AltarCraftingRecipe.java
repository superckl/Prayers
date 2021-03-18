package me.superckl.prayers.recipe;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Getter
public class AltarCraftingRecipe extends AbstractAltarCraftingRecipe{

	private final NonNullList<Ingredient> ingredients;
	private final IntList ingredientCounts;
	private final ItemStack output;

	public AltarCraftingRecipe(final ResourceLocation id, final String group, final NonNullList<Ingredient> ingredients, final IntList ingredientCounts, final ItemStack output, final float points) {
		super(id, group, points);
		while(ingredients.size() < 4) {
			ingredients.add(Ingredient.EMPTY);
			ingredientCounts.add(0);
		}
		this.ingredients = ingredients;
		this.ingredientCounts = ingredientCounts;
		this.output = output;
	}

	@Override
	public int[] findMapping(final List<ItemStack> inventory) {
		return RecipeMatcher.findMatches(inventory, this.ingredients);
	}

	@Override
	public int[] getIngredientCounts() {
		return this.ingredientCounts.toIntArray();
	}

	@Override
	public ItemStack assemble(final IInventory inv) {
		return this.output.copy();
	}

	@Override
	public ItemStack getResultItem() {
		return this.output.copy();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.ALTAR_SERIALIZER.get();
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AltarCraftingRecipe>{

		@Override
		public AltarCraftingRecipe fromJson(final ResourceLocation recipeId, final JsonObject json) {
			final String group = JSONUtils.getAsString(json, "group", "");
			final JsonArray ingredientsJson = json.get("ingredients").getAsJsonArray();
			if(ingredientsJson.size() == 0 || ingredientsJson.size() > 4)
				throw new IllegalArgumentException("Altar recipe "+recipeId.toString()+" has invalid number of ingredients "+ingredientsJson.size());
			final NonNullList<Ingredient> ingredients = NonNullList.create();
			final IntList ingredientCounts = new IntArrayList();
			ingredientsJson.forEach(element -> {
				ingredients.add(Ingredient.fromJson(element));
				if(element.getAsJsonObject().has("count"))
					ingredientCounts.add(JSONUtils.getAsInt(element.getAsJsonObject(), "count"));
				else
					ingredientCounts.add(1);
			});
			final ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			final float points = JSONUtils.getAsFloat(json, "points");
			return new AltarCraftingRecipe(recipeId, group, ingredients, ingredientCounts, result, points);
		}

		@Override
		public AltarCraftingRecipe fromNetwork(final ResourceLocation recipeId, final PacketBuffer buffer) {
			final String group = buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH);
			final NonNullList<Ingredient> ingredients = NonNullList.create();
			final IntList ingredientCounts = new IntArrayList();
			final int numIng = buffer.readInt();
			for (int i = 0; i < numIng; i++) {
				ingredients.add(Ingredient.fromNetwork(buffer));
				ingredientCounts.add(buffer.readInt());
			}
			final ItemStack result = buffer.readItem();
			final float points = buffer.readFloat();
			return new AltarCraftingRecipe(recipeId, group, ingredients, ingredientCounts, result, points);
		}

		@Override
		public void toNetwork(final PacketBuffer buffer, final AltarCraftingRecipe recipe) {
			buffer.writeUtf(recipe.group, PrayerUserPacket.BUFFER_STRING_LENGTH);
			buffer.writeInt(recipe.ingredients.size());
			for (int i = 0; i < recipe.ingredients.size(); i++) {
				recipe.ingredients.get(i).toNetwork(buffer);
				buffer.writeInt(recipe.ingredientCounts.getInt(i));
			}
			buffer.writeItem(recipe.output);
			buffer.writeFloat(recipe.points);
		}

	}

}
