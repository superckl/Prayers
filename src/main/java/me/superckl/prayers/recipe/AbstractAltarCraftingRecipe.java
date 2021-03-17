package me.superckl.prayers.recipe;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayers;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Getter
@RequiredArgsConstructor
public abstract class AbstractAltarCraftingRecipe implements IRecipe<IInventory>{

	public static final IRecipeType<AbstractAltarCraftingRecipe> TYPE = IRecipeType.register(new ResourceLocation(Prayers.MOD_ID, "altar_crafting").toString());

	protected final ResourceLocation id;
	protected final String group;
	protected final float points;

	public abstract int[] findMapping(List<ItemStack> inventory);
	public abstract int[] getIngredientCounts();

	@Override
	public boolean matches(final IInventory inv, final World worldIn) {
		return false;
	}

	@Override
	public boolean canFit(final int width, final int height) {
		return false;
	}

	@Override
	public IRecipeType<?> getType() {
		return AbstractAltarCraftingRecipe.TYPE;
	}

}
