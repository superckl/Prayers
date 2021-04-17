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
