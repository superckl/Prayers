package me.superckl.prayers.inventory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.common.collect.Maps;

import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/*
 * This class is an abstraction layer over player inventories to ease interfacing
 * with aspects of a player's inventory that may not be present in vanilla (e.g., curios)
 */
public abstract class SlotHelper {

	private static final Map<Class<? extends SlotHelper>, String> classes = Maps.newConcurrentMap();
	private static final Map<String, Function<PacketBuffer, ? extends SlotHelper>> deserializers = Maps.newConcurrentMap();
	private static final Map<String, BiPredicate<PlayerEntity, Slot>> predicates = Maps.newConcurrentMap();
	private static final Map<String, Function<Slot, ? extends SlotHelper>> factories = Maps.newConcurrentMap();

	public abstract ItemStack getStack(PlayerEntity entity);

	public final void writeToBuffer(final PacketBuffer buffer) {
		buffer.writeUtf(SlotHelper.classes.get(this.getClass()), PrayerUserPacket.BUFFER_STRING_LENGTH);
		this.serialize(buffer);
	}

	protected void serialize(final PacketBuffer buffer) {}

	public static SlotHelper deserialize(final PacketBuffer buffer) {
		return SlotHelper.deserializers.get(buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH)).apply(buffer);
	}

	public static <T extends SlotHelper> void registerHelper(final String id, final Class<T> clazz, final Function<PacketBuffer, T> deserializer,
			final BiPredicate<PlayerEntity, Slot> predicate, final Function<Slot, T> factory) {
		if(SlotHelper.deserializers.containsKey(id))
			throw new IllegalStateException("Slot helper "+id+" has already been registered!");
		SlotHelper.classes.put(clazz, id);
		SlotHelper.deserializers.put(id, deserializer);
		SlotHelper.predicates.put(id, predicate);
		SlotHelper.factories.put(id, factory);
	}

	public static Optional<SlotHelper> fromSlot(final PlayerEntity player, final Slot slot) {
		for(final Entry<String, BiPredicate<PlayerEntity, Slot>> pair:SlotHelper.predicates.entrySet())
			if(pair.getValue().test(player, slot))
				return Optional.of(SlotHelper.factories.get(pair.getKey()).apply(slot));
		return Optional.empty();
	}

	public static SlotHelper fromBuffer(final PacketBuffer buffer) {
		return SlotHelper.deserializers.get(buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH)).apply(buffer);
	}

}
