package me.superckl.prayers.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

@RequiredArgsConstructor
@Getter
public class AltarCraftingRecipe implements IRecipe<IInventory>{

	public static final IRecipeType<AltarCraftingRecipe> TYPE = IRecipeType.register(new ResourceLocation(Prayers.MOD_ID, "altar_crafting").toString());

	private final ResourceLocation id;
	private final String group;
	private final NonNullList<Ingredient> ingredients;
	private final IntList ingredientCounts;
	private final ItemStack output;
	private final float points;

	@Override
	public boolean matches(final IInventory inv, final World worldIn) {
		return false;
	}

	@Override
	public ItemStack getCraftingResult(final IInventory inv) {
		return this.output;
	}

	@Override
	public boolean canFit(final int width, final int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.output.copy();
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.ALTAR_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return AltarCraftingRecipe.TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AltarCraftingRecipe>{

		@Override
		public AltarCraftingRecipe read(final ResourceLocation recipeId, final JsonObject json) {
			final String group = JSONUtils.getString(json, "group", "");
			final JsonArray ingredientsJson = json.get("ingredients").getAsJsonArray();
			if(ingredientsJson.size() == 0 || ingredientsJson.size() > 4)
				throw new IllegalArgumentException("Altar recipe "+recipeId.toString()+" has invalid number of ingredients "+ingredientsJson.size());
			final NonNullList<Ingredient> ingredients = NonNullList.create();
			final IntList ingredientCounts = new IntArrayList();
			ingredientsJson.forEach(element -> {
				ingredients.add(Ingredient.deserialize(element));
				if(element.getAsJsonObject().has("count"))
					ingredientCounts.add(JSONUtils.getInt(element, "count"));
				else
					ingredientCounts.add(1);
			});
			while(ingredients.size() < 4) {
				ingredients.add(Ingredient.fromStacks(ItemStack.EMPTY));
				ingredientCounts.add(0);
			}
			final ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			final float points = JSONUtils.getFloat(json, "points");
			return new AltarCraftingRecipe(recipeId, group, ingredients, ingredientCounts, result, points);
		}

		@Override
		public AltarCraftingRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
			final String group = buffer.readString(PrayerUserPacket.BUFFER_STRING_LENGTH);
			final NonNullList<Ingredient> ingredients = NonNullList.create();
			final IntList ingredientCounts = new IntArrayList();
			final int numIng = buffer.readInt();
			for (int i = 0; i < numIng; i++) {
				ingredients.add(Ingredient.read(buffer));
				ingredientCounts.add(buffer.readInt());
			}
			final ItemStack result = buffer.readItemStack();
			final float points = buffer.readFloat();
			return new AltarCraftingRecipe(recipeId, group, ingredients, ingredientCounts, result, points);
		}

		@Override
		public void write(final PacketBuffer buffer, final AltarCraftingRecipe recipe) {
			buffer.writeString(recipe.group, PrayerUserPacket.BUFFER_STRING_LENGTH);
			buffer.writeInt(recipe.ingredients.size());
			for (int i = 0; i < recipe.ingredients.size(); i++) {
				recipe.ingredients.get(i).write(buffer);
				buffer.writeInt(recipe.ingredientCounts.getInt(i));
			}
			buffer.writeItemStack(recipe.output);
			buffer.writeFloat(recipe.points);
		}

	}

}
