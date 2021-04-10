package me.superckl.prayers.inventory;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerInventoryHelper {

	public static SlotAwareIterator<?> allItems(final PlayerEntity player){
		return new PlayerInventoryIterator(player);
	}

}
