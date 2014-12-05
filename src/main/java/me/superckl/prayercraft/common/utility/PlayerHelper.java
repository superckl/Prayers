package me.superckl.prayercraft.common.utility;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;

public class PlayerHelper {

	public static EntityPlayerMP getPlayer(final String username){
		for(final WorldServer wServer:MinecraftServer.getServer().worldServers)
			for(final Object player:wServer.playerEntities){
				if((player instanceof EntityPlayerMP) == false)
					continue;
				if(((EntityPlayerMP)player).getGameProfile().getName().equalsIgnoreCase(username))
					return (EntityPlayerMP) player;
			}
		return null;
	}

	public static void sendTranlsatedError(final ICommandSender sender, final String key, final Object ... args){
		final ChatComponentTranslation chat = new ChatComponentTranslation(key, args);
		chat.getChatStyle().setColor(EnumChatFormatting.RED);
		sender.addChatMessage(chat);
	}

	public static void sendTranlsatedConfirmation(final ICommandSender sender, final String key, final Object ... args){
		final ChatComponentTranslation chat = new ChatComponentTranslation(key, args);
		chat.getChatStyle().setColor(EnumChatFormatting.GOLD);
		sender.addChatMessage(chat);
	}

}
