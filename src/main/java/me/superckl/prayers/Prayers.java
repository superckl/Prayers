package me.superckl.prayers;

import org.apache.logging.log4j.LogManager;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.DefaultLivingPrayerUser;
import me.superckl.prayers.capability.DefaultPlayerPrayerUser;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.capability.LivingPrayerUser;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.capability.TalismanPrayerProvider;
import me.superckl.prayers.capability.TickablePrayerProvider;
import me.superckl.prayers.client.AltarRenderer;
import me.superckl.prayers.client.CraftingStandRenderer;
import me.superckl.prayers.client.OfferingStandRenderer;
import me.superckl.prayers.client.RenderTickHandler;
import me.superckl.prayers.client.VesselItemColor;
import me.superckl.prayers.client.gui.GuiEventHandler;
import me.superckl.prayers.client.input.KeyBindings;
import me.superckl.prayers.client.particle.PrayerParticle;
import me.superckl.prayers.init.ModBlocks;
import me.superckl.prayers.init.ModEffects;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.init.ModLoot;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModPotions;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.item.VesselItem;
import me.superckl.prayers.network.packet.PacketInventorySlotChanged;
import me.superckl.prayers.network.packet.PacketSetAltarItem;
import me.superckl.prayers.network.packet.PacketTalismanToggle;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketActivatePrayer;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import me.superckl.prayers.network.packet.user.PacketSetPrayerLevel;
import me.superckl.prayers.network.packet.user.PacketSetPrayerPoints;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.potion.PotionTransformRecipe;
import me.superckl.prayers.server.CommandSet;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
public class Prayers {

	public static final String MOD_ID = "prayers";

	public Prayers() {
		LogHelper.setLogger(LogManager.getFormatterLogger(Prayers.MOD_ID));
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);
		bus.addListener(this::clientSetup);
		bus.addListener(this::initColors);
		bus.addListener(this::createRegistry);
		bus.addListener(this::registerParticleFactory);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.setup());

		ModBlocks.REGISTER.register(bus);
		ModItems.REGISTER.register(bus);
		ModTiles.REGISTER.register(bus);
		ModParticles.REGISTER.register(bus);
		ModRecipes.REGISTER.register(bus);
		ModEffects.REGISTER.register(bus);
		ModPotions.REGISTER.register(bus);
		Prayer.REGISTER.register(bus);
		AltarItem.REGISTER.register(bus);
		ModLoot.REGISTER.register(bus);
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.register(this);
			MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
			MinecraftForge.EVENT_BUS.register(AltarItem.class);
			MinecraftForge.EVENT_BUS.register(VesselItem.class);
			BrewingRecipeRegistry.addRecipe(Ingredient.of(new ItemStack(ModItems.BLESSED_WATER::get)),
					Ingredient.of(new ItemStack(ModItems.GILDED_BONE::get)), PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.INSTANT_PRAYER.get()));
			BrewingRecipeRegistry.addRecipe(new PotionTransformRecipe(ModPotions.INSTANT_PRAYER::get, Items.REDSTONE, ModPotions.PRAYER_RENEWAL::get));
			BrewingRecipeRegistry.addRecipe(new PotionTransformRecipe(ModPotions.INSTANT_PRAYER::get, Items.GLOWSTONE_DUST, ModPotions.STRONG_INSTANT_PRAYER::get));
			BrewingRecipeRegistry.addRecipe(new PotionTransformRecipe(ModPotions.PRAYER_RENEWAL::get, Items.REDSTONE, ModPotions.LONG_PRAYER_RENEWAL::get));

		});
		CapabilityManager.INSTANCE.register(PlayerPrayerUser.class, new PlayerPrayerUser.Storage(), () -> new DefaultPlayerPrayerUser(null));
		CapabilityManager.INSTANCE.register(LivingPrayerUser.class, new TickablePrayerProvider.Storage<LivingPrayerUser>(), () -> new DefaultLivingPrayerUser(null, 0));
		CapabilityManager.INSTANCE.register(InventoryPrayerProvider.class, new TickablePrayerProvider.Storage<InventoryPrayerProvider>(), () -> new TalismanPrayerProvider(ItemStack.EMPTY));
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
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketTalismanToggle.class,
				PacketTalismanToggle::encode, PacketTalismanToggle::decode, PacketTalismanToggle::handle);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
			MinecraftForge.EVENT_BUS.register(KeyBindings.class);
			MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
		});
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_PRAYER_GUI);
		ModTiles.ALTARS.values().forEach(tileTypeObj -> ClientRegistry.bindTileEntityRenderer(tileTypeObj.get(), AltarRenderer::new));
		ClientRegistry.bindTileEntityRenderer(ModTiles.OFFERING_STAND.get(), OfferingStandRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTiles.CRAFTING_STAND.get(), CraftingStandRenderer::new);
	}

	private void initColors(final ColorHandlerEvent.Item e) {
		e.getItemColors().register(new VesselItemColor(), ModItems.VESSEL::get);
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
										.then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("level", IntegerArgumentType.integer(1))
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
