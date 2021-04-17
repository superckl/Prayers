package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Iterator;

import me.superckl.prayers.inventory.SlotHelper;
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

	public void inventoryTick(final PlayerEntity entity, final SlotHelper slot) {
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
		double drain = user.modifyDrain(prayers.stream().mapToDouble(Prayer::getDrain).sum()/20F);
		final PrayerInventoryItem<?> item = (PrayerInventoryItem<?>) this.ref.getItem();
		final boolean drainHolder = item.isShouldDrainHolder();
		if(drainHolder)
			drain = drain - user.drainReliquaries(drain);
		double newPoints = this.getCurrentPrayerPoints()-drain;
		if (newPoints < 0) {
			drain = -newPoints;
			newPoints = 0;
			if(drainHolder) {
				final double drained = user.drainPoints(drain, false);
				if(drained < drain) {
					this.deactivateAllPrayers();
					if(entity instanceof ServerPlayerEntity)
						PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity),
								PacketDeactivateInventoryPrayer.builder().entityID(entity.getId()).slot(slot).build());
					item.onPointsDepleted();
				}
			}else {
				this.deactivateAllPrayers();
				if(entity instanceof ServerPlayerEntity)
					PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity),
							PacketDeactivateInventoryPrayer.builder().entityID(entity.getId()).slot(slot).build());
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
			final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
			user.itemDeactivated(prayer);
			return true;
		}
		return false;
	}

	public boolean activatePrayer(final Prayer prayer, final PlayerEntity player) {
		if(this.canActivatePrayer(player, prayer) && super.activatePrayer(prayer)) {
			final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
			user.deactivatePrayer(prayer);
			user.itemActivated(prayer);
			return true;
		}
		return false;
	}

	public boolean togglePrayer(final Prayer prayer, final PlayerEntity player) {
		if (this.isPrayerActive(prayer))
			return this.deactivatePrayer(prayer, player);
		return this.activatePrayer(prayer, player);
	}

	@Override
	public void tick() {}

	public void dropped(final PlayerEntity player) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		this.getActivePrayers().forEach(prayer -> user.itemDeactivated(prayer));
	}

	public void pickedUp(final PlayerEntity player) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		this.getActivePrayers().forEach(prayer -> user.itemActivated(prayer));
	}

}
