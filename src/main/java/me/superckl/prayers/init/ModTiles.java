package me.superckl.prayers.init;

import java.util.EnumMap;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.block.TileEntityAltar;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTiles {

	public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Prayers.MOD_ID);

	public static final EnumMap<AltarTypes, RegistryObject<TileEntityType<? extends TileEntityAltar>>> ALTARS = new EnumMap<>(AltarTypes.class);

	static {
		for (final AltarTypes type : AltarTypes.values())
			ModTiles.ALTARS.put(type, ModTiles.REGISTER.register(type.name().toLowerCase()+"_altar",
					() -> TileEntityType.Builder.create(() -> new TileEntityAltar(type), ModBlocks.ALTARS.get(type).get()).build(null)));
	}

}
