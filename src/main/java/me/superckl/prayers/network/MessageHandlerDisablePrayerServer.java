package me.superckl.prayers.network;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerDisablePrayerServer implements IMessageHandler<MessageDisablePrayer, MessageEnablePrayer>{

	@Override
	public MessageEnablePrayer onMessage(final MessageDisablePrayer message, final MessageContext ctx) {
		// TODO sanity checks
		((PrayerExtendedProperties)ctx.getServerHandler().playerEntity.getExtendedProperties("prayer")).getActivePrayers().remove(message.getPrayer());
		return null;
	}

}
