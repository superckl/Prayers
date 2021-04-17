package me.superckl.prayers.effects.entity;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerFlight extends EntitySpecificEffect<PlayerEntity>{

	private boolean couldFly;

	public PlayerFlight(final PlayerEntity entity) {
		super(entity);
	}

	@Override
	public void onActivate() {
		this.couldFly = this.entity.abilities.mayfly;
		this.entity.abilities.mayfly = true;
	}

	@Override
	public void onDeactivate() {
		this.entity.abilities.mayfly = this.couldFly;
		if(!this.couldFly)
			this.entity.abilities.flying = false;
	}

}
