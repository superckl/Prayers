package me.superckl.prayers.common.altar.crafting;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.api.AltarRegistry;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This is the generic class for Offering Table recipes. Crafting Handlers ARE NOT singleton instances, and are copied quite often.
 */
public abstract class TableCraftingHandler{

	@Getter
	@Setter
	protected int recipeID;

	/**
	 * Used to check if the base requirements for a recipe are satisfied. This could include checking for ingredients or certain conditions of the altar such as level or max points.
	 * This does not include checking available points of the altar.
	 * If this returns true, the Offering Table will store the recipe and wait until either it's inventory has changed or {@link #areAdditionalRequirementsMet(TileEntityOfferingTable) areAdditionalRequirementsMet} returns true.
	 * @param te The Offering Table performing the check.
	 * @return If the requirements are satisfied.
	 */
	public abstract boolean areBaseRequirementsMet(final TileEntityOfferingTable te);
	/**
	 * Used to check if an extraneous requirements are met. This is anything that wasn't check in {@link #areBaseRequirementsMet(TileEntityOfferingTable) areBaseRequirementsMet}.
	 * @param te The Offering Table performing the check.
	 * @return If the requirements are satisfied.
	 */
	public abstract boolean areAdditionalRequirementsMet(final TileEntityOfferingTable te);
	/**
	 * Called when the altar wants to initiate crafting. If you ever want to cancel the crafting, simply return true in {@link #isComplete(TileEntityOfferingTable) isComplete}.
	 * @param te The Offering Table performing the check.
	 */
	public abstract void beginCrafting(final TileEntityOfferingTable te);
	/**
	 * Called every update tick so the recipe can update any timers or conditions.
	 * @param te The Offering Table performing the check.
	 */
	public abstract void handleUpdate(final TileEntityOfferingTable te);
	/**
	 * Used by Offering Tables to check if the recipe is currently crafting, before calling {@link #handleUpdate(TileEntityOfferingTable) handleUpdate}.
	 * @param te The Offering Table performing the check.
	 * @return If this recipe is currently crafting.
	 */
	public abstract boolean isCrafting(final TileEntityOfferingTable te);
	/**
	 * Used by Offering Tables to check if the recipe has finished crafting, before calling {@link #onPostComplete(TileEntityOfferingTable) onPostComplete}.
	 * @param te The Offering Table performing the check.
	 * @return If this recipe has finished crafting.
	 */
	public abstract boolean isComplete(final TileEntityOfferingTable te);
	/**
	 * Called when {@link #isComplete(TileEntityOfferingTable) isComplete} returns true. After this method is called, the Offering Table will discard it's reference to this recipe.
	 * If you didn't change anything on the tables's inventory, and you wish the table to not find another recipe until it's inventory is changed, you can use {@link TileEntityOfferingTable#setCraftingLock(boolean) setCraftingLock}.
	 * @param te The Offering Table performing the call.
	 */
	public abstract void onPostComplete(final TileEntityOfferingTable te);
	/**
	 * Called to copy the recipe. This is most commonly used when retrieving a registered recipe. This should be considered analagous to {@link #clone() clone}.
	 * @return The new recipe instance.
	 */
	public abstract TableCraftingHandler copy();
	/**
	 * Called to copy the recipe, with stored NBT data. This is most commonly used when deserializing recipes.
	 * You can look at some of Prayer's recipe classes for examples of what should and what shouldn't be copied.
	 * @param comp The NBT tag the serialized recipe was stored in.
	 * @return
	 */
	public abstract TableCraftingHandler copyWithNBT(final NBTTagCompound comp);

	/**
	 * Used to write this recipe to NBT. If you override this, you should always perform a super call. Otherwise, you will break NBT storage.
	 * You can look at some of Prayer's recipe classes for examples of what should and what shouldn't be stored.
	 * @param compound The NBT tag this recipe should store it's data in. It doesn't have to be this tag, as long as you return the one you used.
	 * @return The NBT tag the data was stored in.
	 */
	public NBTTagCompound toNBT(final NBTTagCompound compound){
		compound.setInteger("recipeID", this.recipeID);
		return compound;
	}

	/**
	 * Attempts to load a recipe from NBT. This is done by retrieving the stored recipeID, retrieving an instance from the registered recipes, and calling {@link #copyWithNBT(NBTTagCompound) copyWithNBT}.
	 * @param compound The NBT tag to load the recipe from.
	 * @return The loaded recipe, or null if the recipe could not be loaded.
	 */
	public static TableCraftingHandler fromNBT(final NBTTagCompound compound){
		try {
			TableCraftingHandler handler = AltarRegistry.getRecipeById(compound.getInteger("recipeID"));
			handler = handler.copyWithNBT(compound);
			return handler;
		} catch (final Exception e) {
			LogHelper.error("Failed to desierialize an Offering Table recipe!");
			e.printStackTrace();
		}
		return null;
	}

}
