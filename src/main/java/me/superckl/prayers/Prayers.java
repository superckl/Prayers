package me.superckl.prayers;

import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import me.superckl.prayers.client.gui.RenderTickHandler;
import me.superckl.prayers.client.input.KeyBindings;
import me.superckl.prayers.network.packet.PacketActivatePrayer;
import me.superckl.prayers.network.packet.PacketDeactivatePrayer;
import me.superckl.prayers.network.packet.PacketSetPrayerLevel;
import me.superckl.prayers.network.packet.PacketSetPrayerPoints;
import me.superckl.prayers.network.packet.PacketSyncPrayerUser;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.server.CommandSet;
import me.superckl.prayers.user.DefaultPrayerUser;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(Prayers.MOD_ID)
public class Prayers
{

	public static final String MOD_ID = "prayers";

	@CapabilityInject(IPrayerUser.class)
	public static Capability<IPrayerUser> PRAYER_USER_CAPABILITY;

	public Prayers() {
		LogHelper.setLogger(LogManager.getFormatterLogger(Prayers.MOD_ID));
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Prayer.class, this::registerPrayers);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.setup());
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		CapabilityManager.INSTANCE.register(IPrayerUser.class, new DefaultPrayerUser.Storage(), DefaultPrayerUser::new);
		int pIndex = 0;
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketActivatePrayer.class,
				PacketActivatePrayer::encode, PacketActivatePrayer::decode, PacketActivatePrayer::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketDeactivatePrayer.class,
				PacketDeactivatePrayer::encode, PacketDeactivatePrayer::decode, PacketDeactivatePrayer::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSetPrayerPoints.class,
				PacketSetPrayerPoints::encode, PacketSetPrayerPoints::decode, PacketSetPrayerPoints::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSyncPrayerUser.class,
				PacketSyncPrayerUser::encode, PacketSyncPrayerUser::decode, PacketSyncPrayerUser::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSetPrayerLevel.class,
				PacketSetPrayerLevel::encode, PacketSetPrayerLevel::decode, PacketSetPrayerLevel::handle);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
		MinecraftForge.EVENT_BUS.register(KeyBindings.class);
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_PRAYER_GUI);
	}

	private void createRegistry(final RegistryEvent.NewRegistry e) {
		new RegistryBuilder<Prayer>().setName(new ResourceLocation(Prayers.MOD_ID, "prayers")).setType(Prayer.class).create();
	}

	private void registerPrayers(final RegistryEvent.Register<Prayer> e) {
		final IForgeRegistry<Prayer> registry = GameRegistry.findRegistry(Prayer.class);
		final List<String> configPrays = Lists.newArrayList(Config.getInstance().getPrayers().get());
		Prayer.all().forEach(prayer -> {
			if(configPrays.remove(prayer.getRegistryName().toString()))
				registry.register(prayer);
		});
		if(!configPrays.isEmpty())
			LogHelper.warn("Prayers in config file do not have corresponding registry entry: "+configPrays.toString());
	}
	
	@SubscribeEvent
	public void registerCommands(final RegisterCommandsEvent e) {
		LiteralArgumentBuilder<CommandSource> root = Commands.literal(Prayers.MOD_ID);
		final SuggestionProvider<CommandSource> simpleFloatEx = (context, builder) -> builder.suggest(0).suggest(10).buildFuture();
		final LiteralArgumentBuilder<CommandSource> set = Commands.literal("set").then(Commands.literal("prayer_points")
				.then(Commands.argument("targets", EntityArgument.entities())
						.then(Commands.argument("amount", FloatArgumentType.floatArg(0)).suggests(simpleFloatEx).executes(CommandSet::prayerPoints))));

		root = root.then(set);
		e.getDispatcher().register(root);
	}

	//Attaches the prayer capability to all living entities
	@SubscribeEvent
	public void attachUser(final AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof LivingEntity) {
			final DefaultPrayerUser.Provider provider = new DefaultPrayerUser.Provider();
			e.addCapability(new ResourceLocation(Prayers.MOD_ID, "prayer_user"), provider);
			e.addListener(provider::invalidate);
		}
	}

	//Sync prayer data when a client logs in
	@SubscribeEvent
	public void onPlayerLogin(final PlayerLoggedInEvent e) {
		if(e.getPlayer() instanceof ServerPlayerEntity) {
			final IPrayerUser user = e.getPlayer().getCapability(Prayers.PRAYER_USER_CAPABILITY)
					.orElseThrow(() -> new IllegalStateException(String.format("Player %s has no prayer capability!", e.getPlayer().getDisplayName().toString())));
			final INBT userNBT = Prayers.PRAYER_USER_CAPABILITY.getStorage().writeNBT(Prayers.PRAYER_USER_CAPABILITY, user, null);
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e.getPlayer()),
					PacketSyncPrayerUser.builder().entityID(e.getPlayer().getEntityId()).userNBT(userNBT).build());
		}
	}

	//Clones prayer data when a player dies
	@SubscribeEvent
	public void onPlayerClone(final PlayerEvent.Clone e) {
		e.getOriginal().getCapability(Prayers.PRAYER_USER_CAPABILITY).ifPresent(user -> {
			final IPrayerUser newUser = e.getPlayer().getCapability(Prayers.PRAYER_USER_CAPABILITY)
					.orElseThrow(() -> new IllegalStateException(String.format("Player entity %s does not have capability data!", e.getPlayer().toString())));
			final IStorage<IPrayerUser> storage = Prayers.PRAYER_USER_CAPABILITY.getStorage();
			storage.readNBT(Prayers.PRAYER_USER_CAPABILITY, newUser, null, storage.writeNBT(Prayers.PRAYER_USER_CAPABILITY, user, null));
		});
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
		e.getEntity().getCapability(Prayers.PRAYER_USER_CAPABILITY).ifPresent(IPrayerUser::deactivateAllPrayers);
	}

}
