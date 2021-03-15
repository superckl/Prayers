package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.potion.InstantPrayerEffect;
import me.superckl.prayers.potion.PrayerRenewalEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {

	public static final DeferredRegister<Effect> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, Prayers.MOD_ID);

	public static final RegistryObject<InstantPrayerEffect> INSTANT_PRAYER = ModEffects.REGISTER.register("instant_prayer", InstantPrayerEffect::new);
	public static final RegistryObject<PrayerRenewalEffect> PRAYER_RENEWAL = ModEffects.REGISTER.register("prayer_regen", PrayerRenewalEffect::new);

}
