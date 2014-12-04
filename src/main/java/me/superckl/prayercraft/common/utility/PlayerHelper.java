package me.superckl.prayercraft.common.utility;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class PlayerHelper {

	public static EntityPlayerMP getPlayer(final String username){
		for(final WorldServer wServer:MinecraftServer.getServer().worldServers)
			for(final Object player:wServer.playerEntities){
				if((player instanceof EntityPlayerMP) == false)
					continue;
				if(((EntityPlayerMP)player).getGameProfile().getName().equalsIgnoreCase(username))
					return (EntityPlayerMP) player;
			}
		return null;
	}

}
