package me.superckl.prayers.boon;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import me.superckl.prayers.Config;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Util;

public class ArmorSupplier implements Function<EquipmentSlotType, AttributeModifier>{

	public static Map<EquipmentSlotType, UUID> IDS = Util.make(new EnumMap<>(EquipmentSlotType.class), map -> {
		map.put(EquipmentSlotType.HEAD, UUID.fromString("8797ce40-0cc5-4c87-9738-1836363403ca"));
		map.put(EquipmentSlotType.CHEST, UUID.fromString("0da7db07-c641-4cb3-8767-b31c6af1866f"));
		map.put(EquipmentSlotType.LEGS, UUID.fromString("0f0604ea-0dbf-4875-9c8a-e71f483c6854"));
		map.put(EquipmentSlotType.FEET, UUID.fromString("bc8e27b8-a1e2-4a9c-9340-6632eb776168"));
	});

	@Override
	public AttributeModifier apply(final EquipmentSlotType t) {
		return new AttributeModifier(ArmorSupplier.IDS.get(t), ItemBoon.ARMOR.getName().getString(),
				Config.getInstance().getBoonValues().get(ItemBoon.ARMOR).get(), Operation.MULTIPLY_TOTAL);
	}

	public static EquipmentSlotType[] validSlots() {
		return new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
	}

}
