package me.superckl.prayers.network;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerUpdatePrayers implements IMessageHandler<MessageUpdatePrayers, IMessage>{

	private final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public IMessage onMessage(final MessageUpdatePrayers message, final MessageContext ctx) {
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) this.mc.thePlayer.getExtendedProperties("prayer");
		prop.setActivePrayers(message.getPrayers());
		return null;
	}

}
