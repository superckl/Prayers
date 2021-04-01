package me.superckl.prayers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientHelper {

	private static Minecraft mc = Minecraft.getInstance();

	public static World getClientLevel() {
		return ClientHelper.mc.level;
	}

	public static PlayerEntity getPlayer() {
		return ClientHelper.mc.player;
	}

}
