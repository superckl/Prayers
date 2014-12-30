package me.superckl.prayers.common.utility;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

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

	public static void sendTileUpdateDim(final TileEntity te) {
		if((te == null) || te.getWorldObj().isRemote)
			return;
		for(final Object obj:DimensionManager.getWorld(te.getWorldObj().provider.dimensionId).playerEntities){
			if((obj instanceof EntityPlayerMP) == false)
				return;
			final EntityPlayerMP player = (EntityPlayerMP) obj;
			player.playerNetServerHandler.sendPacket(te.getDescriptionPacket());
			//I could make a long process to only send it to players with this chunk loaded, but I'm not going to.
		}
	}

}
