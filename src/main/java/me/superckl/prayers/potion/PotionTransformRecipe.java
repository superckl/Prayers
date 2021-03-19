package me.superckl.prayers.potion;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.brewing.IBrewingRecipe;

@RequiredArgsConstructor
public class PotionTransformRecipe implements IBrewingRecipe{

	private final Supplier<Potion> from;
	private final IItemProvider ingredient;
	private final Supplier<Potion> to;

	@Override
	public boolean isInput(final ItemStack input) {
		return PotionUtils.getPotion(input) == this.from.get();
	}

	@Override
	public boolean isIngredient(final ItemStack ingredient) {
		return ingredient.getItem() == this.ingredient.asItem();
	}

	@Override
	public ItemStack getOutput(final ItemStack input, final ItemStack ingredient) {
		if(!this.isInput(input) || !this.isIngredient(ingredient))
			return ItemStack.EMPTY;
		return PotionUtils.setPotion(input.copy(), this.to.get());
	}

}
