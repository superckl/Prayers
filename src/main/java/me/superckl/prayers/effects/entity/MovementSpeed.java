package me.superckl.prayers.effects.entity;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;

public class MovementSpeed extends EntitySpecificEffect<LivingEntity>{

	public static final UUID MODIFIER_ID = UUID.fromString("09c193b8-9cd4-47cf-bb92-b9a7cd247e74");

	private final AttributeModifier modifier;

	public MovementSpeed(final LivingEntity entity, final float modifier) {
		super(entity);
		this.modifier = new AttributeModifier(MovementSpeed.MODIFIER_ID, "speed_prayer", modifier, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public void onActivate() {
		this.entity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(this.modifier);
	}

	@Override
	public void onDeactivate() {
		this.entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(this.modifier);
	}

}
