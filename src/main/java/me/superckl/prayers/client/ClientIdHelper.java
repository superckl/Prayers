package me.superckl.prayers.client;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.player.PlayerEntity;

@RequiredArgsConstructor
public class ClientIdHelper {

	private final String id;
	private final Prayer prayer;

	public String modifyId() {
		final PlayerEntity player = ClientHelper.getPlayer();
		if(this.prayer != null && player != null && player.isAlive() && CapabilityHandler.getPrayerCapability(player).getPrayerLevel() >= this.prayer.getLevel())
			return this.id.concat("_prayer");
		return this.id;
	}

}
