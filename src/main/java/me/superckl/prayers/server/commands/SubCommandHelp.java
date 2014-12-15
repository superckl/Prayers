package me.superckl.prayers.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

public class SubCommandHelp implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("help", "h", "?", "halpplz");

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		sender.addChatMessage(new ChatComponentText(ChatFormatting.GOLD+"Commands:"));
		for(final ISubCommand com:CommandPrayers.COMMAND_INSTANCES){
			final ChatComponentText chat = new ChatComponentText(new StringBuilder().append(com.getName()).append(": ").append(com.getDescription()).append(" Aliases: ").append(Arrays.toString(com.getAliases().toArray()).replaceAll("\\[\\]", "")).toString());
			chat.getChatStyle().setColor(EnumChatFormatting.GOLD);
			sender.addChatMessage(chat);
		}
	}

	@Override
	public boolean isUsernameIndex(final String[] args, final int index) {
		return false;
	}

	@Override
	public String getName() {
		return "Help";
	}

	@Override
	public String getDescription() {
		return "Lists all commands and what they do.";
	}

}
