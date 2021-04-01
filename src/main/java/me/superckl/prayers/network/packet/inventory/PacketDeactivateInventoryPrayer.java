package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@RequiredArgsConstructor
public class PacketDeactivateInventoryPrayer {

	private final int slot;

	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.slot);
	}

	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		//Only the server should be sending these packets
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				final ItemStack stack = Minecraft.getInstance().player.inventory.getItem(this.slot);
				if(!stack.isEmpty() && stack.getItem() instanceof PrayerInventoryItem)
					CapabilityHandler.getPrayerCapability(stack).deactivateAllPrayers();
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketDeactivateInventoryPrayer decode(final PacketBuffer buffer) {
		final int slot = buffer.readInt();
		return new PacketDeactivateInventoryPrayer(slot);
	}

}
