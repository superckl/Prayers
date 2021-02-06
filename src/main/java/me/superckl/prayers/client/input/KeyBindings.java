package me.superckl.prayers.client.input;

import org.lwjgl.glfw.GLFW;

import me.superckl.prayers.client.gui.PrayerSelectGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyBindings {

	public static final KeyBinding OPEN_PRAYER_GUI = new KeyBinding("Open Prayer GUI", GLFW.GLFW_KEY_O, "Prayers");

	private static Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void onKeyPress(final KeyInputEvent e) {
		if (KeyBindings.OPEN_PRAYER_GUI.isPressed())
			KeyBindings.mc.displayGuiScreen(new PrayerSelectGUI());
	}

}
