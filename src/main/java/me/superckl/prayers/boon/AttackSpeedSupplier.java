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

public class AttackSpeedSupplier implements Function<EquipmentSlotType, AttributeModifier>{

	public static Map<EquipmentSlotType, UUID> IDS = Util.make(new EnumMap<>(EquipmentSlotType.class), map -> {
		map.put(EquipmentSlotType.MAINHAND, UUID.fromString("3bd64f65-de75-40fa-bc1f-dec3e9e805f2"));
		map.put(EquipmentSlotType.OFFHAND, UUID.fromString("9e789302-eb1f-4f02-a7bc-243d3d3151f5"));
	});

	@Override
	public AttributeModifier apply(final EquipmentSlotType t) {
		return new AttributeModifier(DamageSupplier.IDS.get(t), ItemBoon.USE_SPEED.getName().getString(),
				Config.getInstance().getBoonValues().get(ItemBoon.USE_SPEED).get(), Operation.MULTIPLY_TOTAL);
	}

	public static EquipmentSlotType[] validSlots() {
		return new EquipmentSlotType[] {EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};
	}

}
