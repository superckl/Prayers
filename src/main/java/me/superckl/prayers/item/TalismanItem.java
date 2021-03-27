package me.superckl.prayers.item;

import java.util.Collection;
import java.util.List;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TalismanItem extends PrayerInventoryItem{

	public static final String PRAYER_KEY = "prayer";

	public TalismanItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP), true);
	}

	@Override
	public ActionResult<ItemStack> use(final World level, final PlayerEntity player, final Hand hand) {
		final Collection<Prayer> active = CapabilityHandler.getPrayerCapability(player).getActivePrayers();
		final ItemStack stack = player.getItemInHand(hand);
		if(active.size() == 1) {
			final Prayer prayer = active.iterator().next();
			this.deactivate(stack);
			this.storePrayer(stack, prayer);

			return ActionResult.consume(stack);
		}else
			return ActionResult.fail(stack);
	}

	@Override
	public boolean onDroppedByPlayer(final ItemStack item, final PlayerEntity player) {
		this.deactivate(item);
		return super.onDroppedByPlayer(item, player);
	}

	@Override
	public ITextComponent getName(final ItemStack stack) {
		final Prayer prayer = this.getStoredPrayer(stack).orElse(null);
		if(prayer != null)
			return new TranslationTextComponent(this.getDescriptionId(stack), prayer.getName());
		else
			return super.getName(stack);
	}

	@Override
	public String getDescriptionId(final ItemStack stack) {
		String id = super.getDescriptionId(stack);
		final Prayer prayer = this.getStoredPrayer(stack).orElse(null);
		if(prayer != null && !prayer.isObfusctated(Minecraft.getInstance().player))
			id = id.concat("_prayer");
		return id;
	}

	@SuppressWarnings("resource")
	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		final Prayer prayer = this.getStoredPrayer(stack).orElse(null);
		if(prayer != null) {
			if(!prayer.isObfusctated(Minecraft.getInstance().player)) {
				tooltip.add(new StringTextComponent("Bound to "+prayer.getName()).withStyle(TextFormatting.WHITE));
				if(CapabilityHandler.getPrayerCapability(stack).isPrayerActive(prayer))
					tooltip.add(new StringTextComponent("Active").withStyle(TextFormatting.GREEN));
				else
					tooltip.add(new StringTextComponent("Inactive").withStyle(TextFormatting.RED));
			}else
				tooltip.add(new StringTextComponent("The bound prayer eludes you...").withStyle(TextFormatting.GRAY));
		}else
			tooltip.add(new StringTextComponent("Use to bind to active prayer").withStyle(TextFormatting.GRAY));
	}

	@SuppressWarnings("resource")
	public void toggle(final ItemStack stack) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			if(!prayer.isObfusctated(Minecraft.getInstance().player))
				CapabilityHandler.getPrayerCapability(stack).togglePrayer(prayer);
		});
	}

	@SuppressWarnings("resource")
	public void activate(final ItemStack stack) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			if(!prayer.isObfusctated(Minecraft.getInstance().player))
				CapabilityHandler.getPrayerCapability(stack).activatePrayer(prayer);
		});
	}

	@SuppressWarnings("resource")
	public void deactivate(final ItemStack stack) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			if(!prayer.isObfusctated(Minecraft.getInstance().player))
				CapabilityHandler.getPrayerCapability(stack).deactivatePrayer(prayer);
		});
	}

	@Override
	public Rarity getRarity(final ItemStack stack) {
		if(this.isFoil(stack))
			return Rarity.RARE;
		else
			return super.getRarity(stack);
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		final LazyOptional<Prayer> opt = this.getStoredPrayer(stack);
		if(opt.isPresent())
			return CapabilityHandler.getPrayerCapability(stack).isPrayerActive(opt.orElse(null));
		else
			return false;
	}

	public LazyOptional<Prayer> getStoredPrayer(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		if(nbt.contains(TalismanItem.PRAYER_KEY))
			return LazyOptional.of(() -> GameRegistry.findRegistry(Prayer.class).getValue(new ResourceLocation(nbt.getString(TalismanItem.PRAYER_KEY))));
		else
			return LazyOptional.empty();
	}

	public void storePrayer(final ItemStack stack, final Prayer prayer) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.putString(TalismanItem.PRAYER_KEY, prayer.getRegistryName().toString());
	}

}
