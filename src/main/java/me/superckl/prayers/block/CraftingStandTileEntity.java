package me.superckl.prayers.block;

import java.util.Arrays;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import me.superckl.prayers.init.ModTiles;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;

public class CraftingStandTileEntity extends TileEntity implements ISidedInventory{

	public static final Reference2IntMap<Direction> dirToSlot = Util.make(new Reference2IntArrayMap<Direction>(5), map -> {
		Direction.Plane.HORIZONTAL.forEach(dir -> map.put(dir, dir.getHorizontalIndex()));
		map.put(Direction.UP, 4);
	});

	public static final Int2ReferenceMap<Direction> slotToDir = Util.make(new Int2ReferenceArrayMap<Direction>(5), map -> {
		CraftingStandTileEntity.dirToSlot.keySet().forEach(dir -> map.put(CraftingStandTileEntity.dirToSlot.getInt(dir), dir));
	});

	protected List<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

	public CraftingStandTileEntity() {
		super(ModTiles.CRAFTING_STAND.get());
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack decrStackSize(final int index, final int count) {
		return ItemStackHelper.getAndSplit(this.items, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(final int index) {
		return ItemStackHelper.getAndRemove(this.items, index);
	}

	@Override
	public void setInventorySlotContents(final int index, final ItemStack stack) {
		this.items.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public boolean isUsableByPlayer(final PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {
		this.items.clear();
	}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		return ISidedInventory.super.isItemValidForSlot(index, stack);
	}

	@Override
	public int[] getSlotsForFace(final Direction side) {
		if(CraftingStandTileEntity.dirToSlot.containsKey(side))
			return new int[] {CraftingStandTileEntity.dirToSlot.getInt(side)};
		else
			return new int[0];
	}

	@Override
	public boolean canInsertItem(final int index, final ItemStack itemStackIn, final Direction direction) {
		return Arrays.stream(this.getSlotsForFace(direction)).anyMatch(slot -> slot == index) && this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(final int index, final ItemStack stack, final Direction direction) {
		return Arrays.stream(this.getSlotsForFace(direction)).anyMatch(slot -> slot == index);
	}

}
