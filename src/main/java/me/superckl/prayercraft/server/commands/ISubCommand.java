package me.superckl.prayercraft.server.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;

public interface ISubCommand {

	public List<String> getAliases();
	public String getName();
	public String getDescription();
	public void processCommand(final ICommandSender sender, final String[] args);
	public boolean isUsernameIndex(final String[] args, final int index);

}
