package me.superckl.prayers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;

public class AltarInventory extends CraftingInventory{

	public AltarInventory(final Container eventHandlerIn, final int numAltars) {
		super(eventHandlerIn, numAltars, 1);
	}

	@Override
	public boolean isUsableByPlayer(final PlayerEntity player) {
		return false;
	}

}
