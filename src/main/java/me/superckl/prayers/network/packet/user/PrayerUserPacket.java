package me.superckl.prayers.network.packet.user;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.LivingPrayerUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

@SuperBuilder
public abstract class PrayerUserPacket {

	//We have to include this because the no-argument readString method is client only
	public static final int BUFFER_STRING_LENGTH = 32767;

	protected final int entityID;

	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.entityID);
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		supplier.get().setPacketHandled(true);
	}

	protected LivingPrayerUser<?> getUser(final World world) {
		return CapabilityHandler.getPrayerCapability((LivingEntity) world.getEntity(this.entityID));
	}

	protected LivingPrayerUser<?> getUser(final NetworkEvent.Context context) {
		return this.getUser(this.getLevel(context));
	}

	protected World getLevel(final NetworkEvent.Context context) {
		if(context.getDirection().getReceptionSide() == LogicalSide.SERVER)
			return context.getSender().level;
		return ClientHelper.getLevel();
	}

	@SuppressWarnings("unchecked")
	public static <T extends PrayerUserPacketBuilder<?, ?>> T decode(final T builder, final PacketBuffer buffer){
		//Calling entityID does not return "T", but it is of type T
		return (T) builder.entityID(buffer.readInt());
	}

}
