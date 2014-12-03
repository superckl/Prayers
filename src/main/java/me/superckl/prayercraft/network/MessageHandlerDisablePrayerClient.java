package me.superckl.prayercraft.network;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerDisablePrayerClient implements IMessageHandler<MessageDisablePrayer, IMessage>{

	private final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public IMessage onMessage(final MessageDisablePrayer message, final MessageContext ctx) {
		((PrayerExtendedProperties)this.mc.thePlayer.getExtendedProperties("prayer")).getActivePrayers().remove(message.getPrayer());
		return null;
	}

}
