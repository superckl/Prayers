package me.superckl.prayers.network.packet;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

@SuperBuilder
public abstract class PrayersPacket {

	//We have to include this because the no-argument readString method is client only
	public static final int BUFFER_STRING_LENGTH = 32767;

	protected final int entityID;

	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.entityID);
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		supplier.get().setPacketHandled(true);
	}

	protected IPrayerUser getUser(World world) {
		return this.getUser(world.getEntityByID(this.entityID));
	}
	
	protected IPrayerUser getUser(Entity entity) {
		return entity.getCapability(Prayers.PRAYER_USER_CAPABILITY)
				.orElseThrow(() -> new IllegalStateException(String.format("Accepted packet for entity %s with no prayer capability!", entity.toString())));
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends PrayersPacketBuilder<?, ?>> T decode(final T builder, final PacketBuffer buffer){
		//Calling entityID does not return "T", but it is of type T
		return (T) builder.entityID(buffer.readInt());
	}

}
