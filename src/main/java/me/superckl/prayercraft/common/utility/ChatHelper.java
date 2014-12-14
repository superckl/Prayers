package me.superckl.prayercraft.common.utility;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatHelper {

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

	public static void sendFormattedMessage(final ICommandSender receiver, final String key, final ChatStyle style, final Object ... args){
		final ChatComponentTranslation chat = new ChatComponentTranslation(key, args);
		chat.setChatStyle(style);
		receiver.addChatMessage(chat);
	}

	public static void sendFormattedDoubleMessage(final ICommandSender receiver, final String key1, final IChatComponent child, final ChatStyle style, final Object ... args){
		final ChatComponentTranslation chat = new ChatComponentTranslation(key1, args);
		chat.setChatStyle(style);
		chat.appendSibling(child);
		receiver.addChatMessage(chat);
	}

	public static ChatComponentTranslation createTranslatedChat(final String key, final Object ... args){
		return new ChatComponentTranslation(key, args);
	}

	public static ChatComponentTranslation createTranslatedChatWithStyle(final String key, final ChatStyle style, final Object ... args){
		final ChatComponentTranslation chat = new ChatComponentTranslation(key, args);
		chat.setChatStyle(style);
		return chat;
	}

}
