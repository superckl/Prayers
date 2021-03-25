package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.loot.PrayerTomeLoot;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModLoot {

	public static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Prayers.MOD_ID);
	public static final RegistryObject<PrayerTomeLoot.Serializer> PRAYER_TOME_LOOT = ModLoot.REGISTER.register("prayer_tome", PrayerTomeLoot.Serializer::new);
	public static final RegistryObject<PrayerTomeLoot.Serializer> PRAYER_TOME_LOW_LOOT = ModLoot.REGISTER.register("prayer_tome_low", PrayerTomeLoot.Serializer::new);

}
