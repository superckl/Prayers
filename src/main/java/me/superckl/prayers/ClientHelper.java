package me.superckl.prayers;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ClientHelper {

	private static Minecraft mc = Minecraft.getInstance();

	public static World getLevel() {
		return ClientHelper.mc.level;
	}

	public static PlayerEntity getPlayer() {
		return ClientHelper.mc.player;
	}

	public static IRenderTypeBuffer.Impl getBufferSource(){
		return ClientHelper.mc.renderBuffers().bufferSource();
	}

	public static float getPartialTicks() {
		return ClientHelper.mc.getFrameTime();
	}

	public static BlockRendererDispatcher getBlockRenderer() {
		return ClientHelper.mc.getBlockRenderer();
	}

	public static ParticleManager getParticleEnginge() {
		return ClientHelper.mc.particleEngine;
	}

	public static ItemRenderer getItemRenderer() {
		return ClientHelper.mc.getItemRenderer();
	}

	public static RayTraceResult getRayTrace() {
		return ClientHelper.mc.hitResult;
	}

	public static MainWindow getWindow() {
		return ClientHelper.mc.getWindow();
	}

	public static FontRenderer getFontRenderer() {
		return ClientHelper.mc.font;
	}

	public static TextureManager getTextureManager() {
		return ClientHelper.mc.getTextureManager();
	}

	public static GameRenderer getGameRenderer() {
		return ClientHelper.mc.gameRenderer;
	}

	public static Screen getScreen() {
		return ClientHelper.mc.screen;
	}

	public static void openScreen(final Screen screen) {
		ClientHelper.mc.setScreen(screen);
	}

	public static SoundHandler getSoundManager() {
		return ClientHelper.mc.getSoundManager();
	}

}
