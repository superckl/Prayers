package me.superckl.prayers.common.item;

import net.minecraft.creativetab.CreativeTabs;

public class ItemPotionPrayers extends ItemPrayers{

	public ItemPotionPrayers() {
		this.setMaxStackSize(1).setCreativeTab(CreativeTabs.tabBrewing).setHasSubtypes(true).setUnlocalizedName("potion");
	}

	@Override
	protected boolean isNameDamageReliant() {
		return true;
	}

}
