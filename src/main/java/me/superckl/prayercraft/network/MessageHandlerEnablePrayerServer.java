package me.superckl.prayercraft.network;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerEnablePrayerServer implements IMessageHandler<MessageEnablePrayer, MessageDisablePrayer>{

	@Override
	public MessageDisablePrayer onMessage(final MessageEnablePrayer message, final MessageContext ctx) {
		// TODO sanity checks
		((PrayerExtendedProperties)ctx.getServerHandler().playerEntity.getExtendedProperties("prayer")).getActivePrayers().add(message.getPrayer());
		return null;
	}

}
