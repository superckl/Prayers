package me.superckl.prayers.network.packet.inventory;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.item.TalismanItem.State;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

@SuperBuilder
public abstract class InventoryItemStatePacket<T extends Item> extends InventoryItemPacket{

	protected final State state;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		buffer.writeEnum(this.state);
	}

	@Override
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			final Entity entity = this.getLevel(supplier.get()).getEntity(this.entityID);
			if(entity instanceof PlayerEntity) {
				final PlayerEntity player = (PlayerEntity) entity;
				final Optional<ItemStack> optStack = this.getStack(supplier.get()).filter(stack -> !stack.isEmpty() && stack.getItem() == this.getItem());
				if((!optStack.isPresent() || !this.slot.canModify(player)) && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
					//Notify the client (sender) that nothing actually changed by sending them the opposite state
					PrayersPacketHandler.INSTANCE.reply(this.opposite(), supplier.get());
				else if(optStack.isPresent()){
					final ItemStack stack = optStack.get();
					//Since this could be from a client, defensively check this can actually be done
					final boolean changed = this.applyState(stack, this.state, player);
					if(changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
						//Notify tracking clients that this player has updated their reliquary
						PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), this);
					else if(!changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
						//Notify the client (sender) that nothing actually changed by sending them the opposite state
						PrayersPacketHandler.INSTANCE.reply(this.opposite(), supplier.get());
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}

	public abstract InventoryItemStatePacket<T> opposite();
	public abstract T getItem();
	public abstract boolean applyState(ItemStack stack, State state, PlayerEntity player);

	@SuppressWarnings("unchecked")
	public static <T extends InventoryItemStatePacketBuilder<?, ?, ?>> T decode(final T builder, final PacketBuffer buffer){
		//Calling state does not return "T", but it is of type T
		return (T) InventoryItemPacket.decode(builder, buffer).state(buffer.readEnum(State.class));
	}

}
