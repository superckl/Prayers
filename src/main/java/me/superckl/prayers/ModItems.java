package me.superckl.prayers;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Prayers.MOD_ID);

	public static final RegistryObject<BlockItem> SANDSTONE_ALTAR = ModItems.REGISTER.register("sandstone_altar",
			() -> new BlockItem(ModBlocks.SANDSTONE_ALTAR.get(), new Item.Properties().group(ItemGroup.MISC)));
	public static final RegistryObject<BlockItem> GILDED_SANDSTONE_ALTAR = ModItems.REGISTER.register("gilded_sandstone_altar",
			() -> new BlockItem(ModBlocks.GILDED_SANDSTONE_ALTAR.get(), new Item.Properties().group(ItemGroup.MISC)));
	public static final RegistryObject<BlockItem> MARBLE_ALTAR = ModItems.REGISTER.register("marble_altar",
			() -> new BlockItem(ModBlocks.MARBLE_ALTAR.get(), new Item.Properties().group(ItemGroup.MISC)));


}
