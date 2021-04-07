package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Iterator;

import me.superckl.prayers.item.PrayerInventoryItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketDeactivateInventoryPrayer;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class InventoryPrayerProvider extends TickablePrayerProvider<ItemStack>{

	protected final ItemStack ref;

	public InventoryPrayerProvider(final ItemStack ref) {
		this.ref = ref;
	}

	public boolean canActivatePrayer(final PlayerEntity player, final Prayer prayer) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		return prayer.isEnabled() && user.canUseItemPrayer(prayer) && !user.hasActiveItem(prayer) &&
				(this.getCurrentPrayerPoints() >= user.modifyDrain(prayer.getDrain()/20F) ||
				((PrayerInventoryItem<?>) this.ref.getItem()).isShouldDrainHolder() && user.getCurrentPrayerPoints() >= user.modifyDrain(prayer.getDrain()/20F));
	}

	public void inventoryTick(final PlayerEntity entity, final int slot) {
		final Collection<Prayer> prayers = this.getActivePrayers();
		if(prayers.isEmpty())
			return;
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(entity);
		final Iterator<Prayer> it = prayers.iterator();
		while(it.hasNext()) {
			final Prayer prayer = it.next();
			if(!user.canUseItemPrayer(prayer)) {
				it.remove();
				this.deactivatePrayer(prayer);
			}
		}
		float drain = user.modifyDrain((float) prayers.stream().mapToDouble(Prayer::getDrain).sum()/20F);
		final PrayerInventoryItem<?> item = (PrayerInventoryItem<?>) this.ref.getItem();
		final boolean drainHolder = item.isShouldDrainHolder();
		if(drainHolder)
			drain = drain - user.drainReliquaries(drain);
		float newPoints = this.getCurrentPrayerPoints()-drain;
		if (newPoints < 0) {
			drain = -newPoints;
			newPoints = 0;
			if(drainHolder) {
				final float remainingPoints = user.drainPoints(drain, false);
				if(remainingPoints <= 0) {
					this.deactivateAllPrayers();
					if(entity instanceof ServerPlayerEntity)
						PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new PacketDeactivateInventoryPrayer(slot));
					item.onPointsDepleted();
				}
			}else {
				this.deactivateAllPrayers();
				if(entity instanceof ServerPlayerEntity)
					PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new PacketDeactivateInventoryPrayer(slot));
				item.onPointsDepleted();
			}
		}
		this.setCurrentPrayerPoints(newPoints);
	}

	public boolean deactivateAllPrayers(final PlayerEntity player) {
		final Iterator<Prayer> it = this.getActivePrayers().iterator();
		boolean removed = false;
		while(it.hasNext())
			removed = removed || this.deactivatePrayer(it.next(), player);
		return removed;
	}

	public boolean deactivatePrayer(final Prayer prayer, final PlayerEntity player) {
		if(super.deactivatePrayer(prayer)) {
			prayer.onDeactivate(player);
			return true;
		}
		return false;
	}

	public boolean activatePrayer(final Prayer prayer, final PlayerEntity player) {
		if(this.canActivatePrayer(player, prayer)) {
			final boolean deactivated = CapabilityHandler.getPrayerCapability(player).deactivatePrayer(prayer);
			super.activatePrayer(prayer);
			if(deactivated)
				prayer.onActivate(player);
			return true;
		}
		return false;
	}

	public boolean togglePrayer(final Prayer prayer, final PlayerEntity player) {
		if (this.isPrayerActive(prayer))
			return this.deactivatePrayer(prayer);
		return this.activatePrayer(prayer, player);
	}

	@Override
	public void tick() {}

}
