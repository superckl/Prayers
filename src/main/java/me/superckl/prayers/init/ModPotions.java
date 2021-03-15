package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPotions {

	public static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(ForgeRegistries.POTION_TYPES, Prayers.MOD_ID);

	public static final RegistryObject<Potion> INSTANT_PRAYER = ModPotions.REGISTER.register("instant_prayer", () -> new Potion(new EffectInstance(ModEffects.INSTANT_PRAYER.get())));
	public static final RegistryObject<Potion> STRONG_INSTANT_PRAYER = ModPotions.REGISTER.register("strong_instant_prayer", () -> new Potion(ModPotions.INSTANT_PRAYER.getId().getPath(),
			new EffectInstance(ModEffects.INSTANT_PRAYER.get(), 0, 1)));

	public static final RegistryObject<Potion> PRAYER_RENEWAL = ModPotions.REGISTER.register("prayer_regen", () -> new Potion(new EffectInstance(ModEffects.PRAYER_RENEWAL.get(), 6000)));
	public static final RegistryObject<Potion> LONG_PRAYER_RENEWAL = ModPotions.REGISTER.register("long_prayer_regen", () -> new Potion(ModPotions.PRAYER_RENEWAL.getId().getPath(),
			new EffectInstance(ModEffects.PRAYER_RENEWAL.get(), 12000)));

}
