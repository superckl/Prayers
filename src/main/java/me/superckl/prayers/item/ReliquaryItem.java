package me.superckl.prayers.item;

import java.util.List;

import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.capability.ReliquaryPrayerProvider;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.TalismanItem.State;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ReliquaryItem extends PrayerInventoryItem<ReliquaryPrayerProvider>{

	public static final String ACTIVE_KEY = "active";

	public ReliquaryItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP), false, 1F/Config.getInstance().getReliquaryLossFactor().get().floatValue());
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
		if(ReliquaryItem.isActive(stack))
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("active")).withStyle(TextFormatting.GREEN));
		else
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("inactive")).withStyle(TextFormatting.RED));
	}

	@Override
	public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
		final boolean notEqual = super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
		if(notEqual && !slotChanged && ItemStack.isSame(oldStack, newStack)) {
			final CompoundNBT tag1 = oldStack.getTag().copy();
			tag1.remove(PrayerInventoryItem.CAPABILITY_KEY);
			final CompoundNBT tag2 = newStack.getTag().copy();
			tag2.remove(PrayerInventoryItem.CAPABILITY_KEY);
			if(tag1 == null && tag2 == null || tag1 != null && tag1.equals(tag2))
				return !CapabilityHandler.getPrayerCapability(oldStack).samePrayersActive(CapabilityHandler.getPrayerCapability(newStack));
		}
		return notEqual;
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return ReliquaryItem.isActive(stack);
	}

	public static boolean applyState(final ItemStack stack, final TalismanItem.State state) {
		final boolean isActive = ReliquaryItem.isActive(stack);
		final boolean active = state == State.ACTIVATE ? true:state == State.DEACTIVATE ? false:!isActive;
		if(!active && isActive) {
			stack.getTagElement(Prayers.MOD_ID).remove(ReliquaryItem.ACTIVE_KEY);
			return true;
		}
		if(active && !isActive && CapabilityHandler.getPrayerCapability(stack).getCurrentPrayerPoints() > 0) {
			stack.getOrCreateTagElement(Prayers.MOD_ID).putBoolean(ReliquaryItem.ACTIVE_KEY, true);
			return true;
		}
		return false;
	}

	public static boolean isActive(final ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)){
			final CompoundNBT nbt = stack.getTagElement(Prayers.MOD_ID);
			if(nbt.contains(ReliquaryItem.ACTIVE_KEY, Constants.NBT.TAG_BYTE))
				return nbt.getBoolean(ReliquaryItem.ACTIVE_KEY);
		}
		return false;
	}

	@Override
	public ReliquaryPrayerProvider newProvider(final ItemStack stack) {
		return new ReliquaryPrayerProvider(stack);
	}

}
