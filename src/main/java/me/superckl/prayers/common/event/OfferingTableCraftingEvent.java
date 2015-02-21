package me.superckl.prayers.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.superckl.prayers.common.altar.crafting.TableCraftingHandler;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Getter
@RequiredArgsConstructor
public abstract class OfferingTableCraftingEvent extends Event{

	private final TileEntityOfferingTable offeringTable;
	private final TableCraftingHandler handler;

	@Cancelable
	public static class Pre extends OfferingTableCraftingEvent{

		public Pre(final TileEntityOfferingTable offeringTable, final TableCraftingHandler handler) {
			super(offeringTable, handler);
			// TODO Auto-generated constructor stub
		}

	}

	@Cancelable
	@Getter
	@Setter
	public static class Post extends OfferingTableCraftingEvent{

		//private ItemStack craftingResult; TODO

		public Post(final TileEntityOfferingTable offeringTable, final TableCraftingHandler handler) {
			super(offeringTable, handler);
			//this.craftingResult = handler.getResult().copy();
		}

	}

}
