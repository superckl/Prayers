package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.init.ModItems;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@RequiredArgsConstructor
public class PacketTalismanToggle {

	private final int slot;

	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.slot);
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
			supplier.get().enqueueWork(() -> {
				final ServerPlayerEntity player = supplier.get().getSender();
				if(player == null)
					return;
				final ItemStack stack = player.inventory.getItem(this.slot);
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get())
					ModItems.TALISMAN.get().toggle(stack, player);
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketTalismanToggle decode(final PacketBuffer buffer) {
		final int slot = buffer.readInt();
		return new PacketTalismanToggle(slot);
	}

}
