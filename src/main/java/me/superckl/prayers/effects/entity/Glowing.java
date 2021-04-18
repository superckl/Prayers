package me.superckl.prayers.effects.entity;

import net.minecraft.entity.LivingEntity;

public class Glowing extends EntitySpecificEffect<LivingEntity>{

	public Glowing(final LivingEntity entity) {
		super(entity);
	}

	@Override
	public void onActivate() {
		this.entity.setGlowing(true);
	}

	@Override
	public void onDeactivate() {
		this.entity.setGlowing(false);
	}

}
