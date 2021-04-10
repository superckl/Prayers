package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.item.PrayerInventoryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@SuperBuilder
public class PacketDeactivateInventoryPrayer extends InventoryItemPacket{

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		//Only the server should be sending these packets
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				final ItemStack stack = this.getStack(supplier.get());
				if(!stack.isEmpty() && stack.getItem() instanceof PrayerInventoryItem)
					CapabilityHandler.getPrayerCapability(stack).deactivateAllPrayers(ClientHelper.getPlayer());
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketDeactivateInventoryPrayer decode(final PacketBuffer buffer) {
		return InventoryItemPacket.decode(PacketDeactivateInventoryPrayer.builder(), buffer).build();
	}

}
