package me.superckl.prayers.boon;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.Getter;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

@Getter
public enum ItemBoon {

	ATTACK_DAMAGE(() -> Attributes.ATTACK_DAMAGE, new DamageSupplier(), EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND),
	ARMOR(() -> Attributes.ARMOR, new ArmorSupplier(), EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD),
	CURIOS(null, null);

	public static final String BOON_KEY = "boons";

	private final boolean hasModifier;
	private final Supplier<Attribute> attributeSupplier;
	private final Supplier<AttributeModifier> modifierSupplier;
	private final EquipmentSlotType[] types;
	private IFormattableTextComponent name;

	ItemBoon(final Supplier<Attribute> attributeSupplier, final Supplier<AttributeModifier> supplier,
			final EquipmentSlotType... equipmentSlotTypes) {
		this.hasModifier = attributeSupplier != null;
		this.attributeSupplier = attributeSupplier;
		this.modifierSupplier = supplier;
		this.types = equipmentSlotTypes;
	}

	public IFormattableTextComponent getName() {
		if(this.name == null)
			this.name = new TranslationTextComponent(this.getNameId());
		return this.name;
	}

	public String getNameId() {
		return Util.makeDescriptionId("boon", new ResourceLocation(Prayers.MOD_ID, this.name().toLowerCase()));
	}

	public void addTo(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		ListNBT boons;
		if(!nbt.contains(ItemBoon.BOON_KEY, Constants.NBT.TAG_LIST))
			boons = new ListNBT();
		else
			boons = nbt.getList(ItemBoon.BOON_KEY, Constants.NBT.TAG_STRING);
		if(!this.contains(boons)) {
			boons.add(StringNBT.valueOf(this.name()));
			nbt.put(ItemBoon.BOON_KEY, boons);
		}
	}

	public boolean has(final ItemStack stack) {
		final Optional<ListNBT> boons = ItemBoon.getList(stack);
		if(boons.isPresent())
			return this.contains(boons.get());
		return false;
	}

	public static EnumSet<ItemBoon> getBoons(final ItemStack stack){
		final EnumSet<ItemBoon> boons = EnumSet.noneOf(ItemBoon.class);
		ItemBoon.getList(stack).ifPresent(boonsList -> boonsList.forEach(nbt -> boons.add(ItemBoon.valueOf(nbt.getAsString()))));
		return boons;
	}

	protected static Optional<ListNBT> getList(final ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND) &&
				stack.getTagElement(Prayers.MOD_ID).contains(ItemBoon.BOON_KEY, Constants.NBT.TAG_LIST))
			return Optional.of(stack.getTagElement(Prayers.MOD_ID).getList(ItemBoon.BOON_KEY, Constants.NBT.TAG_STRING));
		return Optional.empty();
	}

	protected boolean contains(final ListNBT boons) {
		for(int i = 0; i < boons.size(); i++)
			if(boons.getString(i).equals(this.name()))
				return true;
		return false;
	}

}
