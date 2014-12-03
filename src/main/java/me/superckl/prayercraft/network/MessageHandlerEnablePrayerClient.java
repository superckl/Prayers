package me.superckl.prayercraft.network;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerEnablePrayerClient implements IMessageHandler<MessageEnablePrayer, IMessage>{

	private final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public IMessage onMessage(final MessageEnablePrayer message, final MessageContext ctx) {
		((PrayerExtendedProperties)this.mc.thePlayer.getExtendedProperties("prayer")).getActivePrayers().add(message.getPrayer());
		//TODO play effects, etc.
		return null;
	}

}
