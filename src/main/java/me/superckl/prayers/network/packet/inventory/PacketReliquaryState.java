package me.superckl.prayers.network.packet.inventory;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.ReliquaryItem;
import me.superckl.prayers.item.TalismanItem.State;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

@SuperBuilder
public class PacketReliquaryState extends InventoryItemStatePacket<ReliquaryItem>{

	//	@Override
	//	public void handle(final Supplier<NetworkEvent.Context> supplier) {
	//		supplier.get().enqueueWork(() -> {
	//			final Entity entity = this.getLevel(supplier.get()).getEntity(this.entityID);
	//			if(entity instanceof PlayerEntity) {
	//				final PlayerEntity player = (PlayerEntity) entity;
	//				final Optional<ItemStack> optStack = this.getStack(supplier.get());
	//				if(!optStack.isPresent() && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
	//					//Notify the client (sender) that nothing actually changed by sending them the opposite state
	//					PrayersPacketHandler.INSTANCE.reply(this.opposite(), supplier.get());
	//				}else if(optStack.isPresent()){
	//					ItemStack stack = optStack.get();
	//					if(!stack.isEmpty() && stack.getItem() == ModItems.RELIQUARY.get()) {
	//						ModItems.RELIQUARY.get();
	//						//Since this is from a client, defensively check this can actually be done
	//						final boolean changed = ReliquaryItem.applyState(stack, this.state);
	//						if(changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
	//							//Notify tracking clients that this player has updated their reliquary
	//							PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), this);
	//						else if(!changed && supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
	//							//Notify the client (sender) that nothing actually changed by sending them the opposite state
	//							PrayersPacketHandler.INSTANCE.reply(this.opposite(), supplier.get());
	//					}
	//				}
	//			}
	//		});
	//		supplier.get().setPacketHandled(true);
	//	}

	public static PacketReliquaryState decode(final PacketBuffer buffer) {
		return InventoryItemStatePacket.decode(PacketReliquaryState.builder(), buffer).build();
	}

	@Override
	public PacketReliquaryState opposite() {
		return PacketReliquaryState.builder().entityID(this.entityID).slot(this.slot)
				.state(this.state.opposite()).build();
	}

	@Override
	public ReliquaryItem getItem() {
		return ModItems.RELIQUARY.get();
	}

	@Override
	public boolean applyState(final ItemStack stack, final State state, final PlayerEntity entity) {
		return ReliquaryItem.applyState(stack, this.state);
	}

}
