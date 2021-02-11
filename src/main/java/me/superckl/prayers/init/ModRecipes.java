package me.superckl.prayers.init;

import me.superckl.prayers.AltarRecipe;
import me.superckl.prayers.Prayers;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {

	public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Prayers.MOD_ID);
	public static final RegistryObject<AltarRecipe.Serializer> ALTAR_SERIALIZER = ModRecipes.REGISTER.register("altar_crafting", AltarRecipe.Serializer::new);

}
