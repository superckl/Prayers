package me.superckl.prayers;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Prayers.MOD_ID);
	
	public static final RegistryObject<BlockItem> ALTAR_BLOCK = REGISTER.register("altar", () -> new BlockItem(ModBlocks.ALTAR.get(), new Item.Properties()));
	
}
