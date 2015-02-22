package me.superckl.prayers.integration.bloodmagic;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.altar.crafting.TableCraftingHandler;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.items.EnergyBattery;


public class OrbChargeTableRecipe extends TableCraftingHandler{

	private final int rate = (int) Math.ceil(Prayers.getInstance().getConfig().getOrbExchangeRate()/2);
	private ItemStack orb;
	protected boolean crafting;
	private boolean addedNone;

	@Override
	public boolean areBaseRequirementsMet(final TileEntityOfferingTable te) {
		final ItemStack currentItem = te.getCurrentItem();
		if((currentItem != null) && (currentItem.getItem() instanceof EnergyBattery) && !SoulNetworkHandler.getOwnerName(currentItem).isEmpty() && te.getTertiaryIngredients().isEmpty()){
			this.orb = currentItem;
			return true;
		}
		return false;
	}

	@Override
	public boolean areAdditionalRequirementsMet(final TileEntityOfferingTable te) {
		if(this.orb == null){
			final ItemStack currentItem = te.getCurrentItem();
			if((currentItem != null) && (currentItem.getItem() instanceof EnergyBattery) && !SoulNetworkHandler.getOwnerName(currentItem).isEmpty())
				this.orb = currentItem;
			else
				return false;
		}
		return SoulNetworkHandler.getCurrentEssence(SoulNetworkHandler.getOwnerName(this.orb)) < SoulNetworkHandler.getMaximumForOrbTier(((EnergyBattery)this.orb.getItem()).getOrbLevel());
	}

	@Override
	public void beginCrafting(final TileEntityOfferingTable te) {
		this.crafting = true;
	}

	@Override
	public void handleUpdate(final TileEntityOfferingTable te) {
		if(this.orb == null){
			final ItemStack currentItem = te.getCurrentItem();
			if((currentItem != null) && (currentItem.getItem() instanceof EnergyBattery) && !SoulNetworkHandler.getOwnerName(currentItem).isEmpty())
				this.orb = currentItem;
			else{
				this.addedNone = true;
				return;
			}
		}
		int added = 0;
		if(te.getAltar().getPrayerPoints() >= 0.5F){
			te.getAltar().setPrayerPoints(te.getAltar().getPrayerPoints()-0.5F);
			added = SoulNetworkHandler.addCurrentEssenceToMaximum(SoulNetworkHandler.getOwnerName(this.orb), this.rate, SoulNetworkHandler.getMaximumForOrbTier(((EnergyBattery)this.orb.getItem()).getOrbLevel()));
		}
		if(added <= 0)
			this.addedNone = true;
	}

	@Override
	public boolean isCrafting(final TileEntityOfferingTable te) {
		return this.crafting;
	}

	@Override
	public boolean isComplete(final TileEntityOfferingTable te) {
		return this.addedNone;
	}

	@Override
	public void onPostComplete(final TileEntityOfferingTable te) {}

	@Override
	public TableCraftingHandler copy() {
		final OrbChargeTableRecipe recipe = new OrbChargeTableRecipe();
		recipe.orb = this.orb;
		recipe.crafting = this.crafting;
		recipe.addedNone = this.addedNone;
		recipe.recipeID = this.recipeID;
		return recipe;
	}

	@Override
	public TableCraftingHandler copyWithNBT(final NBTTagCompound comp) {
		final OrbChargeTableRecipe recipe = new OrbChargeTableRecipe();
		recipe.crafting = comp.getBoolean("crafting");
		recipe.addedNone = comp.getBoolean("addedNone");
		return recipe;
	}

	@Override
	public NBTTagCompound toNBT(NBTTagCompound compound) {
		compound = super.toNBT(compound);
		compound.setBoolean("crafting", this.crafting);
		compound.setBoolean("addedNone", this.addedNone);
		return compound;
	}

}
