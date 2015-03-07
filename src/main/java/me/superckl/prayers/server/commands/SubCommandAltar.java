package me.superckl.prayers.server.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.api.AltarRegistry;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.utility.ChatHelper;
import me.superckl.prayers.common.utility.PlayerHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

@ExtensionMethod(ChatHelper.class)
public class SubCommandAltar implements ISubCommand{

	@Getter
	private final List<String> aliases = Arrays.asList("altar", "alt", "a");

	@Override
	public String getName() {
		return "Altar";
	}

	@Override
	public String getDescription() {
		return "Used to bypass altar activation rituals or to deactivate altars.";
	}

	@Override
	public void processCommand(final ICommandSender sender, final String[] args) {
		if(args.length < 1){
			sender.sendTranlsatedError("msg.noargs.text");
			return;
		}
		boolean activate = false;
		if(args[0].equalsIgnoreCase("activate"))
			activate = true;
		else if(!args[0].equalsIgnoreCase("deactivate")){
			sender.sendTranlsatedError("msg.nomatch.text", "activate, deactivate.");
			return;
		}
		if(args.length == 1){
			if((sender instanceof EntityPlayerMP) == false){
				sender.sendTranlsatedError("msg.notplayer.text");
				return;
			}
			final EntityPlayerMP player = (EntityPlayerMP) sender;
			final Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY+player.eyeHeight, player.posZ);
			final Vec3 vec31 = player.getLook(1F);
			final Vec3 vec32 = vec3.addVector(vec31.xCoord * 20D, vec31.yCoord * 20D, vec31.zCoord * 20D);
			final MovingObjectPosition pos = player.worldObj.rayTraceBlocks(vec3, vec32);
			if(pos.typeOfHit != MovingObjectType.BLOCK){
				sender.sendTranlsatedError("msg.noaltarinrange.text");
				return;
			}
			final Altar altar = AltarRegistry.findAltarAt(player.worldObj, pos.blockX, pos.blockY, pos.blockZ);
			if(altar == null){
				sender.sendTranlsatedError("msg.notaltar.text");
				return;
			}
			altar.setActivated(activate);
			if(activate)
				Prayers.getInstance().getConfig().setStats(altar);
			PlayerHelper.sendTileUpdateDim(altar.getHolder());
			sender.sendTranlsatedConfirmation(String.format("msg.altar%s.text", activate ? "activate":"deactivate"));
		}else if(args.length == 4){
			if((sender instanceof EntityPlayer) == false){
				sender.sendTranlsatedError("msg.notplayer.text");
				return;
			}
			if(!args[1].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 2);
				return;
			}
			if(!args[2].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 3);
				return;
			}
			if(!args[3].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 4);
				return;
			}
			final int x = Integer.parseInt(args[1]);
			final int y = Integer.parseInt(args[2]);
			final int z = Integer.parseInt(args[3]);

			final Altar altar = AltarRegistry.findAltarAt(((EntityPlayer) sender).worldObj, x, y, z);
			if(altar == null){
				sender.sendTranlsatedError("msg.notaltar.text");
				return;
			}
			altar.setActivated(activate);
			if(activate)
				Prayers.getInstance().getConfig().setStats(altar);
			PlayerHelper.sendTileUpdateDim(altar.getHolder());
			sender.sendTranlsatedConfirmation(String.format("msg.altar%s.text", activate ? "activate":"deactivate"));
		}else if(args.length == 5){
			if(!args[1].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 2);
				return;
			}
			if(!args[2].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 3);
				return;
			}
			if(!args[3].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 4);
				return;
			}
			if(!args[4].matches("^(\\+|-)?\\d+$")){
				ChatHelper.sendTranlsatedError(sender, "msg.notint.text", 5);
				return;
			}
			final int dim = Integer.parseInt(args[1]);
			if(!DimensionManager.isDimensionRegistered(dim)){
				ChatHelper.sendTranlsatedError(sender, "msg.nodim.text", dim);
				return;
			}
			final World world = DimensionManager.getWorld(dim);
			final int x = Integer.parseInt(args[1]);
			final int y = Integer.parseInt(args[2]);
			final int z = Integer.parseInt(args[3]);

			final Altar altar = AltarRegistry.findAltarAt(world, x, y, z);
			if(altar == null){
				sender.sendTranlsatedError("msg.notaltar.text");
				return;
			}
			altar.setActivated(activate);
			if(activate)
				Prayers.getInstance().getConfig().setStats(altar);
			PlayerHelper.sendTileUpdateDim(altar.getHolder());
			sender.sendTranlsatedConfirmation(String.format("msg.altar%s.text", activate ? "activate":"deactivate"));
		}else
			sender.sendTranlsatedError("msg.noargs.text");

	}

	@Override
	public boolean isUsernameIndex(final String[] args, final int index) {
		return false;
	}

}
