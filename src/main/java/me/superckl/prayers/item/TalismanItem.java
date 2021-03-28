package me.superckl.prayers.item;

import java.util.Collection;
import java.util.List;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarTileEntity;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.capability.TalismanPrayerProvider;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketSetInventoryItemPoints;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.PacketDistributor;

public class TalismanItem extends PrayerInventoryItem<TalismanPrayerProvider>{

	public static final String PRAYER_KEY = "prayer";

	public TalismanItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP), true);
	}

	@Override
	public ActionResult<ItemStack> use(final World level, final PlayerEntity player, final Hand hand) {
		final Collection<Prayer> active = CapabilityHandler.getPrayerCapability(player).getActivePrayers();
		final ItemStack stack = player.getItemInHand(hand);
		final Prayer stored = this.getStoredPrayer(stack).orElse(null);
		if(active.size() == 1) {
			final Prayer prayer = active.iterator().next();
			if(prayer == stored)
				return ActionResult.pass(stack);
			this.deactivate(stack, player);
			this.storePrayer(stack, prayer);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, player.getSoundSource(), 0.25F, 0.75F);
			return ActionResult.sidedSuccess(stack, level.isClientSide);
		}else
			return ActionResult.fail(stack);
	}

	@SuppressWarnings("resource")
	@Override
	public ActionResultType useOn(final ItemUseContext context) {
		if(context.getLevel().isClientSide)
			return ActionResultType.sidedSuccess(true);
		final TileEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		if(te instanceof AltarTileEntity) {
			final AltarTileEntity aTE = (AltarTileEntity) te;
			if(aTE.canRegen()) {
				final InventoryPrayerProvider provider = CapabilityHandler.getPrayerCapability(context.getItemInHand());
				final float recharge = provider.getMaxPrayerPoints()-provider.getCurrentPrayerPoints();
				final float actual = aTE.removePoints(recharge);
				provider.addPoints(actual);
				final EquipmentSlotType type = context.getHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND:EquipmentSlotType.OFFHAND;
				PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) context.getPlayer()),
						new PacketSetInventoryItemPoints(provider.getCurrentPrayerPoints(), type));
			}
			return ActionResultType.sidedSuccess(false);
		}
		return ActionResultType.PASS;
	}

	@Override
	public boolean onDroppedByPlayer(final ItemStack item, final PlayerEntity player) {
		this.deactivate(item, player);
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

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		final Prayer prayer = this.getStoredPrayer(stack).orElse(null);
		boolean shouldToggle = false;
		if(prayer != null) {
			if(level == null || CapabilityHandler.getPrayerCapability(Minecraft.getInstance().player).canUseItemPrayer(prayer)) {
				//tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.bound"), prayer.getName().withStyle(TextFormatting.AQUA)).withStyle(TextFormatting.GRAY));
				if(CapabilityHandler.getPrayerCapability(stack).isPrayerActive(prayer))
					tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("active")).withStyle(TextFormatting.GREEN));
				else
					tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("inactive")).withStyle(TextFormatting.RED));
				shouldToggle = true;
			}else
				tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.prayer_obfuscated")).withStyle(TextFormatting.GRAY));
		}else
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.use_bind")).withStyle(TextFormatting.GRAY));
		if(level != null) {
			final InventoryPrayerProvider provider = CapabilityHandler.getPrayerCapability(stack);
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("talisman.points"), (int) provider.getCurrentPrayerPoints(), (int) provider.getMaxPrayerPoints()).withStyle(TextFormatting.GRAY));
		}
		if(shouldToggle)
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("click_toggle")).withStyle(TextFormatting.DARK_GRAY));
	}

	public boolean toggle(final ItemStack stack, final PlayerEntity player) {
		final Prayer prayer = this.getStoredPrayer(stack).orElse(null);
		if(prayer != null && CapabilityHandler.getPrayerCapability(player).canUseItemPrayer(prayer))
			return CapabilityHandler.getPrayerCapability(stack).togglePrayer(prayer, player);
		else
			return false;
	}

	public void activate(final ItemStack stack, final PlayerEntity player) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			if(CapabilityHandler.getPrayerCapability(player).canUseItemPrayer(prayer))
				CapabilityHandler.getPrayerCapability(stack).activatePrayer(prayer, player);
		});
	}

	public void deactivate(final ItemStack stack, final PlayerEntity player) {
		this.getStoredPrayer(stack).ifPresent(prayer -> {
			if(CapabilityHandler.getPrayerCapability(player).canUseItemPrayer(prayer))
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
		if(opt.isPresent() && (Minecraft.getInstance().player == null || CapabilityHandler.getPrayerCapability(Minecraft.getInstance().player).canUseItemPrayer(opt.orElse(null))))
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

	@Override
	public TalismanPrayerProvider newProvider(final ItemStack stack) {
		return new TalismanPrayerProvider(stack);
	}

}
