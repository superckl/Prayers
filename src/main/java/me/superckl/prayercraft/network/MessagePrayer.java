package me.superckl.prayercraft.network;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayercraft.common.prayer.Prayers;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@AllArgsConstructor
public abstract class MessagePrayer implements IMessage{

	@Getter
	@Setter
	private Prayers prayer;

	public MessagePrayer() {}

	@Override
	public void fromBytes(final ByteBuf buf) {
		this.prayer = Prayers.getById(buf.readBytes(buf.readInt()).toString(Charset.defaultCharset()));
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		final byte[] bytes = this.prayer.getId().getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}

}
