package me.superckl.prayers.common.prayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPrayerAltar {

	public boolean isActivated();
	public void setActivated(final boolean activated);
	public float getPrayerPoints();
	public void setPrayerPoints(final float points);
	/**
	 * Called when a player attempts to recharge their prayer points at this altar. All subtraction math should be done in this method.
	 * NOTE: Prayers does not call this method on anything other than it's own altars. If you don't like this method, just make your own.
	 * @param points The points the player is attempting to recharge.
	 * @param player The player attempting to recharge.
	 * @param shouldSubtract Whether or not the subtraction math should actually be done for the altar.
	 * @return The points the player should recharge. This should not be greater than the passed points.
	 */
	public float onRechargePlayer(final float points, final EntityPlayer player, final boolean shouldSubtract);
	public float getMaxPrayerPoints();
	public boolean canBlessWater();
	/**
	 * If this altar can bless water, this will be checked. If true, Prayers will handle blessing water bottles used on your altar in an instant manner. If it is false, you must decide how to implement it.
	 */
	public boolean canBlessInstantly();
	/**
	 * Retrive the boost received when offering the given stack on this altar. You can retrieve the IBuryable instance being used by Prayers with {@link #PrayerHelper.findBuryable(ItemStack)}.
	 * @param stack The stack being offered. Only one item is to be used per offering. The subtraction is handled by Prayers.
	 * @return The xp modifier.
	 */
	public float getOfferXPBoost(final ItemStack stack);

}
