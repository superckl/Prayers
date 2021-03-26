package me.superckl.prayers.capability;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CapabilityEventHandler {

	//Attaches the prayer capability to all living entities
	@SubscribeEvent
	public void attachUserEntity(final AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof LivingEntity) {
			final ITickablePrayerProvider.Provider<ILivingPrayerUser> provider = new ITickablePrayerProvider.Provider<>(new DefaultLivingPrayerUser(), () -> Prayers.PRAYER_USER_CAPABILITY);
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "prayer_user"), provider);
			e.addListener(provider::invalidate);
		}
	}

	@SubscribeEvent
	public void attachTalisman(final AttachCapabilitiesEvent<Item> e) {
		if(e.getObject() == ModItems.TALISMAN.get()) {
			final ITickablePrayerProvider.Provider<IInventoryPrayerProvider> provider = new ITickablePrayerProvider.Provider<>(new TalismanPrayerProvider(), () -> Prayers.INVENTORY_PRAYER_CAPABILITY);
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "inventory_prayer_provider"), provider);
			e.addListener(provider::invalidate);
		}
	}

	//Sync prayer data when a client logs in
	@SubscribeEvent
	public void onPlayerLogin(final PlayerLoggedInEvent e) {
		final AltarsSavedData data = AltarsSavedData.get((ServerWorld)e.getPlayer().level);
		if(data.hasPendingXP(e.getPlayer().getUUID()))
			ILivingPrayerUser.getUser(e.getPlayer()).giveXP(data.getAndRemoveXP(e.getPlayer().getUUID()));
		PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(e::getPlayer),
				PacketSyncPrayerUser.fromPlayer(e.getPlayer()));
	}

	//Clones prayer data when a player dies
	@SubscribeEvent
	public void onPlayerClone(final PlayerEvent.Clone e) {
		e.getOriginal().getCapability(Prayers.PRAYER_USER_CAPABILITY).ifPresent(user -> {
			final ILivingPrayerUser newUser =  ILivingPrayerUser.getUser(e.getPlayer());
			Prayers.PRAYER_USER_CAPABILITY.readNBT(newUser, null, Prayers.PRAYER_USER_CAPABILITY.writeNBT(user, null));
		});
	}

	@SubscribeEvent
	public void onLivingTick(final LivingUpdateEvent e) {
		ILivingPrayerUser.getUser(e.getEntityLiving()).tick(e.getEntityLiving());
	}

	//Updates clients of an entity's prayer data when they begin tracking
	@SubscribeEvent
	public void onPlayerTrack(final PlayerEvent.StartTracking e) {
		if(!(e.getPlayer() instanceof ServerPlayerEntity))
			return;
		e.getEntity().getCapability(Prayers.PRAYER_USER_CAPABILITY).ifPresent(user -> {
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()),
					PacketSyncPrayerUser.fromPlayer(e.getPlayer()));
		});
	}

	//Deactivates all prayers when an entity dies
	@SubscribeEvent
	public void onDeath(final LivingDeathEvent e) {
		ILivingPrayerUser.getUser(e.getEntityLiving()).deactivateAllPrayers();
	}

}
