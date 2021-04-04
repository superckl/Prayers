package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketDeactivatePrayer  extends PrayerUserPacket{

	private final Prayer prayer;

	@Override
	public void encode(final PacketBuffer buffer) {
		buffer.writeRegistryId(this.prayer);
		super.encode(buffer);
	}

	public static PacketDeactivatePrayer decode(final PacketBuffer buffer) {
		final Prayer prayer = buffer.readRegistryIdSafe(Prayer.class);
		return PrayerUserPacket.decode(PacketDeactivatePrayer.builder(), buffer).prayer(prayer).build();
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT || context.getDirection() == NetworkDirection.LOGIN_TO_CLIENT)
			context.enqueueWork(() -> this.getUser(context).deactivatePrayer(this.prayer));
		else
			context.enqueueWork(() -> {
				//Since this is from a client, defensively check this can actually be done
				if (this.entityID != context.getSender().getId()) {
					//Tell the client they can't deactivate that prayer
					PrayersPacketHandler.INSTANCE.reply(PacketActivatePrayer.builder().entityID(this.entityID).prayer(this.prayer).build(), context);
					return;
				}
				CapabilityHandler.getPrayerCapability(context.getSender()).deactivatePrayer(this.prayer); //This should automatically sync to all tracking clients
			});
	}

}
