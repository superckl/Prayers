package me.superckl.prayers.inventory;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContainerSlotHelper extends SlotHelper{

	private final int containerId;
	private final int slot;

	public ContainerSlotHelper(final int containerId, final Slot slot) {
		this.containerId = containerId;
		this.slot = slot.index;
	}

	@Override
	public Optional<ItemStack> getStack(final PlayerEntity entity) {
		if(entity.containerMenu.containerId != this.containerId)
			return Optional.empty();
		return Optional.of(entity.containerMenu.getSlot(this.slot).getItem());
	}

	@Override
	public void serialize(final PacketBuffer buffer) {
		buffer.writeVarInt(this.containerId);
		buffer.writeVarInt(this.slot);
	}

	public static ContainerSlotHelper deserialize(final PacketBuffer buffer) {
		return new ContainerSlotHelper(buffer.readVarInt(), buffer.readVarInt());
	}

}
