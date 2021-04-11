package me.superckl.prayers.inventory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.Maps;

import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/*
 * This class is an abstraction layer over player inventories to ease interfacing
 * with aspects of a player's inventory that may not be present in vanilla (e.g., curios)
 */
public abstract class SlotHelper {

	private static final Map<Class<? extends SlotHelper>, String> classes = Maps.newConcurrentMap();
	private static final Map<String, Function<PacketBuffer, ? extends SlotHelper>> deserializers = Maps.newConcurrentMap();

	public abstract Optional<ItemStack> getStack(PlayerEntity player);
	public abstract boolean canModify(PlayerEntity player);

	public final void writeToBuffer(final PacketBuffer buffer) {
		buffer.writeUtf(SlotHelper.classes.get(this.getClass()), PrayerUserPacket.BUFFER_STRING_LENGTH);
		this.serialize(buffer);
	}

	protected void serialize(final PacketBuffer buffer) {}

	public static SlotHelper deserialize(final PacketBuffer buffer) {
		return SlotHelper.deserializers.get(buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH)).apply(buffer);
	}

	public static <T extends SlotHelper> void registerHelper(final String id, final Class<T> clazz,
			final Function<PacketBuffer, T> deserializer) {
		if(SlotHelper.deserializers.containsKey(id))
			throw new IllegalStateException("Slot helper "+id+" has already been registered!");
		SlotHelper.classes.put(clazz, id);
		SlotHelper.deserializers.put(id, deserializer);
	}

}
