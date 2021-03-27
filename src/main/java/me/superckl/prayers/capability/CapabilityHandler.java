package me.superckl.prayers.capability;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.item.PrayerInventoryItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CapabilityHandler {

	@CapabilityInject(PlayerPrayerUser.class)
	private static Capability<PlayerPrayerUser> PLAYER_CAPABILITY;
	@CapabilityInject(LivingPrayerUser.class)
	private static Capability<LivingPrayerUser> LIVING_CAPABILITY;
	@CapabilityInject(InventoryPrayerProvider.class)
	private static Capability<InventoryPrayerProvider> INVENTORY_CAPABILITY;

	//Attaches the prayer capability to all living entities
	@SubscribeEvent
	public void attachUserEntity(final AttachCapabilitiesEvent<Entity> e) {
		final TickablePrayerProvider.Provider<?> provider;
		if (e.getObject() instanceof LivingEntity) {
			if(e.getObject() instanceof PlayerEntity)
				provider = CapabilityHandler.makeProvider(new DefaultPlayerPrayerUser((PlayerEntity) e.getObject()));
			else
				provider = CapabilityHandler.makeProvider(new DefaultLivingPrayerUser((LivingEntity) e.getObject(), 0));
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "prayer_user"), provider);
			e.addListener(provider::invalidate);
		}
	}

	@SubscribeEvent
	public void attachTalisman(final AttachCapabilitiesEvent<ItemStack> e) {
		if(e.getObject().getItem() instanceof PrayerInventoryItem) {
			final TickablePrayerProvider.Provider<?> provider = CapabilityHandler.makeProvider(((PrayerInventoryItem<?>) e.getObject().getItem()).newProvider(e.getObject()));
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "inventory_prayer_provider"), provider);
			e.addListener(provider::invalidate);
		}
	}

	//Sync prayer data when a client logs in
	@SubscribeEvent
	public void onPlayerLogin(final PlayerLoggedInEvent e) {
		final AltarsSavedData data = AltarsSavedData.get((ServerWorld)e.getPlayer().level);
		if(data.hasPendingXP(e.getPlayer().getUUID()))
			CapabilityHandler.getPrayerCapability(e.getPlayer()).giveXP(data.getAndRemoveXP(e.getPlayer().getUUID()));
		PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(e::getPlayer),
				PacketSyncPrayerUser.from(e.getPlayer()));
	}

	@SubscribeEvent
	public void onLivingTick(final LivingUpdateEvent e) {
		if(!e.getEntityLiving().level.isClientSide && e.getEntityLiving().isAlive())
			CapabilityHandler.getPrayerCapability(e.getEntityLiving()).tick();
	}

	//Deactivates all prayers when an entity dies
	@SubscribeEvent
	public void onDeath(final LivingDeathEvent e) {
		CapabilityHandler.getPrayerCapability(e.getEntityLiving()).deactivateAllPrayers();
	}

	//Clones prayer data when a player dies
	@SubscribeEvent
	public void onPlayerClone(final PlayerEvent.Clone e) {
		e.getOriginal().getCapability(CapabilityHandler.PLAYER_CAPABILITY).ifPresent(user -> {
			final PlayerPrayerUser newUser =  CapabilityHandler.getPrayerCapability(e.getPlayer());
			CapabilityHandler.PLAYER_CAPABILITY.readNBT(newUser, null, CapabilityHandler.PLAYER_CAPABILITY.writeNBT(user, null));
		});
	}

	//Updates clients of an entity's prayer data when they begin tracking
	@SubscribeEvent
	public void onPlayerTrack(final PlayerEvent.StartTracking e) {
		if(!(e.getPlayer() instanceof ServerPlayerEntity) || !(e.getTarget() instanceof LivingEntity))
			return;
		PrayersPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()),
				PacketSyncPrayerUser.from((LivingEntity) e.getTarget()));
	}

	public static PlayerPrayerUser getPrayerCapability(final PlayerEntity ref) {
		return ref.getCapability(CapabilityHandler.PLAYER_CAPABILITY).orElseThrow(() ->
		new IllegalArgumentException("Passed player with no inventory prayer capability! "+ref));
	}

	public static TickablePrayerProvider<? extends LivingEntity> getPrayerCapability(final LivingEntity ref) {
		if(ref instanceof PlayerEntity)
			return CapabilityHandler.getPrayerCapability((PlayerEntity) ref);
		else
			return ref.getCapability(CapabilityHandler.LIVING_CAPABILITY).orElseThrow(() ->
			new IllegalArgumentException("Passed living entity with no prayer capability! "+ref));
	}

	public static InventoryPrayerProvider getPrayerCapability(final ItemStack ref) {
		return ref.getCapability(CapabilityHandler.INVENTORY_CAPABILITY).orElseThrow(() ->
		new IllegalArgumentException("Passed itemstack with no inventory prayer capability! "+ref));
	}

	public static <T extends PlayerPrayerUser> TickablePrayerProvider.Provider<PlayerPrayerUser> makeProvider(final T cap){
		return new TickablePrayerProvider.Provider<>(cap, () -> CapabilityHandler.PLAYER_CAPABILITY);
	}

	public static <T extends LivingPrayerUser> TickablePrayerProvider.Provider<LivingPrayerUser> makeProvider(final T cap){
		return new TickablePrayerProvider.Provider<>(cap, () -> CapabilityHandler.LIVING_CAPABILITY);
	}

	public static <T extends InventoryPrayerProvider> TickablePrayerProvider.Provider<InventoryPrayerProvider> makeProvider(final T cap){
		return new TickablePrayerProvider.Provider<>(cap, () -> CapabilityHandler.INVENTORY_CAPABILITY);
	}

	public static INBT serialize(final TickablePrayerProvider<?> cap) {
		if(cap instanceof PlayerPrayerUser)
			return CapabilityHandler.PLAYER_CAPABILITY.writeNBT((PlayerPrayerUser) cap, null);
		else if(cap instanceof LivingPrayerUser)
			return CapabilityHandler.LIVING_CAPABILITY.writeNBT((LivingPrayerUser) cap, null);
		else if(cap instanceof InventoryPrayerProvider)
			return CapabilityHandler.INVENTORY_CAPABILITY.writeNBT((InventoryPrayerProvider) cap, null);
		else
			throw new IllegalArgumentException("Unknown capability type! "+cap);
	}

	public static void deserialize(final TickablePrayerProvider<?> cap, final INBT nbt) {
		if(cap instanceof PlayerPrayerUser)
			CapabilityHandler.PLAYER_CAPABILITY.readNBT((PlayerPrayerUser) cap, null, nbt);
		else if(cap instanceof LivingPrayerUser)
			CapabilityHandler.LIVING_CAPABILITY.readNBT((LivingPrayerUser) cap, null, nbt);
		else if(cap instanceof InventoryPrayerProvider)
			CapabilityHandler.INVENTORY_CAPABILITY.readNBT((InventoryPrayerProvider) cap, null, nbt);
		else
			throw new IllegalArgumentException("Unknown capability type! "+cap);
	}

}
