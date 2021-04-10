package me.superckl.prayers.inventory;

import java.util.function.BiPredicate;
import java.util.function.Function;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class MainInventorySlotHelper extends SlotHelper{

	public static final BiPredicate<PlayerEntity, Slot> PREDICATE = (player, slot) -> slot.container == player.inventory;
	public static final Function<Slot, MainInventorySlotHelper> FACTORY = slot -> new MainInventorySlotHelper(slot.getSlotIndex());
	public static final Function<PacketBuffer, MainInventorySlotHelper> DESERIALIZER = MainInventorySlotHelper::deserialize;

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
	public ItemStack getStack(final PlayerEntity entity) {
		if(this.type != null)
			return entity.getItemBySlot(this.type);
		return entity.inventory.getItem(this.slot);
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
