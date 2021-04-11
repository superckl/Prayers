package me.superckl.prayers.inventory;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class MainInventorySlotHelper extends SlotHelper{

	private final int slot;
	private final EquipmentSlotType type;

	public MainInventorySlotHelper(final int slot) {
		this.slot = slot;
		this.type = null;
	}

	public MainInventorySlotHelper(final EquipmentSlotType type) {
		this.slot = -1;
		this.type = type;
	}

	@Override
	public Optional<ItemStack> getStack(final PlayerEntity entity) {
		if(this.type != null)
			return Optional.of(entity.getItemBySlot(this.type));
		return Optional.of(entity.inventory.getItem(this.slot));
	}

	@Override
	public boolean canModify(final PlayerEntity player) {
		return true;
	}

	@Override
	public void serialize(final PacketBuffer buffer) {
		if(this.type == null) {
			buffer.writeBoolean(false);
			buffer.writeVarInt(this.slot);
		}else {
			buffer.writeBoolean(true);
			buffer.writeEnum(this.type);
		}
	}

	public static MainInventorySlotHelper deserialize(final PacketBuffer buffer) {
		if(buffer.readBoolean())
			return new MainInventorySlotHelper(buffer.readEnum(EquipmentSlotType.class));
		return new MainInventorySlotHelper(buffer.readVarInt());
	}

}
