package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.IPrayerUser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketSyncPrayerUser extends PrayerUserPacket{

	public static final String USER_KEY = "prayer_user";

	private final INBT userNBT;

	@Override
	public void encode(final PacketBuffer buffer) {
		final CompoundNBT nbt = new CompoundNBT();
		nbt.put(PacketSyncPrayerUser.USER_KEY, this.userNBT);
		buffer.writeNbt(nbt);
		super.encode(buffer);
	}

	public static PacketSyncPrayerUser decode(final PacketBuffer buffer) {
		final CompoundNBT nbt = buffer.readNbt();
		return PrayerUserPacket.decode(PacketSyncPrayerUser.builder(), buffer).userNBT(nbt.get(PacketSyncPrayerUser.USER_KEY)).build();
	}

	@Override
	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		//Only the server should be sending these packets.
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT || context.getDirection() == NetworkDirection.LOGIN_TO_CLIENT)
			context.enqueueWork(() -> {
				final IPrayerUser prayerUser = this.getUser(Minecraft.getInstance().level);
				Prayers.PRAYER_USER_CAPABILITY.readNBT(prayerUser, null, this.userNBT);});
	}

	public static PacketSyncPrayerUser fromPlayer(final PlayerEntity player){
		return PacketSyncPrayerUser.builder().entityID(player.getId()).userNBT(Prayers.PRAYER_USER_CAPABILITY.writeNBT(IPrayerUser.getUser(player), null)).build();
	}

}
