package me.superckl.prayers.client.gui;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.inventory.ContainerSlotHelper;
import me.superckl.prayers.inventory.MainInventorySlotHelper;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.item.ReliquaryItem;
import me.superckl.prayers.item.TalismanItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketReliquaryState;
import me.superckl.prayers.network.packet.inventory.PacketTalismanState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiEventHandler {

	@SubscribeEvent
	public void onMouseClick(final GuiScreenEvent.MouseClickedEvent.Pre e) {
		if(e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && e.getGui() instanceof ContainerScreen<?>) {
			final ContainerScreen<?> containerS = (ContainerScreen<?>) e.getGui();
			final Optional<SlotHelper> helper = this.getHoveredSlot(containerS);
			if(helper.isPresent() && helper.get().canModify(ClientHelper.getPlayer())) {
				final ItemStack stack = helper.get().getStack(ClientHelper.getPlayer()).orElseThrow(() ->
				new IllegalStateException("Matched slot helper did not return itemstack!"));
				if(!stack.isEmpty() && stack.getItem() == ModItems.TALISMAN.get()) {
					if(ModItems.TALISMAN.get().applyState(stack, ClientHelper.getPlayer(), TalismanItem.State.TOGGLE))
						PrayersPacketHandler.INSTANCE.sendToServer(PacketTalismanState.builder().entityID(ClientHelper.getPlayer().getId())
								.slot(helper.get()).state(TalismanItem.State.TOGGLE).build());
					e.setCanceled(true);
				}else if(!stack.isEmpty() && stack.getItem() == ModItems.RELIQUARY.get()) {
					if(ReliquaryItem.applyState(stack, TalismanItem.State.TOGGLE))
						PrayersPacketHandler.INSTANCE.sendToServer(PacketReliquaryState.builder().entityID(ClientHelper.getPlayer().getId())
								.slot(helper.get()).state(TalismanItem.State.TOGGLE).build());
					e.setCanceled(true);
				}
			}
		}
	}

	private Optional<SlotHelper> getHoveredSlot(final ContainerScreen<?> container){
		final Slot slot = container.getSlotUnderMouse();
		if(slot == null)
			return Optional.empty();
		if(container instanceof CreativeScreen && slot.container == ClientHelper.getPlayer().inventory)
			return Optional.of(new MainInventorySlotHelper(slot.getSlotIndex()));
		if(slot.mayPickup(ClientHelper.getPlayer()) && slot.mayPlace(slot.getItem()))
			return Optional.of(new ContainerSlotHelper(container.getMenu().containerId, slot));
		return Optional.empty();
	}

}
