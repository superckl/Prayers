package me.superckl.prayercraft.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.utility.PlayerHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

//@ExtensionMethod(PlayerHelper.class)
public class SubCommandPrayerLevel implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("prayerlevel", "level", "lvl");

	@Override
	public String getName() {
		return "PrayerLevel";
	}

	@Override
	public String getDescription() {
		return "Used to set a player's prayer level.";
	}

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		if(args.length < 2){
			PlayerHelper.sendTranlsatedError(sender, "msg.noargs.text");
			return;
		}
		if(!args[args.length == 2 ? 1:2].matches("^\\d+$")){
			PlayerHelper.sendTranlsatedError(sender, "msg.notint.text", 2);
			return;
		}
		final int amount = Integer.parseInt(args[args.length == 2 ? 1:2]);
		if(args.length == 2){
			if(!(sender instanceof EntityPlayerMP)){
				PlayerHelper.sendTranlsatedError(sender, "msg.notplayer.text");
				return;
			}
			final EntityPlayerMP player = (EntityPlayerMP) sender;
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			if(args[0].equalsIgnoreCase("set"))
				prop.setPrayerLevel(Math.max(1, amount));
			else if(args[0].equalsIgnoreCase("add"))
				prop.setPrayerLevel(prop.getPrayerLevel()+amount);
			else if(args[0].equalsIgnoreCase("subtract"))
				prop.setPrayerLevel(Math.max(1, prop.getPrayerLevel()-amount));
			else
				PlayerHelper.sendTranlsatedError(sender, "msg.nomatch.text", "set, add, subtract.");
		}else if(args.length == 3){
			final EntityPlayerMP player = PlayerHelper.getPlayer(args[0]);
			if(player == null){
				PlayerHelper.sendTranlsatedError(sender, "msg.noplayer.text", args[0]);
				return;
			}
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			if(args[1].equalsIgnoreCase("set"))
				prop.setPrayerLevel(Math.max(1, amount));
			else if(args[1].equalsIgnoreCase("add"))
				prop.setPrayerLevel(prop.getPrayerLevel()+amount);
			else if(args[1].equalsIgnoreCase("subtract"))
				prop.setPrayerLevel(Math.max(1, prop.getPrayerLevel()-amount));
			else
				PlayerHelper.sendTranlsatedError(sender, "msg.nomatch.text", "set, add, subtract.");
		} else
			PlayerHelper.sendTranlsatedError(sender, "msg.noargs.text");
	}

	@Override
	public boolean isUsernameIndex(final String[] args, final int index) {
		return index == 0;
	}

}
