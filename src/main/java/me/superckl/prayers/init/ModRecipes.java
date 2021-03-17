package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.recipe.AltarCraftingRecipe;
import me.superckl.prayers.recipe.HolyWaterRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {

	public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Prayers.MOD_ID);
	public static final RegistryObject<AltarCraftingRecipe.Serializer> ALTAR_SERIALIZER = ModRecipes.REGISTER.register("altar_crafting", AltarCraftingRecipe.Serializer::new);
	public static final RegistryObject<HolyWaterRecipe.Serializer> HOLY_WATER_SERIALIZER = ModRecipes.REGISTER.register("altar_holy_water_crafting", HolyWaterRecipe.Serializer::new);

}
