package me.superckl.prayers.common.item;

import me.superckl.prayers.common.reference.ModData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemPrayers extends Item{

	@Override
	public String getUnlocalizedName(final ItemStack stack){
		return String.format("item.%s%s%s", ModData.MOD_ID.toLowerCase(), ":", this.getUnwrappedUnlocalizedName(super.getUnlocalizedName(), stack.getItemDamage()));
	}

	protected String getUnwrappedUnlocalizedName(final String unlocalizedName, final int damage){
		return unlocalizedName.substring(unlocalizedName.indexOf(".")+1).concat(this.isNameDamageReliant() ? ":"+damage:"");
	}

	protected boolean isNameDamageReliant(){
		return false;
	}

}
