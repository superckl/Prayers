package me.superckl.prayers.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.prayer.Prayers;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PrayerHelper;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@AllArgsConstructor
public class MessageUpdatePrayers implements IMessage{

	@Getter
	@Setter
	private List<Prayers> prayers;

	public MessageUpdatePrayers() {}

	@Override
	public void fromBytes(final ByteBuf buf) {
		final PacketBuffer packetBuf = new PacketBuffer(buf);
		try {
			this.prayers = PrayerHelper.fromNBT(packetBuf.readNBTTagCompoundFromBuffer());
		} catch (final IOException e) {
			LogHelper.warn("Failed to deserialize prayer list!");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		final PacketBuffer packetBuf = new PacketBuffer(buf);
		try {
			packetBuf.writeNBTTagCompoundToBuffer(PrayerHelper.toNBT(this.prayers));
		} catch (final IOException e) {
			LogHelper.warn("Failed to serialize prayer list!");
			e.printStackTrace();
		}
	}

}
