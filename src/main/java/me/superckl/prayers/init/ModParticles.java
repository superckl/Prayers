package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles {

	public static DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Prayers.MOD_ID);

	public static RegistryObject<BasicParticleType> ALTAR_ACTIVE = ModParticles.REGISTER.register("altar_active", () -> new BasicParticleType(false));
	public static RegistryObject<BasicParticleType> ITEM_SACRIFICE = ModParticles.REGISTER.register("item_sacrifice", () -> new BasicParticleType(false));

}
