package me.superckl.prayers.client.input;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.client.gui.PrayerSelectGUI;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketTalismanToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyBindings {

	public static final KeyBinding OPEN_PRAYER_GUI = new KeyBinding(Util.makeDescriptionId("key", new ResourceLocation(Prayers.MOD_ID, "open_gui")), GLFW.GLFW_KEY_O, "Prayers");
	public static final KeyBinding TOGGLE_TALISMANS = new KeyBinding(Util.makeDescriptionId("key", new ResourceLocation(Prayers.MOD_ID, "toggle_talisman")), -1, "Prayers");

	private static Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void onKeyPress(final KeyInputEvent e) {
		if (KeyBindings.OPEN_PRAYER_GUI.isDown())
			KeyBindings.mc.setScreen(new PrayerSelectGUI());
		else if(KeyBindings.TOGGLE_TALISMANS.isDown() && KeyBindings.mc.player != null) {
			final List<ItemStack> stacks = KeyBindings.mc.player.inventory.items;
			for(int i = 0; i < stacks.size(); i++) {
				final ItemStack stack = stacks.get(i);
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get())
					if(ModItems.TALISMAN.get().toggle(stack, KeyBindings.mc.player))
						PrayersPacketHandler.INSTANCE.sendToServer(new PacketTalismanToggle(i));
			}
		}
	}

}
