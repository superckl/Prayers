package me.superckl.prayers.item;

import lombok.Getter;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class PrayerInventoryItem<T extends InventoryPrayerProvider> extends Item{

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
		CapabilityHandler.getPrayerCapability(stack).inventoryTick((LivingEntity) entity);
	}

	public void onPointsDepleted() {}

	public abstract T newProvider(ItemStack stack);

}