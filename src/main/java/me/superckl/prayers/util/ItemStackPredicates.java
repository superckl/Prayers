package me.superckl.prayers.util;

import java.util.function.Predicate;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class ItemStackPredicates {

	public static final Predicate<ItemStack> IS_WEAPON = stack -> (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem);

	public static final Predicate<ItemStack> IS_ARMOR = stack -> (stack.getItem() instanceof ArmorItem);

}
