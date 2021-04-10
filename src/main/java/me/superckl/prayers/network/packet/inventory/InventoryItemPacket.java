package me.superckl.prayers.network.packet.inventory;

import java.util.Optional;

import lombok.experimental.SuperBuilder;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

@SuperBuilder
public class InventoryItemPacket extends PrayerUserPacket{

	protected final SlotHelper slot;

	@Override
	public void encode(final PacketBuffer buffer) {
		super.encode(buffer);
		this.slot.writeToBuffer(buffer);
	}

	public Optional<ItemStack> getStack(final NetworkEvent.Context context) {
		final Entity entity = this.getLevel(context).getEntity(this.entityID);
		if(entity instanceof PlayerEntity)
			return this.slot.getStack((PlayerEntity) entity);
		throw new IllegalStateException("Received inventory packet for non-player entity! "+entity);
	}

	@SuppressWarnings("unchecked")
	public static <T extends InventoryItemPacketBuilder<?, ?>> T decode(final T builder, final PacketBuffer buffer){
		//Calling entityID does not return "T", but it is of type T
		return (T) PrayerUserPacket.decode(builder, buffer).slot(SlotHelper.deserialize(buffer));
	}

}
