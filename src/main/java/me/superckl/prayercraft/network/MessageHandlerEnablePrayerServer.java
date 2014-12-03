package me.superckl.prayercraft.network;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.Prayers;
import me.superckl.prayercraft.common.utility.PrayerHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerEnablePrayerServer implements IMessageHandler<MessageEnablePrayer, MessageDisablePrayer>{

	@Override
	public MessageDisablePrayer onMessage(final MessageEnablePrayer message, final MessageContext ctx) {
		final List<Prayers> list = ((PrayerExtendedProperties)ctx.getServerHandler().playerEntity.getExtendedProperties("prayer")).getActivePrayers();
		final List<Prayers> temp = new ArrayList<Prayers>(list);
		temp.add(message.getPrayer());
		if(PrayerHelper.hasConflictions(temp)){
			final MessageDisablePrayer disable = new MessageDisablePrayer();
			disable.setPrayer(message.getPrayer());
			return disable;
		}
		list.add(message.getPrayer());
		return null;
	}

}
