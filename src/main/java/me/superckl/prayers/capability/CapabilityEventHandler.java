package me.superckl.prayers.capability;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.TileEntityAltar;
import me.superckl.prayers.network.packet.PacketSyncPrayerUser;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability.IStorage;
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
			final IPrayerUser.Provider provider = new IPrayerUser.Provider(new DefaultPrayerUser());
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "prayer_user"), provider);
			e.addListener(provider::invalidate);
		}
	}

	//Attaches the prayer capability to altar tiles
	@SubscribeEvent
	public void attachUserTile(final AttachCapabilitiesEvent<TileEntity> e) {
		if (e.getObject() instanceof TileEntityAltar) {
			final IPrayerUser.Provider provider = new IPrayerUser.Provider(new AltarPrayerUser());
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "prayer_user"), provider);
			e.addListener(provider::invalidate);
		}
	}

	//Sync prayer data when a client logs in
	@SubscribeEvent
	public void onPlayerLogin(final PlayerLoggedInEvent e) {
		if(e.getPlayer() instanceof ServerPlayerEntity) {
			final IPrayerUser user = IPrayerUser.getUser(e.getPlayer());
			final INBT userNBT = Prayers.PRAYER_USER_CAPABILITY.getStorage().writeNBT(Prayers.PRAYER_USER_CAPABILITY, user, null);
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(e::getPlayer),
					PacketSyncPrayerUser.builder().entityID(e.getPlayer().getEntityId()).userNBT(userNBT).build());
		}
	}

	//Clones prayer data when a player dies
	@SubscribeEvent
	public void onPlayerClone(final PlayerEvent.Clone e) {
		e.getOriginal().getCapability(Prayers.PRAYER_USER_CAPABILITY).ifPresent(user -> {
			final IPrayerUser newUser =  IPrayerUser.getUser(e.getPlayer());
			final IStorage<IPrayerUser> storage = Prayers.PRAYER_USER_CAPABILITY.getStorage();
			storage.readNBT(Prayers.PRAYER_USER_CAPABILITY, newUser, null, storage.writeNBT(Prayers.PRAYER_USER_CAPABILITY, user, null));
		});
	}

	@SubscribeEvent
	public void onLivingTick(final LivingUpdateEvent e) {
		IPrayerUser.getUser(e.getEntityLiving()).applyDrain();
	}

	//Updates clients of an entity's prayer data when they begin tracking
	@SubscribeEvent
	public void onPlayerTrack(final PlayerEvent.StartTracking e) {
		if(!(e.getPlayer() instanceof ServerPlayerEntity))
			return;
		e.getEntity().getCapability(Prayers.PRAYER_USER_CAPABILITY).ifPresent(user -> {
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()),
					PacketSyncPrayerUser.builder().entityID(e.getEntity().getEntityId())
					.userNBT(Prayers.PRAYER_USER_CAPABILITY.getStorage().writeNBT(Prayers.PRAYER_USER_CAPABILITY, user, null)).build());
		});
	}

	//Deactivates all prayers when an entity dies
	@SubscribeEvent
	public void onDeath(final LivingDeathEvent e) {
		IPrayerUser.getUser(e.getEntity()).deactivateAllPrayers();
	}

}
