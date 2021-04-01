package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@RequiredArgsConstructor
public class PacketSetInventoryItemPoints {

	private final float points;
	private final int slot;
	private final boolean isEquipment;
	private final EquipmentSlotType type;

	public PacketSetInventoryItemPoints(final float points, final int slot) {
		this.points = points;
		this.slot = slot;
		this.isEquipment = false;
		this.type = null;
	}

	public PacketSetInventoryItemPoints(final float points, final EquipmentSlotType type) {
		this.points = points;
		this.type = type;
		this.isEquipment = true;
		this.slot = -1;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeFloat(this.points);
		buffer.writeBoolean(this.isEquipment);
		if(this.isEquipment)
			buffer.writeEnum(this.type);
		else
			buffer.writeInt(this.slot);
	}

	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		//Only the server should be sending these packets
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				final ItemStack stack;
				if(this.isEquipment)
					stack = Minecraft.getInstance().player.getItemBySlot(this.type);
				else
					stack = Minecraft.getInstance().player.inventory.getItem(this.slot);
				if(!stack.isEmpty() && stack.getItem() instanceof PrayerInventoryItem)
					CapabilityHandler.getPrayerCapability(stack).setCurrentPrayerPoints(this.points);
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketSetInventoryItemPoints decode(final PacketBuffer buffer) {
		final float points = buffer.readFloat();
		final boolean isEquip = buffer.readBoolean();
		if(isEquip)
			return new PacketSetInventoryItemPoints(points, buffer.readEnum(EquipmentSlotType.class));
		return new PacketSetInventoryItemPoints(points, buffer.readInt());
	}

}
