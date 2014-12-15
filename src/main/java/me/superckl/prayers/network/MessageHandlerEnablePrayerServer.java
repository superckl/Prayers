package me.superckl.prayers.network;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.Prayers;
import me.superckl.prayers.common.utility.PrayerHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerEnablePrayerServer implements IMessageHandler<MessageEnablePrayer, MessageDisablePrayer>{

	@Override
	public MessageDisablePrayer onMessage(final MessageEnablePrayer message, final MessageContext ctx) {
		final PrayerExtendedProperties prop = ((PrayerExtendedProperties)ctx.getServerHandler().playerEntity.getExtendedProperties("prayer"));
		final List<Prayers> list = prop.getActivePrayers();
		final List<Prayers> temp = new ArrayList<Prayers>(list);
		temp.add(message.getPrayer());
		if((message.getPrayer().getLevel() > prop.getPrayerLevel()) || list.contains(message.getPrayer()) || PrayerHelper.hasConflictions(temp)){
			final MessageDisablePrayer disable = new MessageDisablePrayer();
			disable.setPrayer(message.getPrayer());
			return disable;
		}
		list.add(message.getPrayer());
		return null;
	}

}
