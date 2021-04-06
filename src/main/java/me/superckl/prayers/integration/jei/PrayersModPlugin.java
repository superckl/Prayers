package me.superckl.prayers.integration.jei;

import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.integration.jei.subtype.RelicSubtypeInterpreter;
import me.superckl.prayers.integration.jei.subtype.TomeSubtypeInterpreter;
import me.superckl.prayers.item.RelicItem;
import me.superckl.prayers.recipe.AbstractAltarCraftingRecipe;
import me.superckl.prayers.recipe.ApplyBoonRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
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

	@Override
	public void registerItemSubtypes(final ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ModItems.PRAYER_TOME.get(), new TomeSubtypeInterpreter());
		for(final RegistryObject<RelicItem> obj:ModItems.RELICS.values())
			registration.registerSubtypeInterpreter(obj.get(), new RelicSubtypeInterpreter());
	}

	@Override
	public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(ApplyBoonRecipe.class, BoonRecipeWrapper::new);
	}

}
