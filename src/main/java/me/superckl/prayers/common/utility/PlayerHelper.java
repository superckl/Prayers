package me.superckl.prayers.common.utility;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

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

	public static EntityPlayerMP getPlayer(final UUID uuid){
		for(final WorldServer wServer:MinecraftServer.getServer().worldServers)
			for(final Object player:wServer.playerEntities){
				if((player instanceof EntityPlayerMP) == false)
					continue;
				if(((EntityPlayerMP)player).getGameProfile().getId().equals(uuid))
					return (EntityPlayerMP) player;
			}
		return null;
	}

	public static EntityLivingBase getShooter(final DamageSource source){
		if((source.getSourceOfDamage() != null) && (source.getSourceOfDamage() != source.getEntity()) && (source.getSourceOfDamage() instanceof EntityLivingBase))
			return (EntityLivingBase) source.getSourceOfDamage();
		final Entity ent = source.getEntity();
		if(ent instanceof EntityThrowable)
			return ((EntityThrowable)ent).getThrower();
		try{
			final Field field = ReflectionHelper.findField(ent.getClass(), ObfuscationReflectionHelper.remapFieldNames(ent.getClass().getName(), "shootingEntity", "field_70235_a", "field_70250_c"));
			if(field == null)
				return null;
			field.setAccessible(true);
			if(!EntityLivingBase.class.isAssignableFrom(field.getType()))
				return null;
			return EntityLivingBase.class.cast(field.get(ent));
		}catch(final Exception e){
			//No existo
		}
		return null;
	}

	public static void sendTileUpdateDim(final TileEntity te){
		if(te == null)
			return;
		te.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
	}

}
