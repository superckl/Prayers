package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@SuperBuilder
public class PacketSetInventoryItemPoints extends InventoryItemPacket{

	private final double points;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeDouble(this.points);
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		//Only the server should be sending these packets
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				final ItemStack stack = this.getStack(supplier.get());
				if(!stack.isEmpty() && stack.getItem() instanceof PrayerInventoryItem)
					CapabilityHandler.getPrayerCapability(stack).setCurrentPrayerPoints(this.points);
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketSetInventoryItemPoints decode(final PacketBuffer buffer) {
		return InventoryItemPacket.decode(PacketSetInventoryItemPoints.builder(), buffer).points(buffer.readDouble()).build();
	}

}
