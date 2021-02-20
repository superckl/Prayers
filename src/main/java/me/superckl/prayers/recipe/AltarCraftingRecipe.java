package me.superckl.prayers.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.network.packet.PrayersPacket;
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
			ingredientsJson.forEach(element -> ingredients.add(Ingredient.deserialize(element)));
			final ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			final float points = JSONUtils.getFloat(json, "points");
			return new AltarCraftingRecipe(recipeId, group, ingredients, result, points);
		}

		@Override
		public AltarCraftingRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
			final String group = buffer.readString(PrayersPacket.BUFFER_STRING_LENGTH);
			final NonNullList<Ingredient> ingredients = NonNullList.create();
			for (int i = 0; i < buffer.readInt(); i++)
				ingredients.add(Ingredient.read(buffer));
			final ItemStack result = buffer.readItemStack();
			final float points = buffer.readFloat();
			return new AltarCraftingRecipe(recipeId, group, ingredients, result, points);
		}

		@Override
		public void write(final PacketBuffer buffer, final AltarCraftingRecipe recipe) {
			buffer.writeString(recipe.group, PrayersPacket.BUFFER_STRING_LENGTH);
			buffer.writeInt(recipe.ingredients.size());
			recipe.ingredients.forEach(ing -> ing.write(buffer));
			buffer.writeItemStack(recipe.output);
			buffer.writeFloat(recipe.points);
		}

	}

}
