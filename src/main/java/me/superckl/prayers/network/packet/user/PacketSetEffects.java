package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.capability.LivingPrayerUser;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketSetEffects extends PrayerUserPacket{

	private final Prayer prayer;
	private final boolean attach;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeRegistryId(this.prayer);
		buffer.writeBoolean(this.attach);
	}

	@Override
	public void handle(final Supplier<Context> supplier) {
		//Only the server should be sending these packets
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				final LivingPrayerUser<?> user =  this.getUser(supplier.get());
				if(this.attach)
					user.attachEffects(this.prayer);
				else
					user.detachEffects(this.prayer);
			});
		super.handle(supplier);
	}

	public static PacketSetEffects decode(final PacketBuffer buffer) {
		return PrayerUserPacket.decode(PacketSetEffects.builder(), buffer)
				.prayer(buffer.readRegistryIdSafe(Prayer.class)).attach(buffer.readBoolean()).build();
	}
}
