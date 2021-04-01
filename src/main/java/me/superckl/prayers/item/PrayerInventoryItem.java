package me.superckl.prayers.item;

import lombok.Getter;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketSetInventoryItemPoints;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class PrayerInventoryItem<T extends InventoryPrayerProvider> extends Item{

	public static final String CAPABILITY_KEY = Prayers.MOD_ID+"inventory_capability";

	@Getter
	protected final boolean shouldDrainHolder;

	public PrayerInventoryItem(final Properties props, final boolean shouldDrainHolder) {
		super(props);
		this.shouldDrainHolder = shouldDrainHolder;
	}

	@Override
	public void inventoryTick(final ItemStack stack, final World level, final Entity entity, final int slot, final boolean selected) {
		if(level.isClientSide || !(entity instanceof PlayerEntity))
			return;
		final InventoryPrayerProvider provider = CapabilityHandler.getPrayerCapability(stack);
		final float old = provider.getCurrentPrayerPoints();
		provider.inventoryTick((PlayerEntity) entity, slot);
		final float newVal = provider.getCurrentPrayerPoints();
		if(MathUtil.isIntDifferent(old, newVal))
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new PacketSetInventoryItemPoints(newVal, slot));
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
