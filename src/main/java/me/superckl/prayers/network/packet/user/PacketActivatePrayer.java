package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.capability.PlayerPrayerUser.Result;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketActivatePrayer extends PrayerUserPacket{

	private final Prayer prayer;

	@Override
	public void encode(final PacketBuffer buffer) {
		buffer.writeRegistryId(this.prayer);
		super.encode(buffer);
	}

	public static PacketActivatePrayer decode(final PacketBuffer buffer) {
		final Prayer prayer = buffer.readRegistryIdSafe(Prayer.class);
		return PrayerUserPacket.decode(PacketActivatePrayer.builder(), buffer).prayer(prayer).build();
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT || context.getDirection() == NetworkDirection.LOGIN_TO_CLIENT)
			context.enqueueWork(() -> this.getUser(context).activatePrayer(this.prayer));
		else
			context.enqueueWork(() -> {
				//Check the client is not attempting to activate a prayer on another entity
				if (this.entityID != context.getSender().getId()) {
					//Respond with a deactivate packet if they are
					PrayersPacketHandler.INSTANCE.reply(PacketDeactivatePrayer.builder().entityID(this.entityID).prayer(this.prayer).build(), context);
					return;
				}
				final PlayerPrayerUser prayerUser = CapabilityHandler.getPrayerCapability(context.getSender());
				//Since this is from a client, defensively check this can actually be done
				if(prayerUser.canActivatePrayer(this.prayer) != Result.YES)
					//Tell the player they cannot activate that prayer
					PrayersPacketHandler.INSTANCE.reply(PacketDeactivatePrayer.builder().entityID(this.entityID).prayer(this.prayer).build(), context);
				else
					prayerUser.activatePrayer(this.prayer); //This should automatically sync to all tracking clients
			});
	}

}
