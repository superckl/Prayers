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

public class SpeedSupplier implements Function<EquipmentSlotType, AttributeModifier>{

	public static Map<EquipmentSlotType, UUID> IDS = Util.make(new EnumMap<>(EquipmentSlotType.class), map -> {
		map.put(EquipmentSlotType.HEAD, UUID.fromString("da5c80a1-4d3a-412e-acd5-e0cd6755ae70"));
		map.put(EquipmentSlotType.CHEST, UUID.fromString("910ed602-ecf4-4c34-a011-34e7ff4b4423"));
		map.put(EquipmentSlotType.LEGS, UUID.fromString("4dbba2d0-0450-4f37-a37c-af367d2d09ab"));
		map.put(EquipmentSlotType.FEET, UUID.fromString("bde26781-a85d-438f-bf7c-28463d827da5"));
	});

	@Override
	public AttributeModifier apply(final EquipmentSlotType t) {
		return new AttributeModifier(SpeedSupplier.IDS.get(t), ItemBoon.SPEED.getName().getString(),
				Config.getInstance().getBoonValues().get(ItemBoon.SPEED).get(), Operation.MULTIPLY_TOTAL);
	}

	public static EquipmentSlotType[] validSlots() {
		return new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
	}

}
