package me.superckl.prayers.client.gui;

import org.lwjgl.glfw.GLFW;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketTalismanToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiEventHandler {

	@SuppressWarnings("resource")
	@SubscribeEvent
	public void onMouseClick(final GuiScreenEvent.MouseClickedEvent.Pre e) {
		if(e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && e.getGui() instanceof ContainerScreen<?>) {
			final ContainerScreen<?> containerS = (ContainerScreen<?>) e.getGui();
			final Slot slot = containerS.getSlotUnderMouse();
			if(slot != null && slot.container == Minecraft.getInstance().player.inventory) {
				final ItemStack stack = containerS.getSlotUnderMouse().getItem();
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get()) {
					if(ModItems.TALISMAN.get().toggle(stack, Minecraft.getInstance().player))
						PrayersPacketHandler.INSTANCE.sendToServer(new PacketTalismanToggle(slot.getSlotIndex()));
					e.setCanceled(true);
				}
			}
		}
	}

}
