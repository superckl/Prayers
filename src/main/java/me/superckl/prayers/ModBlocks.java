package me.superckl.prayers;

import me.superckl.prayers.block.AltarBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Prayers.MOD_ID);
	
	public static final RegistryObject<Block> ALTAR = REGISTER.register("altar", AltarBlock::new);
	
}
