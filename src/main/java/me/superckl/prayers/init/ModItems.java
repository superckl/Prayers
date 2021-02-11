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

	public static final ItemGroup PRAYERS_GROUP = new ItemGroup("Prayers") {

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.ALTARS.get(AltarTypes.SANDSTONE).get());
		}
	};

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Prayers.MOD_ID);

	public static final EnumMap<AltarTypes, RegistryObject<? extends BlockItem>> ALTARS = new EnumMap<>(AltarTypes.class);

	static {
		for (final AltarTypes type : AltarTypes.values())
			ModItems.ALTARS.put(type, ModItems.REGISTER.register(type.name().toLowerCase()+"_altar",
					() -> new BlockItem(ModBlocks.ALTARS.get(type).get(), new Item.Properties().group(ModItems.PRAYERS_GROUP))));
	}

}
