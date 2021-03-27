package me.superckl.prayers.item;

import java.util.List;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PrayerTomeItem extends Item{

	public static final String PRAYER_KEY = "prayer";

	public PrayerTomeItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public ActionResult<ItemStack> use(final World level, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getItemInHand(hand);
		final LazyOptional<Prayer> prayerOpt = this.getStoredPrayer(stack);
		if(!prayerOpt.isPresent())
			return ActionResult.pass(stack);
		final Prayer prayer = prayerOpt.orElse(null);
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		if(user.getPrayerLevel() < prayer.getLevel() || !user.unlockPrayer(prayerOpt.orElse(null)))
			return ActionResult.fail(stack);
		else {
			final ItemStack shrunk = stack.copy();
			shrunk.shrink(1);
			return ActionResult.consume(shrunk);
		}
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
		if(prayer != null && Minecraft.getInstance().player != null && CapabilityHandler.getPrayerCapability(Minecraft.getInstance().player).getPrayerLevel() >= prayer.getLevel())
			id = id.concat("_prayer");
		return id;
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return this.getStoredPrayer(stack).isPresent();
	}

	@Override
	public Rarity getRarity(final ItemStack stack) {
		if(this.getStoredPrayer(stack).isPresent())
			return Rarity.RARE;
		else return super.getRarity(stack);
	}

	@SuppressWarnings("resource")
	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			if(Minecraft.getInstance().player != null && CapabilityHandler.getPrayerCapability(Minecraft.getInstance().player).getPrayerLevel() < prayer.getLevel()) {
				tooltip.add(new StringTextComponent("You cannot decipher the esoteric text...").withStyle(TextFormatting.GRAY));
				tooltip.add(new StringTextComponent("Requires level "+prayer.getLevel()).withStyle(TextFormatting.DARK_GRAY));
			}else
				tooltip.add(new StringTextComponent("Use to unlock "+prayer.getName()).withStyle(TextFormatting.BLUE));
		});
	}

	public LazyOptional<Prayer> getStoredPrayer(final ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		if(nbt.contains(PrayerTomeItem.PRAYER_KEY))
			return LazyOptional.of(() -> GameRegistry.findRegistry(Prayer.class).getValue(new ResourceLocation(nbt.getString(PrayerTomeItem.PRAYER_KEY))));
		else
			return LazyOptional.empty();
	}

	public void storePrayer(final ItemStack stack, final Prayer prayer) {
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.putString(PrayerTomeItem.PRAYER_KEY, prayer.getRegistryName().toString());
	}

	@Override
	public void fillItemCategory(final ItemGroup tab, final NonNullList<ItemStack> stacks) {
		for(final Prayer prayer:GameRegistry.findRegistry(Prayer.class).getValues())
			if(prayer.isRequiresTome()) {
				final ItemStack stack = new ItemStack(this);
				this.storePrayer(stack, prayer);
				stacks.add(stack);
			}
	}

}
