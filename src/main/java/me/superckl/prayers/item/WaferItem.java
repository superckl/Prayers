package me.superckl.prayers.item;

import me.superckl.prayers.init.ModEffects;
import me.superckl.prayers.init.ModItems;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;

public class WaferItem extends Item{

	public static Food WAFER_FOOD = new Food.Builder().alwaysEat().fast().nutrition(1).saturationMod(0.2F)
			.effect(() -> new EffectInstance(ModEffects.SCALED_INSTANT_PRAYER.get(), 1, 0, false, false), 1).build();

	public WaferItem() {
		super(new Item.Properties().tab(ModItems.PRAYERS_GROUP).food(WaferItem.WAFER_FOOD));
	}

}
