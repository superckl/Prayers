package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketDeactivateAllPrayers  extends PrayerUserPacket{

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
	}

	public static PacketDeactivateAllPrayers decode(final PacketBuffer buffer) {
		return PrayerUserPacket.decode(PacketDeactivateAllPrayers.builder(), buffer).build();
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT )
			context.enqueueWork(() -> {
				this.getUser(context).deactivateAllPrayers();
			});
	}

}
