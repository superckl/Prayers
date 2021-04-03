package me.superckl.prayers.block.entity;

import javax.annotation.Nonnull;

import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketInventorySlotChanged;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class InteractableInventoryTileEntity extends TileEntity{

	public static final String INV_KEY = "inventory";

	public InteractableInventoryTileEntity(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public ActionResultType onInteract(final PlayerEntity player, final Hand hand, final IItemHandlerModifiable handler, final int slot) {
		final ItemStack stack = handler.getStackInSlot(slot);
		final ItemStack heldStack = player.getItemInHand(hand).copy();
		if(!player.isCrouching()) {
			final ItemStack remaining = handler.insertItem(slot, heldStack, false);
			if(!player.isCreative() && remaining != heldStack)
				player.setItemInHand(hand, remaining);
			return ActionResultType.sidedSuccess(this.level.isClientSide);
		}
		if(!stack.isEmpty() && heldStack.isEmpty()) {
			player.setItemInHand(hand, stack);
			handler.setStackInSlot(slot, ItemStack.EMPTY);
			return ActionResultType.sidedSuccess(this.level.isClientSide);
		}
		return ActionResultType.PASS;
	}

	public IItemHandlerModifiable getItemHandlerForSide(final Direction side) {
		return this.getInternalItemHandler();
	}

	public abstract IItemHandlerModifiable getInternalItemHandler();

	public void onSlotChange(final int slot) {
		this.setChanged();
		this.sendSlot(slot);
	}

	public void sendSlot(final int slot) {
		if(!this.level.isClientSide)
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
					new PacketInventorySlotChanged(this.worldPosition, slot, this.getInternalItemHandler().getStackInSlot(slot)));
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null)
				return LazyOptional.of(this::getInternalItemHandler).cast();
			return LazyOptional.of(() -> this.getItemHandlerForSide(facing)).cast();
		}
		return super.getCapability(capability, facing);
	}

}
