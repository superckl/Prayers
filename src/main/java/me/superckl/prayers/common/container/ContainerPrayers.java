package me.superckl.prayers.common.container;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerPrayers extends Container{

	@Getter
	private final InventoryPlayer invPlayer;
	@Getter
	private final InventoryBasic invPrayer;

	public ContainerPrayers(final InventoryPlayer invPlayer) {
		this.invPlayer = invPlayer;
		this.invPrayer = new InventoryBasic("prayerItems", true, 0);

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(invPlayer, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));

		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + (i * 18), 142));

		for (int i = 0; i < 4; ++i)
		{
			final int k = i;
			this.addSlotToContainer(new Slot(invPlayer, invPlayer.getSizeInventory() - 1 - i, 8, 8 + (i * 18))
			{
				private static final String __OBFID = "CL_00001755";
				/**
				 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
				 * in the case of armor slots)
				 */
				@Override
				public int getSlotStackLimit()
				{
					return 1;
				}
				/**
				 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
				 */
				@Override
				public boolean isItemValid(final ItemStack p_75214_1_)
				{
					if (p_75214_1_ == null) return false;
					return p_75214_1_.getItem().isValidArmor(p_75214_1_, k, invPlayer.player);
				}
				/**
				 * Returns the icon index on items.png that is used as background image of the slot.
				 */
				@Override
				@SideOnly(Side.CLIENT)
				public IIcon getBackgroundIconIndex()
				{
					return ItemArmor.func_94602_b(k);
				}
			});
		}
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer p_82846_1_, final int p_82846_2_)
	{
		ItemStack itemstack = null;
		final Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

		if ((slot != null) && slot.getHasStack())
		{
			final ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (p_82846_2_ == 0)
			{
				if (!this.mergeItemStack(itemstack1, 9, 45, true))
					return null;

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if ((p_82846_2_ >= 1) && (p_82846_2_ < 5))
			{
				if (!this.mergeItemStack(itemstack1, 9, 45, false))
					return null;
			}
			else if ((p_82846_2_ >= 5) && (p_82846_2_ < 9))
			{
				if (!this.mergeItemStack(itemstack1, 9, 45, false))
					return null;
			}
			else if ((itemstack.getItem() instanceof ItemArmor) && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack())
			{
				final int j = 5 + ((ItemArmor)itemstack.getItem()).armorType;

				if (!this.mergeItemStack(itemstack1, j, j + 1, false))
					return null;
			}
			else if ((p_82846_2_ >= 9) && (p_82846_2_ < 36))
			{
				if (!this.mergeItemStack(itemstack1, 36, 45, false))
					return null;
			}
			else if ((p_82846_2_ >= 36) && (p_82846_2_ < 45))
			{
				if (!this.mergeItemStack(itemstack1, 9, 36, false))
					return null;
			}
			else if (!this.mergeItemStack(itemstack1, 9, 45, false))
				return null;

			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack)null);
			else
				slot.onSlotChanged();

			if (itemstack1.stackSize == itemstack.stackSize)
				return null;

			slot.onPickupFromSlot(p_82846_1_, itemstack1);
		}

		return itemstack;
	}

}
