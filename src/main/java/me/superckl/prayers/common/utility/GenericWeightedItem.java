package me.superckl.prayers.common.utility;

import lombok.Getter;
import net.minecraft.util.WeightedRandom;

public class GenericWeightedItem<T> extends WeightedRandom.Item{

	@Getter
	private final T item;

	public GenericWeightedItem(final int weight, final T item) {
		super(weight);
		this.item = item;
	}

}
