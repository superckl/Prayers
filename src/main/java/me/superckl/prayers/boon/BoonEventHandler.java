package me.superckl.prayers.boon;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.ArmorItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BoonEventHandler {

	@SubscribeEvent
	public void onAttributes(final ItemAttributeModifierEvent e) {
		if(!(e.getItemStack().getItem() instanceof ArmorItem) || ((ArmorItem) e.getItemStack().getItem()).getSlot() == e.getSlotType())
			ItemBoon.getBoon(e.getItemStack()).ifPresent(boon -> {
				if(ArrayUtils.contains(boon.getTypes(), e.getSlotType()))
					e.addModifier(boon.getAttributeSupplier().get(), boon.getModifierSupplier().apply(e.getSlotType()));
			});
	}

}
