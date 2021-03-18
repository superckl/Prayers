package me.superckl.prayers;

import org.apache.logging.log4j.LogManager;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import me.superckl.prayers.capability.CapabilityEventHandler;
import me.superckl.prayers.capability.DefaultPrayerUser;
import me.superckl.prayers.capability.IPrayerUser;
import me.superckl.prayers.client.AltarRenderer;
import me.superckl.prayers.client.CraftingStandRenderer;
import me.superckl.prayers.client.OfferingStandRenderer;
import me.superckl.prayers.client.RenderTickHandler;
import me.superckl.prayers.client.input.KeyBindings;
import me.superckl.prayers.client.particle.PrayerParticle;
import me.superckl.prayers.init.ModBlocks;
import me.superckl.prayers.init.ModEffects;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModPotions;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.network.packet.PacketInventorySlotChanged;
import me.superckl.prayers.network.packet.PacketSetAltarItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketActivatePrayer;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import me.superckl.prayers.network.packet.user.PacketSetPrayerLevel;
import me.superckl.prayers.network.packet.user.PacketSetPrayerPoints;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.server.CommandSet;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerParticleFactory);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.setup());

		ModBlocks.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModItems.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModTiles.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModParticles.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModRecipes.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModEffects.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModPotions.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		Prayer.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		AltarItem.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.register(this);
			MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
		});
		CapabilityManager.INSTANCE.register(IPrayerUser.class, new IPrayerUser.Storage(), DefaultPrayerUser::new);
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
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketInventorySlotChanged.class,
				PacketInventorySlotChanged::encode, PacketInventorySlotChanged::decode, PacketInventorySlotChanged::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSetAltarItem.class,
				PacketSetAltarItem::encode, PacketSetAltarItem::decode, PacketSetAltarItem::handle);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
			MinecraftForge.EVENT_BUS.register(KeyBindings.class);
		});
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_PRAYER_GUI);
		ModTiles.ALTARS.values().forEach(tileTypeObj -> ClientRegistry.bindTileEntityRenderer(tileTypeObj.get(), AltarRenderer::new));
		ClientRegistry.bindTileEntityRenderer(ModTiles.OFFERING_STAND.get(), OfferingStandRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTiles.CRAFTING_STAND.get(), CraftingStandRenderer::new);
	}

	private void createRegistry(final RegistryEvent.NewRegistry e) {
		new RegistryBuilder<Prayer>().setName(new ResourceLocation(Prayers.MOD_ID, "prayers")).setType(Prayer.class).create();
		new RegistryBuilder<AltarItem>().setName(new ResourceLocation(Prayers.MOD_ID, "altar_items")).setType(AltarItem.class).create();
	}

	@SubscribeEvent
	public void registerCommands(final RegisterCommandsEvent e) {
		final SuggestionProvider<CommandSource> simpleNumberEx = (context, builder) -> builder.suggest(1).suggest(10).buildFuture();
		final LiteralArgumentBuilder<CommandSource> root = Commands.literal(Prayers.MOD_ID).then(Commands.literal("set")
				.then(Commands.literal("points").then(Commands.argument("targets", EntityArgument.entities())
						.then(Commands.argument("amount", FloatArgumentType.floatArg(0)).suggests(simpleNumberEx)
								.executes(CommandSet::prayerPoints)))).then(Commands.literal("level")
										.then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("level", IntegerArgumentType.integer(1))
												.suggests(simpleNumberEx).executes(CommandSet::prayerLevel)))));

		e.getDispatcher().register(root);
	}

	//Called to ensure the overworld saved data is the one we use
	public void onServerStarting(final FMLServerStartingEvent e) {
		AltarsSavedData.get(e.getServer().getLevel(World.OVERWORLD));
	}

	@SuppressWarnings("resource")
	public void registerParticleFactory(final ParticleFactoryRegisterEvent e) {
		Minecraft.getInstance().particleEngine.register(ModParticles.ALTAR_ACTIVE.get(), PrayerParticle.Factory::new);
		Minecraft.getInstance().particleEngine.register(ModParticles.ITEM_SACRIFICE.get(), PrayerParticle.Factory::new);
	}

}
