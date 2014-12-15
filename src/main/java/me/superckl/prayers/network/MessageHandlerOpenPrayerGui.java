package me.superckl.prayers.network;

import me.superckl.prayers.Prayers;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageHandlerOpenPrayerGui implements IMessageHandler<MessageOpenPrayerGui, IMessage>{

	@Override
	public IMessage onMessage(final MessageOpenPrayerGui message, final MessageContext ctx) {
		final EntityPlayer p = ctx.getServerHandler().playerEntity;
		p.openGui(Prayers.getInstance(), 0, p.worldObj, (int) p.posX, (int) p.posY, (int) p.posZ);
		return null;
	}

}
