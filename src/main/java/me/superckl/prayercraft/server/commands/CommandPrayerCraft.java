package me.superckl.prayercraft.server.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommandPrayerCraft implements ICommand{

	public static final ISubCommand[] COMMAND_INSTANCES =new ISubCommand[] {new SubCommandPrayerPoints(), new SubCommandHelp()};

	private final List<String> aliases = Arrays.asList("prayercraft", "pc", "prayer");
	@Getter
	private final Map<String, ISubCommand> subCommands = new HashMap<String, ISubCommand>();

	public CommandPrayerCraft() {
		for(final ISubCommand command:CommandPrayerCraft.COMMAND_INSTANCES)
			for(final String alias:command.getAliases())
				this.subCommands.put(alias, command);
	}

	@Override
	public int compareTo(final Object o) {
		if(o instanceof ISubCommand)
			return 1;
		return 0;
	}

	@Override
	public String getCommandName() {
		return "PrayerCraft";
	}

	@Override
	public String getCommandUsage(final ICommandSender sender) {
		return LanguageRegistry.instance().getStringLocalization("msg.usage.text");
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		if(args.length == 0){
			sender.addChatMessage(new ChatComponentTranslation("msg.usage.text"));
			return;
		}
		final ISubCommand com = this.findCommand(args[0]);
		if(com != null)
			com.processCommand(sender, args);
	}

	@Override
	public boolean canCommandSenderUseCommand(final ICommandSender sender) {
		return sender.canCommandSenderUseCommand(4, this.getCommandName());
	}

	@Override
	public List addTabCompletionOptions(final ICommandSender sender, final String[] args) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(final String[] args, final int index) {
		if((args.length == 0) || (args.length < index) || (index == 0))
			return false;
		final ISubCommand com = this.findCommand(args[0]);
		return com == null ? false:com.isUsernameIndex(Arrays.copyOfRange(args, 1, args.length), index-1);
	}

	public ISubCommand findCommand(final String command){
		return this.subCommands.get(command.toLowerCase().trim());
	}

}
