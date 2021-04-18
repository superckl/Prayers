package me.superckl.prayers.item;

import java.util.List;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ForbiddenFruitItem extends Item{

	public static Food FRUIT_FOOD = new Food.Builder().alwaysEat().nutrition(1).saturationMod(0.6F).effect(() -> new EffectInstance(Effects.HARM, 1), 1).build();

	public ForbiddenFruitItem() {
		super(new Item.Properties().tab(ModItems.PRAYERS_GROUP).food(ForbiddenFruitItem.FRUIT_FOOD));
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack stack, final World level, final LivingEntity entity) {
		final ItemStack newStack = super.finishUsingItem(stack, level, entity);
		if(entity instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity) entity;
			player.enchantmentSeed = player.getRandom().nextInt();
		}
		return newStack;
	}

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("forbidden_fruit")).withStyle(TextFormatting.GRAY));
	}

}
