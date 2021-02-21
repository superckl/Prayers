package me.superckl.prayers.block;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.inventory.InteractableInventoryTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraftforge.common.util.Constants;

public class CraftingStandTileEntity extends InteractableInventoryTileEntity implements ISidedInventory, ITickableTileEntity{

	public static final String CRAFTING_STAND_KEY = "crafting_stand";

	public static final Reference2IntMap<Direction> dirToSlot = Util.make(new Reference2IntArrayMap<Direction>(5), map -> {
		Direction.Plane.HORIZONTAL.forEach(dir -> map.put(dir, dir.getHorizontalIndex()));
		map.put(Direction.UP, 4);
	});

	public static final Int2ReferenceMap<Direction> slotToDir = Util.make(new Int2ReferenceArrayMap<Direction>(5), map -> {
		CraftingStandTileEntity.dirToSlot.keySet().forEach(dir -> map.put(CraftingStandTileEntity.dirToSlot.getInt(dir), dir));
	});

	public CraftingStandTileEntity() {
		super(ModTiles.CRAFTING_STAND.get(), 5);
	}

	public ActionResultType onActivate(final PlayerEntity player, final Hand hand, final Direction dir) {
		final int slot = CraftingStandTileEntity.dirToSlot.getInt(dir);
		return this.onInteract(player, hand, slot);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSlotChange(final int slot, final boolean itemChanged) {
		if(!this.world.isRemote)
			this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NO_RERENDER);
		super.onSlotChange(slot, itemChanged);
	}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		return super.isItemValidForSlot(index, stack);
	}

	@Override
	public int[] getSlotsForFace(final Direction side) {
		if(CraftingStandTileEntity.dirToSlot.containsKey(side) && this.hasStand(side))
			return new int[] {CraftingStandTileEntity.dirToSlot.getInt(side)};
		else
			return new int[0];
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
