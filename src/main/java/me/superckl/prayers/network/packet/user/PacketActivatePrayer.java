package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.Prayer;
import me.superckl.prayers.capability.IPrayerUser;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketActivatePrayer extends PrayerUserPacket{

	private final Prayer prayer;

	@Override
	public void encode(final PacketBuffer buffer) {
		buffer.writeUtf(this.prayer.getRegistryName().toString());
		super.encode(buffer);
	}

	public static PacketActivatePrayer decode(final PacketBuffer buffer) {
		final ResourceLocation loc = new ResourceLocation(buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH));
		final Prayer prayer = GameRegistry.findRegistry(Prayer.class).getValue(loc);
		if (prayer == null)
			throw new IllegalArgumentException(String.format("Invalid prayer location %s!", loc.toString()));
		return PrayerUserPacket.decode(PacketActivatePrayer.builder(), buffer).prayer(prayer).build();
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT || context.getDirection() == NetworkDirection.LOGIN_TO_CLIENT)
			context.enqueueWork(() -> this.getUser(Minecraft.getInstance().level).activatePrayer(this.prayer));
		else
			context.enqueueWork(() -> {
				//Check the client is not attempting to activate a prayer an another entity
				if (this.entityID != context.getSender().getId()) {
					//Respond with a deactivate packet if they are
					PrayersPacketHandler.INSTANCE.reply(PacketDeactivatePrayer.builder().entityID(this.entityID).prayer(this.prayer).build(), context);
					return;
				}
				final IPrayerUser prayerUser = IPrayerUser.getUser(context.getSender());
				if(!prayerUser.canActivatePrayer(this.prayer))
					//Tell the player they cannot activate that prayer
					PrayersPacketHandler.INSTANCE.reply(PacketDeactivatePrayer.builder().entityID(this.entityID).prayer(this.prayer).build(), context);
				else
					prayerUser.activatePrayer(this.prayer);
			});
	}

}
