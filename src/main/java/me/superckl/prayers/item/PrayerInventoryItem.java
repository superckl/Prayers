package me.superckl.prayers.item;

import lombok.Getter;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.entity.AltarTileEntity;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.inventory.MainInventorySlotHelper;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketSetInventoryItemPoints;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class PrayerInventoryItem<T extends InventoryPrayerProvider> extends Item{

	public static final String CAPABILITY_KEY = Prayers.MOD_ID+"inventory_capability";

	@Getter
	protected final boolean shouldDrainHolder;
	@Getter
	protected final double rechargeLossFactor;

	public PrayerInventoryItem(final Properties props, final boolean shouldDrainHolder, final double rechargeLossFactor) {
		super(props);
		this.shouldDrainHolder = shouldDrainHolder;
		this.rechargeLossFactor = rechargeLossFactor;
	}

	@Override
	public void inventoryTick(final ItemStack stack, final World level, final Entity entity, final int slot, final boolean selected) {
		//Ignore the tick if the slot is undefined. It is likely in a modded slot that must be handled by other methods
		if(!(entity instanceof PlayerEntity) || slot == -1)
			return;
		PrayerInventoryItem.onInventoryTick(stack, (PlayerEntity) entity, new MainInventorySlotHelper(slot));
	}

	public static void onInventoryTick(final ItemStack stack, final PlayerEntity player, final SlotHelper slot) {
		if(player.level.isClientSide)
			return;
		final InventoryPrayerProvider provider = CapabilityHandler.getPrayerCapability(stack);
		final double old = provider.getCurrentPrayerPoints();
		provider.inventoryTick(player, slot);
		final double newVal = provider.getCurrentPrayerPoints();
		if(newVal == 0 || MathUtil.isIntDifferent(old, newVal))
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
					PacketSetInventoryItemPoints.builder().entityID(player.getId()).points(newVal).slot(slot).build());
	}

	@SuppressWarnings("resource")
	@Override
	public ActionResultType useOn(final ItemUseContext context) {
		final TileEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		if(te instanceof AltarTileEntity) {
			if(context.getLevel().isClientSide)
				return ActionResultType.sidedSuccess(true);
			final AltarTileEntity aTE = (AltarTileEntity) te;
			if(aTE.canRegen()) {
				final InventoryPrayerProvider provider = CapabilityHandler.getPrayerCapability(context.getItemInHand());
				final double recharge = provider.getMaxPrayerPoints()-provider.getCurrentPrayerPoints();
				final double actual = aTE.removePoints(recharge/this.rechargeLossFactor)*this.rechargeLossFactor;
				provider.addPoints(actual);
				final EquipmentSlotType type = context.getHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND:EquipmentSlotType.OFFHAND;
				PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) context.getPlayer()),
						PacketSetInventoryItemPoints.builder().entityID(context.getPlayer().getId()).points(provider.getCurrentPrayerPoints())
						.slot(new MainInventorySlotHelper(type)).build());
			}
			return ActionResultType.sidedSuccess(false);
		}
		return ActionResultType.PASS;
	}

	@Override
	public CompoundNBT getShareTag(final ItemStack stack) {
		CompoundNBT nbt = super.getShareTag(stack);
		if(nbt == null)
			nbt = new CompoundNBT();
		nbt.put(PrayerInventoryItem.CAPABILITY_KEY, CapabilityHandler.serialize(CapabilityHandler.getPrayerCapability(stack)));
		return nbt;
	}

	@Override
	public void readShareTag(final ItemStack stack, final CompoundNBT nbt) {
		super.readShareTag(stack, nbt);
		if(nbt != null && nbt.contains(PrayerInventoryItem.CAPABILITY_KEY, Constants.NBT.TAG_COMPOUND))
			CapabilityHandler.deserialize(CapabilityHandler.getPrayerCapability(stack), nbt.getCompound(PrayerInventoryItem.CAPABILITY_KEY));
	}

	public void onPointsDepleted() {}

	public abstract T newProvider(ItemStack stack);

}
