package me.superckl.prayers.inventory;

import java.util.List;

import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketInventorySlotChanged;
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
		if(this.level.isClientSide)
			return ActionResultType.SUCCESS;
		final ItemStack stack = this.getItem(slot);
		if(player.isCrouching()) {
			if(!stack.isEmpty() && player.getItemInHand(hand).isEmpty()) {
				player.addItem(stack);
				this.onSlotChange(slot, true);
				return ActionResultType.CONSUME;
			}
		}else if(stack.isEmpty()) {
			final ItemStack held = player.getItemInHand(hand);
			if(this.canPlaceItem(slot, held)) {
				this.setItem(slot, held.copy());
				if(!player.isCreative())
					player.setItemInHand(hand, ItemStack.EMPTY);
				this.onSlotChange(slot, true);
				return ActionResultType.CONSUME;
			}
		}else {
			final ItemStack held = player.getItemInHand(hand);
			if(held.sameItem(stack) && ItemStack.tagMatches(held, stack)) {
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
		this.setChanged();
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(final int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack removeItem(final int index, final int count) {
		final ItemStack stack = ItemStackHelper.removeItem(this.items, index, count);
		if(this.getItem(index).isEmpty() && !stack.isEmpty())
			this.onSlotChange(index, true);
		else
			this.onSlotChange(index, false);
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(final int index) {
		final ItemStack stack = ItemStackHelper.takeItem(this.items, index);
		this.onSlotChange(index, true);
		return stack;
	}

	@Override
	public void setItem(final int index, final ItemStack stack) {
		final ItemStack oldStack = this.items.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize())
			stack.setCount(this.getMaxStackSize());
		this.onSlotChange(index, !stack.sameItem(oldStack));
	}

	@Override
	public int getContainerSize() {
		return this.invSize;
	}

	@Override
	public boolean stillValid(final PlayerEntity player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.items.clear();
		this.onSlotChange(-1, true);
	}

	@Override
	public void setChanged() {
		this.detectAndSendChanges();
		super.setChanged();
	}

	public void detectAndSendChanges() {
		for (int i = 0; i < this.invSize; i++) {
			final ItemStack backup = this.itemsBack.get(i);
			final ItemStack stack = this.items.get(i);
			if(!ItemStack.matches(backup, stack)) {
				final boolean clientChanged = !backup.equals(stack, true);
				final ItemStack copy = stack.copy();
				this.itemsBack.set(i, copy);
				if(!this.level.isClientSide && clientChanged)
					PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
							new PacketInventorySlotChanged(this.worldPosition, i, copy));
			}
		}
	}

	public void readInventory(final CompoundNBT nbt) {
		final ListNBT inv = nbt.getList(InteractableInventoryTileEntity.INV_KEY, Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < this.invSize; i++)
			this.items.set(i, ItemStack.of(inv.getCompound(i)));
		for(int i = 0; i < this.invSize; i++)
			this.itemsBack.set(i, this.items.get(i).copy());
	}

	public CompoundNBT writeInventory(final CompoundNBT compound) {
		final ListNBT inv = new ListNBT();
		this.items.forEach(stack -> inv.add(stack.save(new CompoundNBT())));
		compound.put(InteractableInventoryTileEntity.INV_KEY, inv);
		return compound;
	}

}
