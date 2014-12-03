package me.superckl.prayercraft.client.gui;

import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModItems;
import me.superckl.prayercraft.network.MessageOpenPrayerGui;
import net.minecraft.item.ItemStack;
import tconstruct.client.tabs.AbstractTab;

public class InventoryTabPrayers extends AbstractTab{

	public InventoryTabPrayers() {
		super(0, 0, 0, new ItemStack(ModItems.basicBone));
	}

	@Override
	public void onTabClicked() {
		ModData.PRAYER_UPDATE_CHANNEL.sendToServer(new MessageOpenPrayerGui());
		//player.openGui(PrayerCraft.getInstance(), 0, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}

}
