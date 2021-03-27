package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.capability.PlayerPrayerUser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketSetPrayerLevel extends PrayerUserPacket{

	private final int level;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeInt(this.level);
	}

	public static PacketSetPrayerLevel decode(final PacketBuffer buffer) {
		return PrayerUserPacket.decode(PacketSetPrayerLevel.builder(), buffer).level(buffer.readInt()).build();
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		//Only the server should be sending this packet, and never on login
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			context.enqueueWork(() -> ((PlayerPrayerUser) this.getUser(Minecraft.getInstance().level)).setPrayerLevel(this.level));
	}

}
