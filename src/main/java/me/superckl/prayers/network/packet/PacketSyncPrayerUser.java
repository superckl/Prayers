package me.superckl.prayers.network.packet;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@SuperBuilder
public class PacketSyncPrayerUser extends PrayersPacket{

	public static final String USER_KEY = "prayer_user";

	private final INBT userNBT;

	@Override
	public void encode(final PacketBuffer buffer) {
		final CompoundNBT nbt = new CompoundNBT();
		nbt.put(PacketSyncPrayerUser.USER_KEY, this.userNBT);
		buffer.writeCompoundTag(nbt);
		super.encode(buffer);
	}

	public static PacketSyncPrayerUser decode(final PacketBuffer buffer) {
		final CompoundNBT nbt = buffer.readCompoundTag();
		return PrayersPacket.decode(PacketSyncPrayerUser.builder(), buffer).userNBT(nbt.get(PacketSyncPrayerUser.USER_KEY)).build();
	}

	@Override
	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		super.handle(supplier);
		final Context context = supplier.get();
		//Only the server should be sending these packets.
		if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT || context.getDirection() == NetworkDirection.LOGIN_TO_CLIENT)
			context.enqueueWork(() -> {
				final Entity entity = Minecraft.getInstance().world.getEntityByID(this.entityID);
				final LazyOptional<IPrayerUser> user = entity.getCapability(Prayers.PRAYER_USER_CAPABILITY);
				final IPrayerUser prayerUser = user.orElseThrow(() -> new IllegalStateException(String.format("Was told to sync prayer data on entity %s without prayer capabilities!",
						entity.toString())));
				Prayers.PRAYER_USER_CAPABILITY.getStorage().readNBT(Prayers.PRAYER_USER_CAPABILITY, prayerUser, null, this.userNBT);});
	}

}
