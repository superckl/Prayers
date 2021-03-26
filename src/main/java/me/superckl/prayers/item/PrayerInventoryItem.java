package me.superckl.prayers.item;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import me.superckl.prayers.capability.IInventoryPrayerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PrayerInventoryItem extends Item{

	@Getter
	protected final boolean shouldDrainHolder;

	public PrayerInventoryItem(final Properties props, final boolean shouldDrainHolder) {
		super(props);
		this.shouldDrainHolder = shouldDrainHolder;
	}

	@Override
	public void inventoryTick(final ItemStack stack, final World level, final Entity entity, final int slot, final boolean selected) {
		if(!(entity instanceof LivingEntity))
			return;
		final IInventoryPrayerProvider cap = IInventoryPrayerProvider.get(stack);
		cap.tick(Pair.of((LivingEntity) entity, stack));
	}

	public void onPointsDepleted() {}

}
