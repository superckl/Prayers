package me.superckl.prayers.item;

import java.util.List;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.capability.ReliquaryPrayerProvider;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ReliquaryItem extends PrayerInventoryItem<ReliquaryPrayerProvider>{

	public ReliquaryItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP), false, 1/4F);
	}

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		if(level != null) {
			InventoryPrayerProvider provider;
			try {
				provider = CapabilityHandler.getPrayerCapability(stack);
			} catch (final IllegalArgumentException e) {
				// Since the search tree is populated before capabilities are registered, it is possible that an
				// ItemStack will not have the capability. Try again after copying it. If it errors after copying,
				// something is actually wrong.
				provider = CapabilityHandler.getPrayerCapability(stack.copy());
			}
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("item.stored_points"), (int) provider.getCurrentPrayerPoints(), (int) provider.getMaxPrayerPoints()).withStyle(TextFormatting.GRAY));
		}
	}

	@Override
	public ReliquaryPrayerProvider newProvider(final ItemStack stack) {
		return new ReliquaryPrayerProvider(stack);
	}

}
