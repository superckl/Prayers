package me.superckl.prayercraft.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import net.minecraft.command.ICommandSender;

public class SubCommandPrayerPoints implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("prayerpoints", "points", "prayerpoint");

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUsernameIndex(final String[] args, final int index) {
		return index == 0;
	}

	@Override
	public String getName() {
		return "PrayerPoints";
	}

	@Override
	public String getDescription() {
		return "Used to modify a player's prayer points.";
	}

}
