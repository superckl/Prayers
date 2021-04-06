package me.superckl.prayers.integration.jei;

import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.recipe.AbstractAltarCraftingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

@JeiPlugin
public class PrayersModPlugin implements IModPlugin{

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Prayers.MOD_ID, "recipes");
	}

	@Override
	public void registerCategories(final IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new AltarCraftingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(final IRecipeRegistration registration) {
		registration.addRecipes(ClientHelper.getLevel().getRecipeManager().getAllRecipesFor(AbstractAltarCraftingRecipe.TYPE), AltarCraftingRecipeCategory.UID);
	}

	@Override
	public void registerRecipeCatalysts(final IRecipeCatalystRegistration registration) {
		for(final RegistryObject<BlockItem> obj:ModItems.ALTARS.values())
			registration.addRecipeCatalyst(new ItemStack(obj::get), AltarCraftingRecipeCategory.UID);
	}

}
