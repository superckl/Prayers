package me.superckl.prayers.integration.jei;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.RelicItem;
import me.superckl.prayers.recipe.ApplyBoonRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor
public class BoonRecipeWrapper implements ICustomCraftingCategoryExtension{

	private final ApplyBoonRecipe recipe;

	@Override
	public ResourceLocation getRegistryName() {
		return this.recipe.getId();
	}

	@Override
	public void setIngredients(final IIngredients ingredients) {
		final NonNullList<ItemStack> inputs = NonNullList.create();
		ItemGroup.TAB_SEARCH.fillItemList(inputs);
		final List<ItemStack> filteredInputs = inputs.stream().filter(this.recipe.getBoon()::canBeAppliedTo).collect(Collectors.toList());

		final ItemStack relic = new ItemStack(ModItems.RELICS.get(this.recipe.getBoon())::get);
		RelicItem.setCharged(relic);

		final List<ItemStack> outputs = filteredInputs.stream().map(ItemStack::copy).collect(Collectors.toList());
		outputs.forEach(this.recipe.getBoon()::setBoon);

		ingredients.setInputLists(VanillaTypes.ITEM, Lists.newArrayList(filteredInputs, Lists.newArrayList(relic)));
		ingredients.setOutputLists(VanillaTypes.ITEM, Lists.<List<ItemStack>>newArrayList(outputs));
	}

	@Override
	public void setRecipe(final IRecipeLayout recipeLayout, final IIngredients ingredients) {
		recipeLayout.setShapeless();

		final IFocus<ItemStack> focus = recipeLayout.getFocus(VanillaTypes.ITEM);
		if(focus != null)
			if(focus.getMode() == IFocus.Mode.OUTPUT) {
				final ItemStack input = focus.getValue().copy();
				ItemBoon.removeBoon(input);
				final List<List<ItemStack>> lists = ingredients.getInputs(VanillaTypes.ITEM);
				final List<ItemStack> list = lists.get(0);
				list.clear();
				list.add(input);
				ingredients.setInputLists(VanillaTypes.ITEM, lists);
			} else if(!(focus.getValue().getItem() instanceof RelicItem)) {
				final ItemStack output = focus.getValue().copy();
				this.recipe.getBoon().setBoon(output);
				ingredients.setOutput(VanillaTypes.ITEM, output);
			}
		recipeLayout.getItemStacks().set(ingredients);
	}

}
