package me.superckl.prayers.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.utility.ChatHelper;
import me.superckl.prayers.common.utility.PlayerHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

//@ExtensionMethod(PlayerHelper.class)
public class SubCommandMaxPrayerPoints implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("maxprayerpoints", "maxpoints", "maxps");

	@Override
	public String getName() {
		return "MaxPrayerPoints";
	}

	@Override
	public String getDescription() {
		return "Used to set a player's prayer level.";
	}

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		if(args.length < 2){
			ChatHelper.sendTranlsatedError(sender, "msg.noargs.text");
			return;
		}
		if(!args[args.length == 2 ? 1:2].matches("^\\d+$")){
			ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 2);
			return;
		}
		final float amount = Integer.parseInt(args[args.length == 2 ? 1:2]);
		if(args.length == 2){
			if(!(sender instanceof EntityPlayerMP)){
				ChatHelper.sendTranlsatedError(sender, "msg.notplayer.text");
				return;
			}
			final EntityPlayerMP player = (EntityPlayerMP) sender;
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			if(args[0].equalsIgnoreCase("set"))
				prop.setBaseMaxPrayerPoints(Math.max(1F, amount));
			else if(args[0].equalsIgnoreCase("add"))
				prop.setBaseMaxPrayerPoints(prop.getBaseMaxPrayerPoints()+amount);
			else if(args[0].equalsIgnoreCase("subtract"))
				prop.setBaseMaxPrayerPoints(Math.max(1F, prop.getBaseMaxPrayerPoints()-amount));
			else
				ChatHelper.sendTranlsatedError(sender, "msg.nomatch.text", "set, add, subtract.");
		}else if(args.length == 3){
			final EntityPlayerMP player = PlayerHelper.getPlayer(args[0]);
			if(player == null){
				ChatHelper.sendTranlsatedError(sender, "msg.noplayer.text", args[0]);
				return;
			}
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			if(args[1].equalsIgnoreCase("set"))
				prop.setBaseMaxPrayerPoints(Math.max(1F, amount));
			else if(args[1].equalsIgnoreCase("add"))
				prop.setBaseMaxPrayerPoints(prop.getBaseMaxPrayerPoints()+amount);
			else if(args[1].equalsIgnoreCase("subtract"))
				prop.setBaseMaxPrayerPoints(Math.max(1F, prop.getBaseMaxPrayerPoints()-amount));
			else
				ChatHelper.sendTranlsatedError(sender, "msg.nomatch.text", "set, add, subtract.");
		} else
			ChatHelper.sendTranlsatedError(sender, "msg.noargs.text");
	}

	@Override
	public boolean isUsernameIndex(final String[] args, final int index) {
		return index == 0;
	}

}
