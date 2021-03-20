package me.superckl.prayers.init;

import java.util.EnumMap;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarBlock;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.block.CraftingStandBlock;
import me.superckl.prayers.block.OfferingStandBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Prayers.MOD_ID);

	public static final RegistryObject<OfferingStandBlock> OFFERING_STAND = ModBlocks.REGISTER.register("offering_stand", OfferingStandBlock::new);
	public static final RegistryObject<CraftingStandBlock> CRAFTING_STAND = ModBlocks.REGISTER.register("crafting_stand", CraftingStandBlock::new);
	public static final EnumMap<AltarTypes, RegistryObject<? extends AltarBlock>> ALTARS = new EnumMap<>(AltarTypes.class);
	public static final RegistryObject<Block> SACRED_MARBLE = ModBlocks.REGISTER.register("sacred_marble",
			() -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

	static {
		for (final AltarTypes type : AltarTypes.values())
			ModBlocks.ALTARS.put(type, ModBlocks.REGISTER.register(type.name().toLowerCase()+"_altar", () -> new AltarBlock(type)));
	}

}
