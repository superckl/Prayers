package me.superckl.prayers.network.packet.inventory;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.TalismanItem;
import me.superckl.prayers.item.TalismanItem.State;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

@SuperBuilder
public class PacketTalismanState extends InventoryItemStatePacket<TalismanItem>{

	//	@Override
	//	public void handle(final Supplier<NetworkEvent.Context> supplier) {
	//		supplier.get().enqueueWork(() -> {
	//			final Entity entity = this.getLevel(supplier.get()).getEntity(this.entityID);
	//			if(entity instanceof PlayerEntity) {
	//				final PlayerEntity player = (PlayerEntity) entity;
	//				final ItemStack stack = this.getStack(supplier.get());
	//				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get()) {
	//					//Since this is from a client, defensively check this can actually be done
	//					final boolean changed = ModItems.TALISMAN.get().applyState(stack, player, this.state);
	//					if(changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
	//						//Notify tracking clients that this player has updated their prayers
	//						PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), this);
	//					else if(!changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
	//						//Notify the client (sender) that nothing actually changed by sending them the opposite state
	//						PrayersPacketHandler.INSTANCE.reply(PacketTalismanState.builder().entityID(this.entityID)
	//								.slot(this.slot).state(this.state.opposite()).build(), supplier.get());
	//				}
	//			}
	//		});
	//		supplier.get().setPacketHandled(true);
	//	}

	public static PacketTalismanState decode(final PacketBuffer buffer) {
		return InventoryItemStatePacket.decode(PacketTalismanState.builder(), buffer).build();
	}

	@Override
	public InventoryItemStatePacket<TalismanItem> opposite() {
		return PacketTalismanState.builder().entityID(this.entityID)
				.slot(this.slot).state(this.state.opposite()).build();
	}

	@Override
	public TalismanItem getItem() {
		return ModItems.TALISMAN.get();
	}

	@Override
	public boolean applyState(final ItemStack stack, final State state, final PlayerEntity player) {
		return ModItems.TALISMAN.get().applyState(stack, player, this.state);
	}

}
