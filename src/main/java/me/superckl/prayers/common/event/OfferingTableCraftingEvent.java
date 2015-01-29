package me.superckl.prayers.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.superckl.prayers.common.altar.crafting.OfferingTableCraftingHandler;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Getter
@RequiredArgsConstructor
public abstract class OfferingTableCraftingEvent extends Event{

	private final TileEntityOfferingTable offeringTable;
	private final OfferingTableCraftingHandler handler;

	@Cancelable
	public static class Pre extends OfferingTableCraftingEvent{

		public Pre(final TileEntityOfferingTable offeringTable, final OfferingTableCraftingHandler handler) {
			super(offeringTable, handler);
			// TODO Auto-generated constructor stub
		}

	}

	@Cancelable
	@Getter
	@Setter
	public static class Post extends OfferingTableCraftingEvent{

		private ItemStack craftingResult;

		public Post(final TileEntityOfferingTable offeringTable, final OfferingTableCraftingHandler handler) {
			super(offeringTable, handler);
			this.craftingResult = handler.getResult().copy();
		}

	}

}
