package me.superckl.prayers.util;

import java.util.function.Predicate;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

public class ItemStackPredicates {

	public static final Predicate<ItemStack> IS_WEAPON = stack -> (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem);

	public static final Predicate<ItemStack> IS_ARMOR = stack -> (stack.getItem() instanceof ArmorItem);

	public static final Predicate<ItemStack> IS_BOOTS = stack -> (stack.getItem() instanceof ArmorItem && ((ArmorItem)stack.getItem()).getSlot() == EquipmentSlotType.FEET);

	public static final Predicate<ItemStack> IS_TOOL = stack -> stack.getItem() instanceof ToolItem;

}
