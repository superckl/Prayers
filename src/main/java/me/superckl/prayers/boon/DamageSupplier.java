package me.superckl.prayers.boon;

import java.util.UUID;
import java.util.function.Supplier;

import me.superckl.prayers.Config;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;

public class DamageSupplier implements Supplier<AttributeModifier>{

	public static final UUID ID = UUID.fromString("8797ce40-0cc5-4c87-9738-1836363403ca");

	@Override
	public AttributeModifier get() {
		return new AttributeModifier(DamageSupplier.ID, ItemBoon.ATTACK_DAMAGE.getName().getString(), Config.getInstance().getBoonValues().get(ItemBoon.ATTACK_DAMAGE).get(), Operation.MULTIPLY_TOTAL);
	}

}
