package me.superckl.prayers.common.item;

import me.superckl.prayers.common.reference.ModData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.ItemFluidContainer;

public abstract class ItemFluidContainerPrayers extends ItemFluidContainer{

	public ItemFluidContainerPrayers(final int itemID) {
		super(itemID);
	}

	public ItemFluidContainerPrayers(final int itemID, final int capacity) {
		super(itemID, capacity);
	}

	@Override
	public String getUnlocalizedName(final ItemStack stack){
		return String.format("item.%s%s%s", ModData.MOD_ID.toLowerCase(), ":", this.getUnwrappedUnlocalizedName(super.getUnlocalizedName(), ((ItemFluidContainer)stack.getItem()).getFluid(stack).getFluid()));
	}

	protected String getUnwrappedUnlocalizedName(final String unlocalizedName, final Fluid fluid){
		return unlocalizedName.substring(unlocalizedName.indexOf(".")+1).concat(":").concat(fluid.getName());
	}

}