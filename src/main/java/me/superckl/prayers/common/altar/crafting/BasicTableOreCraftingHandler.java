package me.superckl.prayers.common.altar.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class BasicTableOreCraftingHandler extends RecipeTableCraftingHandler{

	private final ItemStack output;
	@Getter
	private ArrayList<Object> input = new ArrayList<Object>();
	protected final int length;
	protected final float pointDrain;
	protected boolean crafting;
	protected int timer;

	public BasicTableOreCraftingHandler(final ItemStack result, final int length, final float pointDrain,
			final Object ... recipe) {
		this.length = length;
		this.pointDrain = pointDrain;
		this.output = result.copy();
		for (final Object in : recipe)
			if (in instanceof ItemStack)
				this.input.add(((ItemStack)in).copy());
			else if (in instanceof Item)
				this.input.add(new ItemStack((Item)in));
			else if (in instanceof Block)
				this.input.add(new ItemStack((Block)in));
			else if (in instanceof String)
				this.input.add(OreDictionary.getOres((String)in));
			else
			{
				String ret = "Invalid shapeless ore recipe: ";
				for (final Object tmp :  recipe)
					ret += tmp + ", ";
				ret += this.output;
				throw new IllegalArgumentException(ret);
			}
	}

	@Override
	public ItemStack getResult() {
		return this.output;
	}

	@Override
	public ItemStack getBaseIngredient() {
		throw new UnsupportedOperationException("Ore dictionary recipe cannot specify specific ingredients! Use getInput and parse it for your needs.");
	}

	@Override
	public List<ItemStack> getTertiaryIngredients() {
		throw new UnsupportedOperationException("Ore dictionary recipe cannot specify specific ingredients! Use getInput and parse it for your needs.");
	}

	@Override
	public boolean areAdditionalRequirementsMet(final TileEntityOfferingTable te) {
		return true;
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
	public BasicTableOreCraftingHandler copy() {
		final BasicTableOreCraftingHandler ore = new BasicTableOreCraftingHandler(this.output, this.length, this.pointDrain);
		ore.crafting = this.crafting;
		ore.timer = this.timer;
		ore.recipeID = this.recipeID;
		ore.input = this.input;
		return ore;
	}

	@Override
	public boolean checkCompletion(final ItemStack base, final List<ItemStack> tertiary){
		final ArrayList<Object> required = new ArrayList<Object>(this.input);
		final Object obj = required.get(0);
		boolean match = false;
		if (obj instanceof ItemStack)
			match = OreDictionary.itemMatches((ItemStack)obj, base, false);
		else if (obj instanceof ArrayList)
		{
			final Iterator<ItemStack> itr = ((ArrayList<ItemStack>)obj).iterator();
			while (itr.hasNext() && !match)
				match = OreDictionary.itemMatches(itr.next(), base, false);
		}
		if(match)
			required.remove(0);
		else
			return false;

		for (final ItemStack slot:tertiary)
			if (slot != null)
			{
				boolean inRecipe = false;
				final Iterator<Object> req = required.iterator();

				while (req.hasNext())
				{
					match = false;

					final Object next = req.next();

					if (next instanceof ItemStack)
						match = OreDictionary.itemMatches((ItemStack)next, slot, false);
					else if (next instanceof ArrayList)
					{
						final Iterator<ItemStack> itr = ((ArrayList<ItemStack>)next).iterator();
						while (itr.hasNext() && !match)
							match = OreDictionary.itemMatches(itr.next(), slot, false);
					}

					if (match)
					{
						inRecipe = true;
						required.remove(next);
						break;
					}
				}

				if (!inRecipe)
					return false;
			}

		return required.isEmpty();
	}

	@Override
	public NBTTagCompound toNBT(NBTTagCompound comp) {
		comp = super.toNBT(comp);
		comp.setInteger("timer", this.timer);
		comp.setBoolean("crafting", this.crafting);
		return comp;
	}

	@Override
	public BasicTableOreCraftingHandler copyWithNBT(final NBTTagCompound comp) {
		final BasicTableOreCraftingHandler handler = this.copy();
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
