package me.superckl.prayercraft.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.utility.PlayerHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

//@ExtensionMethod(PlayerHelper.class)
public class SubCommandPrayerPoints implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("prayerpoints", "points", "prayerpoint");

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		if(args.length < 2){
			PlayerHelper.sendTranlsatedError(sender, "msg.noargs.text");
			return;
		}
		if(!args[args.length == 2 ? 1:2].matches("^\\d+$")){
			PlayerHelper.sendTranlsatedError(sender, "msg.notposint.text", 2);
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
				prop.setPrayerPoints(amount);
			else if(args[0].equalsIgnoreCase("add"))
				prop.setPrayerPoints(prop.getPrayerPoints()+amount);
			else if(args[0].equalsIgnoreCase("subtract"))
				prop.setPrayerPoints(Math.max(0F, prop.getPrayerPoints()-amount));
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
				prop.setPrayerPoints(amount);
			else if(args[1].equalsIgnoreCase("add"))
				prop.setPrayerPoints(prop.getPrayerPoints()+amount);
			else if(args[1].equalsIgnoreCase("subtract"))
				prop.setPrayerPoints(Math.max(0F, prop.getPrayerPoints()-amount));
			else
				PlayerHelper.sendTranlsatedError(sender, "msg.nomatch.text", "set, add, subtract.");
		} else
			PlayerHelper.sendTranlsatedError(sender, "msg.noargs.text");

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
