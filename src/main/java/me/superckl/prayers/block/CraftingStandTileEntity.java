package me.superckl.prayers.block;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import me.superckl.prayers.LogHelper;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.inventory.InteractableInventoryTileEntity;
import me.superckl.prayers.recipe.AbstractAltarCraftingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraftforge.common.util.LazyOptional;

public class CraftingStandTileEntity extends InteractableInventoryTileEntity implements ISidedInventory, ITickableTileEntity{

	public static final String CRAFTING_STAND_KEY = "crafting_stand";

	public static final Reference2IntMap<Direction> dirToSlot = Util.make(new Reference2IntArrayMap<Direction>(5), map -> {
		Direction.Plane.HORIZONTAL.forEach(dir -> map.put(dir, dir.getHorizontalIndex()));
		map.put(Direction.UP, 4);
	});

	public static final Int2ReferenceMap<Direction> slotToDir = Util.make(new Int2ReferenceArrayMap<Direction>(5), map -> {
		CraftingStandTileEntity.dirToSlot.keySet().forEach(dir -> map.put(CraftingStandTileEntity.dirToSlot.getInt(dir), dir));
	});

	private AbstractAltarCraftingRecipe activeRecipe;
	private int[] recipeMapping;
	private float consumedPoints;
	private boolean inventoryChanged = true;

	public CraftingStandTileEntity() {
		super(ModTiles.CRAFTING_STAND.get(), 5);
	}

	public ActionResultType onActivate(final PlayerEntity player, final Hand hand, final Direction dir) {
		final int slot = CraftingStandTileEntity.dirToSlot.getInt(dir);
		return this.onInteract(player, hand, slot);
	}

	@Override
	public void tick() {
		if(this.world.isRemote)
			return;
		if(this.inventoryChanged) {
			this.inventoryChanged = false;
			final LazyOptional<Pair<AbstractAltarCraftingRecipe, int[]>> pair = this.findRecipe();
			if(pair.isPresent()) {
				final AbstractAltarCraftingRecipe recipe = pair.orElse(null).getKey();
				final int[] mapping = pair.orElse(null).getValue();
				boolean enoughItem = true;
				for (int i = 0; i < mapping.length; i++)
					if(this.getStackInSlot(i).getCount() < recipe.getIngredientCounts()[mapping[i]]) {
						enoughItem = false;
						break;
					}
				if(enoughItem) {
					if(this.activeRecipe != recipe) {
						this.activeRecipe = recipe;
						this.recipeMapping = mapping;
						this.consumedPoints = 0;
					}
					this.tickCrafting();
				} else
					this.clearRecipe();
			} else
				this.clearRecipe();
		}else if(this.activeRecipe != null)
			this.tickCrafting();
	}

	public void clearRecipe() {
		this.activeRecipe = null;
		this.recipeMapping = null;
		this.consumedPoints = 0;
	}

	protected void tickCrafting() {
		this.findValidAltar().ifPresent(altar -> {
			if(!this.canRecipeOutput(this.activeRecipe))
				return; //no room to output, don't tick
			final float reqPoints = this.activeRecipe.getPoints();
			final float transfer = altar.getAltarType().getTransferRate();
			final float toTransfer = Math.min(transfer, reqPoints-this.consumedPoints);
			final float transferred = altar.removePoints(toTransfer);
			this.consumedPoints += transferred;
			if(this.consumedPoints >= reqPoints)
				this.finishCrafting();
		});
	}

	protected void finishCrafting() {
		for(int i = 0; i < this.recipeMapping.length; i++)
			this.decrStackSize(i, this.activeRecipe.getIngredientCounts()[this.recipeMapping[i]]);
		final ItemStack output = this.getStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP));
		if(output.isEmpty())
			this.setInventorySlotContents(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP), this.activeRecipe.getRecipeOutput().copy());
		else
			output.grow(this.activeRecipe.getRecipeOutput().getCount());
		this.activeRecipe = null;
		this.recipeMapping = null;
		this.consumedPoints = 0;
	}

	protected boolean canRecipeOutput(final AbstractAltarCraftingRecipe recipe) {
		if(!this.hasOutputSlot())
			return false;
		final ItemStack output = this.getStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP));
		return output.isEmpty() || ItemStack.areItemsEqual(output, recipe.getRecipeOutput()) && ItemStack.areItemStackTagsEqual(output, recipe.getRecipeOutput()) && output.getCount()+recipe.getRecipeOutput().getCount() < output.getMaxStackSize();
	}

	public boolean isCrafting() {
		return this.activeRecipe != null;
	}
	
	public float getCraftingProgress() {
		if(!this.isCrafting())
			return 0;
		return this.consumedPoints/this.activeRecipe.getPoints();
	}
	
	@Override
	public void onSlotChange(final int slot, final boolean itemChanged) {
		this.inventoryChanged = true;
		super.onSlotChange(slot, itemChanged);
	}

	protected LazyOptional<AltarTileEntity> findValidAltar() {
		final TileEntity below = this.world.getTileEntity(this.pos.down());
		if(below != null && below instanceof AltarTileEntity) {
			final AltarTileEntity altar = (AltarTileEntity) below;
			return altar.canRegen() ? LazyOptional.of(() -> altar):LazyOptional.empty();
		}else
			return LazyOptional.empty();
	}

	public LazyOptional<Pair<AbstractAltarCraftingRecipe, int[]>> findRecipe(){
		final List<AbstractAltarCraftingRecipe> recipes = this.world.getRecipeManager().getRecipesForType(AbstractAltarCraftingRecipe.TYPE);
		final List<ItemStack> inventory = this.items.subList(0, 4);
		for(final AbstractAltarCraftingRecipe recipe:recipes) {
			final int[] mapping = recipe.findMapping(inventory);
			if(mapping != null)
				if(this.canRecipeOutput(recipe))
					return LazyOptional.of(() -> Pair.of(recipe, mapping));
				else
					return LazyOptional.empty(); //return because the matching recipe cannot output
		}
		return LazyOptional.empty();
	}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		return CraftingStandTileEntity.slotToDir.get(index) != Direction.UP;
	}

	@Override
	public int[] getSlotsForFace(final Direction side) {
		if(CraftingStandTileEntity.dirToSlot.containsKey(side) && this.hasStand(side))
			return new int[] {CraftingStandTileEntity.dirToSlot.getInt(side)};
		else
			return new int[0];
	}

	public boolean hasOutputSlot() {
		return this.hasStand(Direction.UP);
	}

	public boolean hasStand(final Direction dir) {
		final BlockState state = this.getBlockState();
		return ((CraftingStandBlock)state.getBlock()).hasStand(state, dir);
	}

	@Override
	public boolean canInsertItem(final int index, final ItemStack itemStackIn, final Direction direction) {
		return direction != Direction.UP && Arrays.stream(this.getSlotsForFace(direction)).anyMatch(slot -> slot == index) && this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(final int index, final ItemStack stack, final Direction direction) {
		return Arrays.stream(this.getSlotsForFace(direction)).anyMatch(slot -> slot == index);
	}

	@Override
	public CompoundNBT write(final CompoundNBT compound) {
		final CompoundNBT craft_data = new CompoundNBT();
		super.writeInventory(craft_data);
		compound.put(CraftingStandTileEntity.CRAFTING_STAND_KEY, craft_data);
		return super.write(compound);
	}

	@Override
	public void read(final BlockState state, final CompoundNBT nbt) {
		final CompoundNBT craft_data = nbt.getCompound(CraftingStandTileEntity.CRAFTING_STAND_KEY);
		this.readInventory(craft_data);
		super.read(state, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.read(state, tag);
	}

}
