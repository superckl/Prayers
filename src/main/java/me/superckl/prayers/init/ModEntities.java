package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.entity.GrenadeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {

	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Prayers.MOD_ID);

	public static final RegistryObject<EntityType<GrenadeEntity>> GRENADE = ModEntities.register("grenade",
			EntityType.Builder.<GrenadeEntity>of(GrenadeEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(final String id, final EntityType.Builder<T> builder) {
		return ModEntities.REGISTER.register(id, () -> builder.build(id));
	}

}
