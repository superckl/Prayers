package me.superckl.prayers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.ints.IntList;
import me.superckl.prayers.init.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AltarRecipe implements IRecipe<AltarInventory>{

	public static IRecipeType<AltarRecipe> TYPE;

	private final ResourceLocation id;
	private final ItemStack recipeOutput;
	private final NonNullList<Ingredient> recipeItems;
	private final boolean isSimple;

	public AltarRecipe(final ResourceLocation idIn, final ItemStack recipeOutputIn, final NonNullList<Ingredient> recipeItemsIn) {
		this.id = idIn;
		this.recipeOutput = recipeOutputIn;
		this.recipeItems = recipeItemsIn;
		this.isSimple = recipeItemsIn.stream().allMatch(Ingredient::isSimple);
	}

	@Override
	public boolean matches(final AltarInventory inv, final World worldIn) {
		final RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
		final java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
		int i = 0;

		for(int j = 0; j < inv.getSizeInventory(); ++j) {
			final ItemStack itemstack = inv.getStackInSlot(j);
			if (!itemstack.isEmpty()) {
				++i;
				if (this.isSimple)
					recipeitemhelper.func_221264_a(itemstack, 1);
				else
					inputs.add(itemstack);
			}
		}

		return i == this.recipeItems.size() && (this.isSimple ? recipeitemhelper.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.recipeItems) != null);
	}

	@Override
	public ItemStack getCraftingResult(final AltarInventory inv) {
		return this.recipeOutput.copy();
	}

	@Override
	public boolean canFit(final int width, final int height) {
		return width * height >= this.recipeItems.size();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.recipeOutput;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.recipeItems;
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
		if(AltarRecipe.TYPE == null)
			AltarRecipe.TYPE = IRecipeType.register("altar_crafting");
		return AltarRecipe.TYPE;
	}

	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AltarRecipe> {

		@Override
		public AltarRecipe read(final ResourceLocation recipeId, final JsonObject json) {
			final NonNullList<Ingredient> nonnulllist = Serializer.readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
			if (nonnulllist.isEmpty())
				throw new JsonParseException("No ingredients for shapeless recipe");
			else {
				final ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
				return new AltarRecipe(recipeId, itemstack, nonnulllist);
			}
		}

		private static NonNullList<Ingredient> readIngredients(final JsonArray ingredientArray) {
			final NonNullList<Ingredient> nonnulllist = NonNullList.create();

			for(int i = 0; i < ingredientArray.size(); ++i) {
				final Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
				if (!ingredient.hasNoMatchingItems())
					nonnulllist.add(ingredient);
			}

			return nonnulllist;
		}

		@Override
		public AltarRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
			final int i = buffer.readVarInt();
			final NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for(int j = 0; j < nonnulllist.size(); ++j)
				nonnulllist.set(j, Ingredient.read(buffer));

			final ItemStack itemstack = buffer.readItemStack();
			return new AltarRecipe(recipeId, itemstack, nonnulllist);
		}

		@Override
		public void write(final PacketBuffer buffer, final AltarRecipe recipe) {
			buffer.writeVarInt(recipe.recipeItems.size());

			for(final Ingredient ingredient : recipe.recipeItems)
				ingredient.write(buffer);

			buffer.writeItemStack(recipe.recipeOutput);
		}
	}

}
