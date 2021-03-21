package me.superckl.prayers.init;

import java.util.EnumMap;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

	public static final ItemGroup PRAYERS_GROUP = new ItemGroup(Prayers.MOD_ID) {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.ALTARS.get(AltarTypes.SANDSTONE).get());
		}
	};

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Prayers.MOD_ID);

	public static final RegistryObject<BlockItem> OFFERING_STAND = ModItems.REGISTER.register("offering_stand",
			() -> new BlockItem(ModBlocks.OFFERING_STAND.get(), new Item.Properties().tab(ModItems.PRAYERS_GROUP)));
	public static final RegistryObject<BlockItem> CRAFTING_STAND = ModItems.REGISTER.register("crafting_stand",
			() -> new BlockItem(ModBlocks.CRAFTING_STAND.get(), new Item.Properties().tab(ModItems.PRAYERS_GROUP)));
	public static final EnumMap<AltarTypes, RegistryObject<? extends BlockItem>> ALTARS = new EnumMap<>(AltarTypes.class);
	public static final RegistryObject<BlockItem> SACRED_MARBLE = ModItems.REGISTER.register("sacred_marble",
			() -> new BlockItem(ModBlocks.SACRED_MARBLE.get(), new Item.Properties().tab(ModItems.PRAYERS_GROUP)));
	public static final RegistryObject<Item> GOLD_LEAF = ModItems.REGISTER.register("gold_leaf",
			() -> new Item(new Item.Properties().tab(ModItems.PRAYERS_GROUP)));
	public static final RegistryObject<Item> GILDED_BONE = ModItems.REGISTER.register("gilded_bone",
			() -> new Item(new Item.Properties().tab(ModItems.PRAYERS_GROUP)));
	public static final RegistryObject<Item> BLESSED_WATER = ModItems.REGISTER.register("blessed_water",
			() -> new Item(new Item.Properties().tab(ModItems.PRAYERS_GROUP).stacksTo(1)));

	static {
		for (final AltarTypes type : AltarTypes.values())
			ModItems.ALTARS.put(type, ModItems.REGISTER.register(type.name().toLowerCase()+"_altar",
					() -> new BlockItem(ModBlocks.ALTARS.get(type).get(), new Item.Properties().tab(ModItems.PRAYERS_GROUP))));
	}

}
