package me.superckl.prayers.integration.curios;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.network.packet.user.PrayerUserPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

@RequiredArgsConstructor
public class CurioSlotHelper extends SlotHelper{

	private static final String CURIO_NOT_FOUND = "Error retrieving curio item type:%s, slot: %d";

	private final SlotContext context;

	@Override
	protected void serialize(final PacketBuffer buffer) {
		buffer.writeUtf(this.context.getIdentifier(), PrayerUserPacket.BUFFER_STRING_LENGTH);
		buffer.writeVarInt(this.context.getIndex());
	}

	@Override
	public Optional<ItemStack> getStack(final PlayerEntity entity) {
		try {
			return Optional.of(CuriosApi.getCuriosHelper().getCuriosHandler(entity).orElseThrow(this::buildException)
					.getStacksHandler(this.context.getIdentifier()).orElseThrow(this::buildException).getStacks()
					.getStackInSlot(this.context.getIndex()));
		} catch (final IllegalStateException e) {
			return Optional.empty();
		}
	}

	public static CurioSlotHelper deserialize(final PacketBuffer buffer) {
		return new CurioSlotHelper(new SlotContext(buffer.readUtf(PrayerUserPacket.BUFFER_STRING_LENGTH),
				null, buffer.readVarInt()));
	}

	private IllegalStateException buildException(){
		return new IllegalStateException(String.format(CurioSlotHelper.CURIO_NOT_FOUND, this.context.getIdentifier(), this.context.getIndex()));
	}

}
