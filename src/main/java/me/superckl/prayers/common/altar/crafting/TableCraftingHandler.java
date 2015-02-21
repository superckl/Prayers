package me.superckl.prayers.common.altar.crafting;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.altar.AltarRegistry;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TableCraftingHandler{

	@Getter
	@Setter
	protected int recipeID;

	public abstract boolean areBaseRequirementsMet(final TileEntityOfferingTable te);
	public abstract boolean areAdditionalRequirementsMet(final TileEntityOfferingTable te);
	public abstract void beginCrafting(final TileEntityOfferingTable te);
	public abstract void handleUpdate(final TileEntityOfferingTable te);
	public abstract boolean isCrafting(final TileEntityOfferingTable te);
	public abstract boolean isComplete(final TileEntityOfferingTable te);
	public abstract void onPostComplete(final TileEntityOfferingTable te);
	public abstract TableCraftingHandler copy();
	public abstract TableCraftingHandler copyWithNBT(final NBTTagCompound comp);

	public NBTTagCompound toNBT(final NBTTagCompound compound){
		compound.setInteger("recipeID", this.recipeID);
		return compound;
	}

	public static TableCraftingHandler fromNBT(final NBTTagCompound compound){
		TableCraftingHandler handler = AltarRegistry.getRecipeById(compound.getInteger("recipeID"));
		handler = handler.copyWithNBT(compound);
		return handler;
	}

}
