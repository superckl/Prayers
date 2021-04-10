package me.superckl.prayers.inventory;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.integration.curios.CuriosIntegration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerInventoryHelper {

	public static SlotAwareIterator<?> allItems(final PlayerEntity player){
		final List<SlotAwareIterator<?>> its = Lists.<SlotAwareIterator<?>>newArrayList(new PlayerInventoryIterator(player));
		if(Prayers.hasCurios)
			CuriosIntegration.getCurios(player).ifPresent(its::add);
		return new SlotAwareIterator.Combined(its.toArray(new SlotAwareIterator<?>[its.size()]));
	}

	public static Stream<ItemStack> allItemsStream(final PlayerEntity player){
		return Streams.stream((Iterator<ItemStack>) PlayerInventoryHelper.allItems(player));
	}

}
