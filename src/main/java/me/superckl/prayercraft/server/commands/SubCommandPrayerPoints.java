package me.superckl.prayercraft.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.utility.PlayerHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class SubCommandPrayerPoints implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("prayerpoints", "points", "prayerpoint");

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		if(args.length < 2){
			final ChatComponentTranslation chat = new ChatComponentTranslation("msg.noargs.text");
			chat.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.addChatMessage(chat);
			return;
		}
		if(!args[args.length == 2 ? 1:2].matches("[0-9]*")){
			final ChatComponentTranslation chat = new ChatComponentTranslation("msg.notint.text", 2);
			chat.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.addChatMessage(chat);
			return;
		}
		final int amount = Integer.parseInt(args[args.length == 2 ? 1:2]);
		if(args.length == 2){
			if(!(sender instanceof EntityPlayerMP)){
				sender.addChatMessage(new ChatComponentTranslation("msg.notplayer.text"));
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
			else{
				final ChatComponentTranslation chat = new ChatComponentTranslation("msg.nomatch.text", "set, add, subtract.");
				chat.getChatStyle().setColor(EnumChatFormatting.RED);
				sender.addChatMessage(chat);
			}
		}else if(args.length == 3){
			final EntityPlayerMP player = PlayerHelper.getPlayer(args[0]);
			if(player == null){
				final ChatComponentTranslation chat = new ChatComponentTranslation("msg.noplayer.text", args[0]);
				chat.getChatStyle().setColor(EnumChatFormatting.RED);
				sender.addChatMessage(chat);
				return;
			}
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			if(args[1].equalsIgnoreCase("set"))
				prop.setPrayerPoints(amount);
			else if(args[1].equalsIgnoreCase("add"))
				prop.setPrayerPoints(prop.getPrayerPoints()+amount);
			else if(args[1].equalsIgnoreCase("subtract"))
				prop.setPrayerPoints(Math.max(0F, prop.getPrayerPoints()-amount));
			else{
				final ChatComponentTranslation chat = new ChatComponentTranslation("msg.nomatch.text", "set, add, subtract.");
				chat.getChatStyle().setColor(EnumChatFormatting.RED);
				sender.addChatMessage(chat);
			}
		}else{
			final ChatComponentTranslation chat = new ChatComponentTranslation("msg.noargs.text");
			chat.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.addChatMessage(chat);
		}

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
