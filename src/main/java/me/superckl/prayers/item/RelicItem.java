package me.superckl.prayers.item;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class RelicItem extends Item{

	public static final String BOON_KEY = "boon";
	public static final String CHARGED_KEY = "charged";

	@Getter
	private final ItemBoon type;

	public RelicItem(final ItemBoon type) {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
		this.type = type;
	}

	@Override
	public ActionResultType interactLivingEntity(final ItemStack stack, final PlayerEntity player,
			final LivingEntity entity, final Hand hand) {
		if(entity instanceof WitherEntity) {
			if(!player.level.isClientSide && !RelicItem.isCharged(stack) && this.storeBoon(entity, stack)) {
				if(this.type.isHasModifier())
					entity.getAttribute(this.type.getAttributeSupplier().get()).addPermanentModifier(this.type.getModifierSupplier().apply(EquipmentSlotType.MAINHAND));
				final ITextComponent name = entity.hasCustomName() ? entity.getCustomName():entity.getName();
				entity.setCustomName(new TranslationTextComponent(this.type.getNameId()+"_wither", name));
			}
			if(!player.isCreative())
				player.getItemInHand(hand).shrink(1);
			return ActionResultType.sidedSuccess(player.level.isClientSide);
		}
		return ActionResultType.PASS;
	}

	@Override
	public String getDescriptionId(final ItemStack stack) {
		String id = super.getDescriptionId(stack);
		if(RelicItem.isCharged(stack))
			id = id.concat("_charged");
		return id;
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return RelicItem.isCharged(stack);
	}

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltips, final ITooltipFlag flag) {
		tooltips.add(this.type.getName().withStyle(TextFormatting.LIGHT_PURPLE));
		if(RelicItem.isCharged(stack))
			tooltips.add(new TranslationTextComponent(LangUtil.buildTextLoc("relic.apply")).withStyle(TextFormatting.GRAY));
	}

	public boolean storeBoon(final Entity entity, final ItemStack stack) {
		final CompoundNBT perData = entity.getPersistentData();
		if(!perData.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND))
			perData.put(Prayers.MOD_ID, new CompoundNBT());
		final CompoundNBT prayersData = perData.getCompound(Prayers.MOD_ID);
		if(!prayersData.contains(RelicItem.BOON_KEY, Constants.NBT.TAG_STRING)) {
			prayersData.put(RelicItem.BOON_KEY, StringNBT.valueOf(this.type.name()));
			return true;
		}
		return false;
	}

	public static Optional<ItemBoon> getBoon(final Entity entity) {
		final CompoundNBT perData = entity.getPersistentData();
		if(perData.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
			final CompoundNBT prayersData = perData.getCompound(Prayers.MOD_ID);
			if(prayersData.contains(RelicItem.BOON_KEY, Constants.NBT.TAG_STRING))
				return Optional.of(ItemBoon.valueOf(prayersData.getString(RelicItem.BOON_KEY)));
		}
		return Optional.empty();
	}

	public static void setCharged(final ItemStack stack) {
		final CompoundNBT prayersData = stack.getOrCreateTagElement(Prayers.MOD_ID);
		prayersData.putBoolean(RelicItem.CHARGED_KEY, true);
	}

	public static boolean isCharged(final ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND) &&
				stack.getTagElement(Prayers.MOD_ID).contains(RelicItem.CHARGED_KEY))
			return stack.getTagElement(Prayers.MOD_ID).getBoolean(RelicItem.CHARGED_KEY);
		return false;
	}

	@Override
	public void fillItemCategory(final ItemGroup group, final NonNullList<ItemStack> stacks) {
		super.fillItemCategory(group, stacks);
		if(this.allowdedIn(group)) {
			final ItemStack stack = new ItemStack(this);
			RelicItem.setCharged(stack);
			stacks.add(stack);
		}
	}

}
