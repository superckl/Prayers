package me.superckl.prayers.client.input;

import org.lwjgl.glfw.GLFW;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.client.gui.PrayerSelectGUI;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.inventory.PlayerInventoryHelper;
import me.superckl.prayers.inventory.SlotAwareIterator;
import me.superckl.prayers.item.ReliquaryItem;
import me.superckl.prayers.item.TalismanItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketReliquaryState;
import me.superckl.prayers.network.packet.inventory.PacketTalismanState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyBindings {

	public static final KeyBinding OPEN_PRAYER_GUI = new KeyBinding(Util.makeDescriptionId("key", new ResourceLocation(Prayers.MOD_ID, "open_gui")), GLFW.GLFW_KEY_O, "Prayers");
	public static final KeyBinding TOGGLE_TALISMANS = new KeyBinding(Util.makeDescriptionId("key", new ResourceLocation(Prayers.MOD_ID, "toggle_talisman")), -1, "Prayers");
	public static final KeyBinding TOGGLE_RELIQUARIES = new KeyBinding(Util.makeDescriptionId("key", new ResourceLocation(Prayers.MOD_ID, "toggle_reliquary")), -1, "Prayers");

	@SubscribeEvent
	public static void onKeyPress(final KeyInputEvent e) {
		if (KeyBindings.OPEN_PRAYER_GUI.isDown() && CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer()).isUnlocked())
			ClientHelper.openScreen(new PrayerSelectGUI());
		else if(KeyBindings.TOGGLE_TALISMANS.isDown() && ClientHelper.getPlayer() != null) {
			final SlotAwareIterator<?> it = PlayerInventoryHelper.allItems(ClientHelper.getPlayer());
			while(it.hasNext()) {
				final ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get())
					if(ModItems.TALISMAN.get().applyState(stack, ClientHelper.getPlayer(), TalismanItem.State.TOGGLE))
						PrayersPacketHandler.INSTANCE.sendToServer(PacketTalismanState.builder().entityID(ClientHelper.getPlayer().getId())
								.slot(it.getHelper()).state(TalismanItem.State.TOGGLE).build());
			}
		}else if(KeyBindings.TOGGLE_RELIQUARIES.isDown()) {
			final SlotAwareIterator<?> it = PlayerInventoryHelper.allItems(ClientHelper.getPlayer());
			while(it.hasNext()) {
				final ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() == ModItems.RELIQUARY.get())
					if(ReliquaryItem.applyState(stack, TalismanItem.State.TOGGLE))
						PrayersPacketHandler.INSTANCE.sendToServer(PacketReliquaryState.builder().entityID(ClientHelper.getPlayer().getId())
								.slot(it.getHelper()).state(TalismanItem.State.TOGGLE).build());
			}
		}
	}

}
