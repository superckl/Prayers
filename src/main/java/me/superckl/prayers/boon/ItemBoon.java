package me.superckl.prayers.boon;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.Getter;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.util.ItemStackPredicates;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

@Getter
public enum ItemBoon {

	ATTACK_DAMAGE(() -> Attributes.ATTACK_DAMAGE, new DamageSupplier(), ItemStackPredicates.IS_WEAPON, false,
			DamageSupplier.validSlots()),
	ARMOR(() -> Attributes.ARMOR, new ArmorSupplier(), ItemStackPredicates.IS_ARMOR, false,
			ArmorSupplier.validSlots()),
	SPEED(() -> Attributes.MOVEMENT_SPEED, new SpeedSupplier(), ItemStackPredicates.IS_ARMOR, false,
			SpeedSupplier.validSlots()),
	ATTACK_SPEED(() -> Attributes.ATTACK_SPEED, new AttackSpeedSupplier(), ItemStackPredicates.IS_WEAPON, false,
			AttackSpeedSupplier.validSlots()),
	STEP_UP(null, null, ItemStackPredicates.IS_BOOTS, true),
	DIG_SPEED(null, null, ItemStackPredicates.IS_TOOL, true),
	PRAYER_DRAIN(null, null, ItemStackPredicates.IS_ARMOR, true),
	CURIOS(null, null, ItemStackPredicates.IS_WEAPON, true);

	public static final String BOON_KEY = "boons";

	private final boolean hasModifier;
	private final Supplier<Attribute> attributeSupplier;
	private final Function<EquipmentSlotType, AttributeModifier> modifierSupplier;
	@Getter(AccessLevel.PRIVATE)
	private final Predicate<ItemStack> matchingPredicate;
	private final boolean hasTooltip;
	private final EquipmentSlotType[] types;
	private IFormattableTextComponent name;
	private IFormattableTextComponent tooltip;

	ItemBoon(final Supplier<Attribute> attributeSupplier, final Function<EquipmentSlotType, AttributeModifier> supplier,
			final Predicate<ItemStack> matchingPredicate, final boolean hasTooltip, final EquipmentSlotType... equipmentSlotTypes) {
		this.hasModifier = attributeSupplier != null;
		this.attributeSupplier = attributeSupplier;
		this.modifierSupplier = supplier;
		this.matchingPredicate = matchingPredicate;
		this.hasTooltip = hasTooltip;
		this.types = equipmentSlotTypes;
	}

	public IFormattableTextComponent getName() {
		if(this.name == null)
			this.name = new TranslationTextComponent(this.getNameId());
		return this.name;
	}

	public IFormattableTextComponent getTooltip() {
		if(this.tooltip == null)
			this.tooltip = new TranslationTextComponent(this.getNameId().concat("_tooltip"));
		return this.tooltip;
	}

	public String getNameId() {
		return Util.makeDescriptionId("boon", new ResourceLocation(Prayers.MOD_ID, this.name().toLowerCase()));
	}

	public void setBoon(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.putString(ItemBoon.BOON_KEY, this.name());
	}

	public boolean canBeAppliedTo(final ItemStack stack) {
		return !stack.isEmpty() && this.matchingPredicate != null && (this.matchingPredicate.test(stack) ||
				stack.getItem().getTags().contains(new ResourceLocation(Prayers.MOD_ID, this.name().toLowerCase())));
	}

	public boolean has(final ItemStack stack) {
		final Optional<ItemBoon> boon = ItemBoon.getBoon(stack);
		return boon.isPresent() && boon.get() == this;
	}

	public static void removeBoon(final ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND) &&
				stack.getTagElement(Prayers.MOD_ID).contains(ItemBoon.BOON_KEY, Constants.NBT.TAG_STRING))
			stack.getTagElement(Prayers.MOD_ID).remove(ItemBoon.BOON_KEY);
	}

	public static Optional<ItemBoon> getBoon(final ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND) &&
				stack.getTagElement(Prayers.MOD_ID).contains(ItemBoon.BOON_KEY, Constants.NBT.TAG_STRING))
			return Optional.of(ItemBoon.valueOf(stack.getTagElement(Prayers.MOD_ID).getString(ItemBoon.BOON_KEY)));
		return Optional.empty();
	}

	protected boolean contains(final ListNBT boons) {
		for(int i = 0; i < boons.size(); i++)
			if(boons.getString(i).equals(this.name()))
				return true;
		return false;
	}

}
