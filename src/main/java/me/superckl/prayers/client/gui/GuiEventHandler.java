package me.superckl.prayers.client.gui;

import org.lwjgl.glfw.GLFW;

import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.ReliquaryItem;
import me.superckl.prayers.item.TalismanItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketReliquaryState;
import me.superckl.prayers.network.packet.inventory.PacketTalismanState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiEventHandler {

	@SubscribeEvent
	public void onMouseClick(final GuiScreenEvent.MouseClickedEvent.Pre e) {
		if(e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && e.getGui() instanceof ContainerScreen<?>) {
			final ContainerScreen<?> containerS = (ContainerScreen<?>) e.getGui();
			final Slot slot = containerS.getSlotUnderMouse();
			if(slot != null && slot.container == ClientHelper.getPlayer().inventory) {
				final ItemStack stack = containerS.getSlotUnderMouse().getItem();
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get()) {
					if(ModItems.TALISMAN.get().applyState(stack, ClientHelper.getPlayer(), TalismanItem.State.TOGGLE))
						PrayersPacketHandler.INSTANCE.sendToServer(PacketTalismanState.builder().entityID(ClientHelper.getPlayer().getId())
								.slot(slot.getSlotIndex()).state(TalismanItem.State.TOGGLE).build());
					e.setCanceled(true);
				}else if(!stack.isEmpty() && stack.getItem() == ModItems.RELIQUARY.get()) {
					if(ReliquaryItem.applyState(stack, TalismanItem.State.TOGGLE))
						PrayersPacketHandler.INSTANCE.sendToServer(PacketReliquaryState.builder().entityID(ClientHelper.getPlayer().getId())
								.slot(slot.getSlotIndex()).state(TalismanItem.State.TOGGLE).build());
					e.setCanceled(true);
				}
			}
		}
	}

}
