package me.superckl.prayers.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@AllArgsConstructor
public class MessageUpdatePrayers implements IMessage{

	@Getter
	@Setter
	private NBTTagCompound prop;

	public MessageUpdatePrayers() {}

	@Override
	public void fromBytes(final ByteBuf buf) {
		final PacketBuffer packetBuf = new PacketBuffer(buf);
		try {
			this.prop = packetBuf.readNBTTagCompoundFromBuffer();
		} catch (final IOException e) {
			LogHelper.warn("Failed to deserialize prayer list!");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		final PacketBuffer packetBuf = new PacketBuffer(buf);
		try {
			packetBuf.writeNBTTagCompoundToBuffer(this.prop);
		} catch (final IOException e) {
			LogHelper.warn("Failed to serialize prayer list!");
			e.printStackTrace();
		}
	}

}
