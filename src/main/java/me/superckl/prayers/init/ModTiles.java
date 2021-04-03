package me.superckl.prayers.init;

import java.util.EnumMap;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.block.entity.AltarTileEntity;
import me.superckl.prayers.block.entity.CraftingStandTileEntity;
import me.superckl.prayers.block.entity.OfferingStandTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTiles {

	public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Prayers.MOD_ID);

	public static final RegistryObject<TileEntityType<OfferingStandTileEntity>> OFFERING_STAND = ModTiles.REGISTER.register("offering_stand",
			() -> TileEntityType.Builder.of(OfferingStandTileEntity::new, ModBlocks.OFFERING_STAND.get()).build(null));
	public static final RegistryObject<TileEntityType<CraftingStandTileEntity>> CRAFTING_STAND = ModTiles.REGISTER.register("crafting_stand",
			() -> TileEntityType.Builder.of(CraftingStandTileEntity::new, ModBlocks.CRAFTING_STAND.get()).build(null));
	public static final EnumMap<AltarTypes, RegistryObject<TileEntityType<? extends AltarTileEntity>>> ALTARS = new EnumMap<>(AltarTypes.class);

	static {
		for (final AltarTypes type : AltarTypes.values())
			ModTiles.ALTARS.put(type, ModTiles.REGISTER.register(type.name().toLowerCase()+"_altar",
					() -> TileEntityType.Builder.of(() -> new AltarTileEntity(type), ModBlocks.ALTARS.get(type).get()).build(null)));
	}

}
