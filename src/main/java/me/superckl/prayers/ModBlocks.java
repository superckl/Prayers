package me.superckl.prayers;

import me.superckl.prayers.block.AltarBlock;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Prayers.MOD_ID);

	public static final RegistryObject<Block> SANDSTONE_ALTAR = ModBlocks.REGISTER.register("sandstone_altar", () -> new AltarBlock(AltarTypes.SANDSTONE));
	public static final RegistryObject<Block> GILDED_SANDSTONE_ALTAR = ModBlocks.REGISTER.register("gilded_sandstone_altar", () -> new AltarBlock(AltarTypes.GILDED_SANDSTONE));
	public static final RegistryObject<Block> MARBLE_ALTAR = ModBlocks.REGISTER.register("marble_altar", () -> new AltarBlock(AltarTypes.MARBLE));

}
