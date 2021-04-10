package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketSetPrayerPoints extends PrayerUserPacket{

	private final double amount;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeDouble(this.amount);
	}

	public static PacketSetPrayerPoints decode(final PacketBuffer buffer) {
		return PrayerUserPacket.decode(PacketSetPrayerPoints.builder(), buffer).amount(buffer.readDouble()).build();
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		//Only the server should be sending this packet, and never on login
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			context.enqueueWork(() -> this.getUser(context).setCurrentPrayerPoints(this.amount));
	}

}
