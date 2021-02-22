package me.superckl.prayers.inventory;

import java.util.List;

import me.superckl.prayers.network.packet.PacketInventorySlotChanged;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class InteractableInventoryTileEntity extends TileEntity implements IInventory{

	public static final String INV_KEY = "inventory";

	protected final int invSize;
	protected final List<ItemStack> items;
	protected final List<ItemStack> itemsBack;

	public InteractableInventoryTileEntity(final TileEntityType<?> tileEntityTypeIn, final int invSize) {
		super(tileEntityTypeIn);
		this.invSize = invSize;
		this.items = NonNullList.withSize(invSize, ItemStack.EMPTY);
		this.itemsBack = NonNullList.withSize(invSize, ItemStack.EMPTY);
	}

	public ActionResultType onInteract(final PlayerEntity player, final Hand hand, final int slot) {
		if(this.world.isRemote)
			return ActionResultType.SUCCESS;
		final ItemStack stack = this.getStackInSlot(slot);
		if(player.isSneaking()) {
			if(!stack.isEmpty() && player.getHeldItem(hand).isEmpty()) {
				player.addItemStackToInventory(stack);
				this.onSlotChange(slot, true);
				return ActionResultType.CONSUME;
			}
		}else if(stack.isEmpty()) {
			final ItemStack held = player.getHeldItem(hand);
			if(this.isItemValidForSlot(slot, held)) {
				this.setInventorySlotContents(slot, held.copy());
				if(!player.isCreative())
					player.setHeldItem(hand, ItemStack.EMPTY);
				this.onSlotChange(slot, true);
				return ActionResultType.CONSUME;
			}
		}else {
			final ItemStack held = player.getHeldItem(hand);
			if(held.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(held, stack)) {
				final int toAdd = Math.min(stack.getMaxStackSize()-stack.getCount(), held.getCount());
				if(toAdd == 0)
					return ActionResultType.PASS;
				stack.grow(toAdd);
				if(!player.isCreative())
					held.shrink(toAdd);
				this.onSlotChange(slot, false);
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}

	public void onSlotChange(final int slot, final boolean itemChanged) {
		this.markDirty();
	}

	@Override
	public int getSizeInventory() {
		return this.invSize;
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
		final ItemStack stack = ItemStackHelper.getAndSplit(this.items, index, count);
		if(this.getStackInSlot(index).isEmpty() && !stack.isEmpty())
			this.onSlotChange(index, true);
		else
			this.onSlotChange(index, false);
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(final int index) {
		final ItemStack stack = ItemStackHelper.getAndRemove(this.items, index);
		this.onSlotChange(index, true);
		return stack;
	}

	@Override
	public void setInventorySlotContents(final int index, final ItemStack stack) {
		final ItemStack oldStack = this.items.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
		this.onSlotChange(index, !stack.isItemEqual(oldStack));
	}

	@Override
	public boolean isUsableByPlayer(final PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.items.clear();
		this.onSlotChange(-1, true);
	}

	@Override
	public void markDirty() {
		this.detectAndSendChanges();
		super.markDirty();
	}

	public void detectAndSendChanges() {
		for (int i = 0; i < this.invSize; i++) {
			final ItemStack backup = this.itemsBack.get(i);
			final ItemStack stack = this.items.get(i);
			if(!ItemStack.areItemStacksEqual(backup, stack)) {
				final boolean clientChanged = !backup.equals(stack, true);
				final ItemStack copy = stack.copy();
				this.itemsBack.set(i, copy);
				if(!this.world.isRemote && clientChanged)
					PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(this.pos)),
							new PacketInventorySlotChanged(this.pos, i, copy));
			}
		}
	}

	public void readInventory(final CompoundNBT nbt) {
		final ListNBT inv = nbt.getList(InteractableInventoryTileEntity.INV_KEY, Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < this.invSize; i++)
			this.items.set(i, ItemStack.read(inv.getCompound(i)));
		for(int i = 0; i < this.invSize; i++)
			this.itemsBack.set(i, this.items.get(i).copy());
	}

	public CompoundNBT writeInventory(final CompoundNBT compound) {
		final ListNBT inv = new ListNBT();
		this.items.forEach(stack -> inv.add(stack.write(new CompoundNBT())));
		compound.put(InteractableInventoryTileEntity.INV_KEY, inv);
		return compound;
	}

}
