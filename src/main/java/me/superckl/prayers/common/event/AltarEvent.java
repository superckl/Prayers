package me.superckl.prayers.common.event;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.altar.multi.BlockRequirement;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.utility.BlockLocation;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public abstract class AltarEvent extends Event{

	public abstract static class SearchForMultiblock extends AltarEvent{

		@Cancelable
		@AllArgsConstructor
		@Getter
		public static class Pre extends SearchForMultiblock{

			private final Altar altar;

		}

		@Cancelable
		@AllArgsConstructor
		@Getter
		@Setter
		public static class Post extends SearchForMultiblock{

			private final Altar altar;
			private final int tier;
			private Map<BlockLocation, BlockRequirement> multiblock;

		}

	}

	@Cancelable
	@AllArgsConstructor
	@Getter
	@Setter
	public static class EstablishBlocks extends AltarEvent{

		private final Altar altar;
		private List<BlockLocation> blocks;
		private List<TileEntityOfferingTable> tables;
		private final Map<BlockLocation, BlockRequirement> multiblock;

	}

	public abstract static class ActivationRitualEvent extends AltarEvent{

		@AllArgsConstructor
		@Getter
		public static class Pre extends ActivationRitualEvent{

			private final Altar altar;

		}

		@AllArgsConstructor
		@Getter
		public static class Post extends ActivationRitualEvent{

			private final Altar altar;

		}

		@Cancelable
		@AllArgsConstructor
		@Getter
		public static class Fail extends ActivationRitualEvent{

			private final Altar altar;

		}

	}

}
