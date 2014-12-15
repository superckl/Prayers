package me.superckl.prayers.client.gui;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.network.MessageOpenPrayerGui;
import net.minecraft.item.ItemStack;
import tconstruct.client.tabs.AbstractTab;

public class InventoryTabPrayers extends AbstractTab{

	public InventoryTabPrayers() {
		super(0, 0, 0, new ItemStack(ModItems.basicBone));
	}

	@Override
	public void onTabClicked() {
		ModData.PRAYER_UPDATE_CHANNEL.sendToServer(new MessageOpenPrayerGui());
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}

}
