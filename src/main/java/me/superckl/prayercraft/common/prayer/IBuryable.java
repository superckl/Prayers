package me.superckl.prayercraft.common.prayer;

import net.minecraft.item.ItemStack;

public interface IBuryable {

	public int getXPFromStack(final ItemStack stack);
	public float getPointsRequiredToOffer(final ItemStack stack, final IPrayerAltar altar);

}
