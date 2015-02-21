package me.superckl.prayers.common.altar.crafting;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@Getter
@RequiredArgsConstructor
public class BasicTableCraftingHandler extends RecipeTableCraftingHandler{

	protected final ItemStack result;
	protected final ItemStack baseIngredient;
	protected final List<ItemStack> tertiaryIngredients;
	protected final int length;
	protected final float pointDrain;
	protected boolean crafting;
	protected int timer;

	@Override
	public boolean areAdditionalRequirementsMet(final TileEntityOfferingTable te) {
		return (te.getAltar() != null) && (te.getAltar().getPrayerPoints() >= (this.pointDrain*this.length));
	}

	@Override
	public void beginCrafting(final TileEntityOfferingTable te) {
		this.timer = this.length;
		this.crafting = true;
	}

	@Override
	public void handleUpdate(final TileEntityOfferingTable te) {
		if(te.getAltar() == null){
			this.crafting = false;
			return;
		}
		if(te.getAltar().getPrayerPoints() < this.pointDrain){
			this.crafting = false;
			return;
		}
		this.timer--;
		te.getAltar().setPrayerPoints(te.getAltar().getPrayerPoints()-this.pointDrain);
	}

	@Override
	public boolean isCrafting(final TileEntityOfferingTable te) {
		return this.crafting;
	}

	@Override
	public boolean isComplete(final TileEntityOfferingTable te) {
		return (this.timer <= 0) && this.crafting;
	}

	@Override
	public BasicTableCraftingHandler copy() {
		final BasicTableCraftingHandler handler = new BasicTableCraftingHandler(this.result, this.baseIngredient, this.tertiaryIngredients, this.length, this.pointDrain);
		handler.crafting = this.crafting;
		handler.timer = this.timer;
		handler.recipeID = this.recipeID;
		return handler;
	}

	@Override
	public NBTTagCompound toNBT(NBTTagCompound comp) {
		comp = super.toNBT(comp);
		comp.setInteger("timer", this.timer);
		comp.setBoolean("crafting", this.crafting);
		return comp;
	}

	@Override
	public BasicTableCraftingHandler copyWithNBT(final NBTTagCompound comp) {
		final BasicTableCraftingHandler handler = this.copy();
		handler.timer = comp.getInteger("timer");
		handler.crafting = comp.getBoolean("crafting");
		return handler;
	}

	@Override
	public float getOverallDrain() {
		return this.pointDrain*this.length;
	}

	@Override
	public int getOverallTime() {
		return this.length;
	}

}
