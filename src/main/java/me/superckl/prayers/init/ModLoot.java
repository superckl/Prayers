package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.loot.CurioLootCondition;
import me.superckl.prayers.init.loot.PrayerTomeLoot;
import me.superckl.prayers.init.loot.RelicLoot;
import net.minecraft.loot.LootConditionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModLoot {

	public static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Prayers.MOD_ID);
	public static final RegistryObject<PrayerTomeLoot.Serializer> PRAYER_TOME_LOOT = ModLoot.REGISTER.register("prayer_tome", PrayerTomeLoot.Serializer::new);
	public static final RegistryObject<PrayerTomeLoot.Serializer> PRAYER_TOME_LOW_LOOT = ModLoot.REGISTER.register("prayer_tome_low", PrayerTomeLoot.Serializer::new);
	public static final RegistryObject<RelicLoot.Serializer> RELIC_LOOT = ModLoot.REGISTER.register("relic", RelicLoot.Serializer::new);

	public static final LootConditionType CURIO_CONDITION = new LootConditionType(new CurioLootCondition.Serializer());

	public static void registerConditions() {
		Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(Prayers.MOD_ID, "curio"), ModLoot.CURIO_CONDITION);
	}

}
