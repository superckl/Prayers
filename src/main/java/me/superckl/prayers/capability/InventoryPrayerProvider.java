package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Iterator;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.item.PrayerInventoryItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketDeactivateInventoryPrayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class InventoryPrayerProvider extends TickablePrayerProvider<ItemStack>{

	public InventoryPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	public boolean canActivatePrayer(final PlayerEntity player, final Prayer prayer) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		return prayer.isEnabled() && (user.isPrayerActive(prayer, false) || user.canUseItemPrayer(prayer)) &&
				(this.getCurrentPrayerPoints() >= prayer.getDrain()/20F || ((PrayerInventoryItem<?>) this.ref.getItem()).isShouldDrainHolder() && user.getCurrentPrayerPoints() >= prayer.getDrain()/20F);
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
		final float drain = (float) prayers.stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			final float diff = -newPoints;
			newPoints = 0;
			final PrayerInventoryItem<?> item = (PrayerInventoryItem<?>) this.ref.getItem();
			if(item.isShouldDrainHolder()) {

				final float remainingPoints = user.setCurrentPrayerPoints(user.getCurrentPrayerPoints()-diff);
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

	public boolean activatePrayer(final Prayer prayer, final PlayerEntity player) {
		if(this.canActivatePrayer(player, prayer)) {
			super.activatePrayer(prayer);
			CapabilityHandler.getPrayerCapability(player).deactivatePrayer(prayer);
			return true;
		}else
			return false;
	}

	public boolean togglePrayer(final Prayer prayer, final PlayerEntity player) {
		if (this.isPrayerActive(prayer)) {
			this.deactivatePrayer(prayer);
			return true;
		}else
			return this.activatePrayer(prayer, player);
	}

	@Override
	public void tick() {}

}
