package me.superckl.prayers.effects.entity;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;

@RequiredArgsConstructor
public abstract class EntitySpecificEffect<T extends LivingEntity> {

	public static final EntitySpecificEffect<LivingEntity> NONE = new None();

	protected final T entity;

	public void onActivate() {}
	public void onDeactivate() {}

	private static class None extends EntitySpecificEffect<LivingEntity>{

		public None() {
			super(null);
		}

	}

}
