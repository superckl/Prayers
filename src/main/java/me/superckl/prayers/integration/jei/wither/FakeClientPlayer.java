package me.superckl.prayers.integration.jei.wither;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public class FakeClientPlayer extends AbstractClientPlayerEntity{

	public FakeClientPlayer(final ClientWorld level, final GameProfile profile) {
		super(level, profile);
	}

	@Override
	public boolean shouldShowName() {
		return false;
	}

	@Override
	public boolean isCustomNameVisible() {
		return false;
	}

	@Override
	public boolean isInvisibleTo(final PlayerEntity player) {
		return true;
	}

	//Expose the method to update swing times
	@Override
	public void updateSwingTime() {
		super.updateSwingTime();
	}

}
