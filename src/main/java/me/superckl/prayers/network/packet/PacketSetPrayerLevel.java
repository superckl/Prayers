package me.superckl.prayers.network.packet;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketSetPrayerLevel extends PrayersPacket{

	private final int level;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeFloat(this.level);
	}

	public static PacketSetPrayerLevel decode(final PacketBuffer buffer) {
		return PrayersPacket.decode(PacketSetPrayerLevel.builder(), buffer).level(buffer.readInt()).build();
	}

	@Override
	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		//Only the server should be sending this packet, and never on login
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			context.enqueueWork(() -> this.getUser(Minecraft.getInstance().world).setPrayerLevel(this.level));
	}

}
