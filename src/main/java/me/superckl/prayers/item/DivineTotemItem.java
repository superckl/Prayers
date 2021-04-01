package me.superckl.prayers.item;

import java.util.List;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class DivineTotemItem extends Item{

	public DivineTotemItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return true;
	}

	@Override
	public Rarity getRarity(final ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("totem")).withStyle(TextFormatting.GRAY));
	}

}
