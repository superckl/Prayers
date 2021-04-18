package me.superckl.prayers.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.init.ModItems;
import net.minecraft.item.Item;

public class BonesItem extends Item{

	@Getter
	protected final Type type;

	public BonesItem(final Type type) {
		super(new Item.Properties().tab(ModItems.PRAYERS_GROUP));
		this.type = type;
	}

	@RequiredArgsConstructor
	@Getter
	public enum Type{

		SMALL(0),
		LARGE(1),
		ANCIENT(2);

		private final int effectAmplifier;

	}

}
