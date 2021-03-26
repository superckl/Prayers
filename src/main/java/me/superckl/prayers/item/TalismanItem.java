package me.superckl.prayers.item;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.IInventoryPrayerProvider;
import me.superckl.prayers.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TalismanItem extends PrayerInventoryItem{

	public static final String PRAYER_KEY = "prayer";

	public TalismanItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP), true);
	}

	public void toggle(final ItemStack stack) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			IInventoryPrayerProvider.get(stack).togglePrayer(prayer);
		});
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		final LazyOptional<Prayer> opt = this.getStoredPrayer(stack);
		if(opt.isPresent())
			return IInventoryPrayerProvider.get(stack).isPrayerActive(opt.orElse(null));
		else
			return false;
	}

	public LazyOptional<Prayer> getStoredPrayer(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		if(nbt.contains(TalismanItem.PRAYER_KEY))
			return LazyOptional.of(() -> GameRegistry.findRegistry(Prayer.class).getValue(new ResourceLocation(nbt.getString(TalismanItem.PRAYER_KEY))));
		else
			return LazyOptional.empty();
	}

	public void storePrayer(final ItemStack stack, final Prayer prayer) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.putString(TalismanItem.PRAYER_KEY, prayer.getRegistryName().toString());
	}

}
