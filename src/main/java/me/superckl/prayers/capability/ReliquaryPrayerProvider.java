package me.superckl.prayers.capability;

import me.superckl.prayers.Config;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.item.ReliquaryItem;
import me.superckl.prayers.item.TalismanItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketReliquaryState;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

public class ReliquaryPrayerProvider extends InventoryPrayerProvider{

	private final double maxPoints = Config.getInstance().getReliquaryPoints().get();

	public ReliquaryPrayerProvider(final ItemStack ref) {
		super(ref);
	}

	@Override
	public double getMaxPrayerPoints() {
		return this.maxPoints;
	}

	@Override
	public boolean canActivatePrayer(final PlayerEntity player, final Prayer prayer) {
		return false;
	}

	@Override
	public void inventoryTick(final PlayerEntity entity, final SlotHelper slot) {
		if(this.getCurrentPrayerPoints() <= 0 && !entity.level.isClientSide) {
			ReliquaryItem.applyState(this.ref, TalismanItem.State.DEACTIVATE);
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
					PacketReliquaryState.builder().entityID(entity.getId()).slot(slot).state(TalismanItem.State.DEACTIVATE).build());
		}
	}

}
