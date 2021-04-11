package me.superckl.prayers.item;

import java.util.Collection;
import java.util.List;

import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.capability.TalismanPrayerProvider;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.client.ClientIdHelper;
import me.superckl.prayers.entity.ai.WitherUsePrayersGoal;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.prayer.ActivationCondition;
import me.superckl.prayers.prayer.Prayer;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TalismanItem extends PrayerInventoryItem<TalismanPrayerProvider>{

	public static final String PRAYER_KEY = "prayer";
	public static final String AUTO_KEY = "auto_activate";
	public static final String TALISMAN_KEY = "talisman";

	public TalismanItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP), true, 1F/Config.getInstance().getTalismanLossFactor().get());
	}

	@Override
	public ActionResult<ItemStack> use(final World level, final PlayerEntity player, final Hand hand) {
		final Collection<Prayer> active = CapabilityHandler.getPrayerCapability(player).getActivePrayers();
		final ItemStack stack = player.getItemInHand(hand);
		final Prayer stored = TalismanItem.getStoredPrayer(stack).orElse(null);
		if(stored != null && player.isCrouching()) {
			TalismanItem.removePrayer(stack);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, player.getSoundSource(), 0.25F, 0.75F);
			return ActionResult.sidedSuccess(stack, level.isClientSide);
		}
		if(active.size() != 1)
			return ActionResult.fail(stack);
		final Prayer prayer = active.iterator().next();
		if(prayer == stored)
			return ActionResult.pass(stack);
		this.applyState(stack, player, State.DEACTIVATE);
		TalismanItem.storePrayer(stack, prayer);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, player.getSoundSource(), 0.25F, 0.75F);
		return ActionResult.sidedSuccess(stack, level.isClientSide);
	}

	@Override
	public ActionResultType interactLivingEntity(final ItemStack stack, final PlayerEntity player,
			final LivingEntity entity, final Hand hand) {
		if(entity instanceof WitherEntity && !TalismanItem.canAutoActivate(stack) && TalismanItem.storeTalisman(entity, stack)) {
			if(!player.level.isClientSide) {
				final WitherUsePrayersGoal goal = new WitherUsePrayersGoal((WitherEntity) entity);
				TalismanItem.getStoredPrayer(stack).ifPresent(goal::addPrayer);
				((WitherEntity)entity).goalSelector.addGoal(0, goal);
				CapabilityHandler.getPrayerCapability(entity).setShouldDrain(false);
				final ITextComponent name = entity.hasCustomName() ? entity.getCustomName():entity.getName();
				entity.setCustomName(new TranslationTextComponent(Util.makeDescriptionId("entity", new ResourceLocation(Prayers.MOD_ID, "boss_enlightened")), name));
			}
			if(!player.isCreative())
				player.getItemInHand(hand).shrink(1);
			return ActionResultType.sidedSuccess(player.level.isClientSide);
		}
		return ActionResultType.PASS;
	}

	@Override
	public ITextComponent getName(final ItemStack stack) {
		final Prayer prayer = TalismanItem.getStoredPrayer(stack).orElse(null);
		if(prayer != null)
			return new TranslationTextComponent(this.getDescriptionId(stack), prayer.getName());
		return super.getName(stack);
	}

	@Override
	public String getDescriptionId(final ItemStack stack) {
		final String id = TalismanItem.canAutoActivate(stack) ? super.getDescriptionId(stack).concat("_auto"):super.getDescriptionId(stack);
		final Prayer prayer = TalismanItem.getStoredPrayer(stack).orElse(null);
		final String newId = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> new ClientIdHelper(id, prayer)::modifyId);
		return newId == null ? id:newId;
	}


	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		final Prayer prayer = TalismanItem.getStoredPrayer(stack).orElse(null);
		boolean shouldToggle = false;
		final boolean auto = TalismanItem.canAutoActivate(stack);
		if(prayer != null) {
			if(level == null || CapabilityHandler.getPrayerCapability(ClientHelper.getPlayer()).canUseItemPrayer(prayer)) {
				if(CapabilityHandler.getPrayerCapability(stack).isPrayerActive(prayer))
					tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("active")).withStyle(TextFormatting.GREEN));
				else
					tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("inactive")).withStyle(TextFormatting.RED));
				if(auto && ActivationCondition.hasCondition(prayer)) {
					tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.auto_conditions")).withStyle(TextFormatting.BLUE));
					final List<ActivationCondition> conditions = ActivationCondition.getConditions(prayer);
					conditions.forEach(condition -> tooltip.add(new StringTextComponent("- ").append(condition.getDescription()).withStyle(TextFormatting.BLUE)));
				}else if(auto)
					tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.prayer_not_auto")).withStyle(TextFormatting.GRAY));
				shouldToggle = true;
			}else
				tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.prayer_obfuscated")).withStyle(TextFormatting.GRAY));
		}else {
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.use_bind")).withStyle(TextFormatting.GRAY));
			if(auto)
				tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.auto_no_prayer")).withStyle(TextFormatting.BLUE));
		}
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
		if(shouldToggle)
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("click_toggle")).withStyle(TextFormatting.DARK_GRAY));
	}

	public boolean applyState(final ItemStack stack, final PlayerEntity player, final State state) {
		final Prayer prayer = TalismanItem.getStoredPrayer(stack).orElse(null);
		if(prayer != null && CapabilityHandler.getPrayerCapability(player).canUseItemPrayer(prayer))
			switch(state) {
			case ACTIVATE:
				return CapabilityHandler.getPrayerCapability(stack).activatePrayer(prayer, player);
			case DEACTIVATE:
				return CapabilityHandler.getPrayerCapability(stack).deactivatePrayer(prayer, player);
			case TOGGLE:
				return CapabilityHandler.getPrayerCapability(stack).togglePrayer(prayer, player);
			}
		return false;
	}

	@Override
	public Rarity getRarity(final ItemStack stack) {
		if(this.isFoil(stack))
			return Rarity.RARE;
		return super.getRarity(stack);
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		final LazyOptional<Prayer> opt = TalismanItem.getStoredPrayer(stack);
		if(opt.isPresent())
			return CapabilityHandler.getPrayerCapability(stack).isPrayerActive(opt.orElse(null));
		return false;
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
	public void fillItemCategory(final ItemGroup tab, final NonNullList<ItemStack> items) {
		super.fillItemCategory(tab, items);
		if(this.allowdedIn(tab)) {
			final ItemStack talisman = new ItemStack(this);
			TalismanItem.setAutoActivate(talisman);
			items.add(talisman);
		}
	}

	public static LazyOptional<Prayer> getStoredPrayer(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		if(nbt.contains(TalismanItem.PRAYER_KEY))
			return LazyOptional.of(() -> GameRegistry.findRegistry(Prayer.class).getValue(new ResourceLocation(nbt.getString(TalismanItem.PRAYER_KEY))));
		return LazyOptional.empty();
	}

	public static void storePrayer(final ItemStack stack, final Prayer prayer) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.putString(TalismanItem.PRAYER_KEY, prayer.getRegistryName().toString());
	}

	public static void removePrayer(final ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
			final CompoundNBT nbt = stack.getTagElement(Prayers.MOD_ID);
			nbt.remove(TalismanItem.PRAYER_KEY);
			if(nbt.isEmpty())
				stack.removeTagKey(Prayers.MOD_ID);
		}
	}

	public static void setAutoActivate(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.putBoolean(TalismanItem.AUTO_KEY, true);
	}

	public static boolean canAutoActivate(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		if(nbt.contains(TalismanItem.AUTO_KEY))
			return nbt.getBoolean(TalismanItem.AUTO_KEY);
		return false;
	}

	public static boolean storeTalisman(final Entity entity, final ItemStack stack) {
		final CompoundNBT perData = entity.getPersistentData();
		if(!perData.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND))
			perData.put(Prayers.MOD_ID, new CompoundNBT());
		final CompoundNBT prayersData = perData.getCompound(Prayers.MOD_ID);
		if(!prayersData.contains(TalismanItem.TALISMAN_KEY, Constants.NBT.TAG_COMPOUND)) {
			prayersData.put(TalismanItem.TALISMAN_KEY, stack.serializeNBT());
			return true;
		}
		return false;
	}

	public static boolean hasStoredTalisman(final Entity entity) {
		final CompoundNBT perData = entity.getPersistentData();
		return perData.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND) &&
				perData.getCompound(Prayers.MOD_ID).contains(TalismanItem.TALISMAN_KEY, Constants.NBT.TAG_COMPOUND);
	}

	@Override
	public TalismanPrayerProvider newProvider(final ItemStack stack) {
		return new TalismanPrayerProvider(stack);
	}

	public enum State {

		TOGGLE,
		ACTIVATE,
		DEACTIVATE;

		public State opposite() {
			switch(this) {
			case ACTIVATE:
				return DEACTIVATE;
			case DEACTIVATE:
				return ACTIVATE;
			case TOGGLE:
				return TOGGLE;
			}
			throw new IllegalArgumentException("Cannot compute opposite of unknown state!");
		}

	}

}
