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

public class DamageSupplier implements Function<EquipmentSlotType, AttributeModifier>{

	public static Map<EquipmentSlotType, UUID> IDS = Util.make(new EnumMap<>(EquipmentSlotType.class), map -> {
		map.put(EquipmentSlotType.MAINHAND, UUID.fromString("408016ef-aefd-4783-86e7-2b46395448d5"));
		map.put(EquipmentSlotType.OFFHAND, UUID.fromString("f035f507-63fb-4b84-971b-a825339007d8"));
	});

	@Override
	public AttributeModifier apply(final EquipmentSlotType t) {
		return new AttributeModifier(DamageSupplier.IDS.get(t), ItemBoon.ATTACK_DAMAGE.getName().getString(),
				Config.getInstance().getBoonValues().get(ItemBoon.ATTACK_DAMAGE).get(), Operation.MULTIPLY_TOTAL);
	}

	public static EquipmentSlotType[] validSlots() {
		return new EquipmentSlotType[] {EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};
	}

}
