package me.superckl.prayers;

import me.superckl.prayers.block.TileEntityAltar;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTiles {

	public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Prayers.MOD_ID);

	public static final RegistryObject<TileEntityType<TileEntityAltar>> ALTAR_BLOCK = ModTiles.REGISTER.register("altar",
			() -> TileEntityType.Builder.create(TileEntityAltar::new, ModBlocks.SANDSTONE_ALTAR.get(),
					ModBlocks.GILDED_SANDSTONE_ALTAR.get(), ModBlocks.MARBLE_ALTAR.get()).build(null));

}
