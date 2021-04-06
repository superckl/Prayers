package me.superckl.prayers.boon;

import java.util.UUID;
import java.util.function.Supplier;

import me.superckl.prayers.Config;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;

public class ArmorSupplier implements Supplier<AttributeModifier>{

	public static final UUID ID = UUID.fromString("4dc7a134-6541-417b-8a49-d3102421899e");
	
	@Override
	public AttributeModifier get() {
		return new AttributeModifier(ArmorSupplier.ID, ItemBoon.ARMOR.getName().getString(),
				Config.getInstance().getBoonValues().get(ItemBoon.ARMOR).get(), Operation.MULTIPLY_TOTAL);
	}

}
