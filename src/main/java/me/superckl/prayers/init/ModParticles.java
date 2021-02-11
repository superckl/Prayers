package me.superckl.prayers.init;

import me.superckl.prayers.Prayers;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles {

	public static DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Prayers.MOD_ID);

	public static RegistryObject<BasicParticleType> PRAYER_PARTICLE = ModParticles.REGISTER.register("prayer_particle", () -> new BasicParticleType(false));

}
