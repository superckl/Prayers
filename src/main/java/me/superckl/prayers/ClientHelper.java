package me.superckl.prayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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

	public static IRenderTypeBuffer.Impl getBufferSource(){
		return mc.renderBuffers().bufferSource();
	}
	
	public static float getPartialTicks() {
		return mc.getFrameTime();
	}
	
}
