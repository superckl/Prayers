package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.TalismanItem.State;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

@SuperBuilder
public class PacketTalismanState extends PrayerUserPacket{

	private final int slot;
	private final State state;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeVarInt(this.slot);
		buffer.writeEnum(this.state);
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			final Entity entity = this.getLevel(supplier.get()).getEntity(this.entityID);
			if(entity instanceof PlayerEntity) {
				final PlayerEntity player = (PlayerEntity) entity;
				final ItemStack stack = player.inventory.getItem(this.slot);
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get()) {
					//Since this is from a client, defensively check this can actually be done
					final boolean changed = ModItems.TALISMAN.get().applyState(stack, player, this.state);
					if(changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
						//Notify tracking clients that this player has updated their prayers
						PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), this);
					else if(!changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
						//Notify the client (sender) that nothing actually changed by sending them the opposite state
						PrayersPacketHandler.INSTANCE.reply(PacketTalismanState.builder().entityID(this.entityID)
								.slot(this.slot).state(this.state.opposite()).build(), supplier.get());
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}

	public static PacketTalismanState decode(final PacketBuffer buffer) {
		return PrayerUserPacket.decode(PacketTalismanState.builder(), buffer).slot(buffer.readVarInt())
				.state(buffer.readEnum(State.class)).build();
	}

}
