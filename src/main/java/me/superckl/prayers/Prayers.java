package me.superckl.prayers;

import org.apache.logging.log4j.LogManager;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import me.superckl.prayers.boon.BoonEventHandler;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.DefaultLivingPrayerUser;
import me.superckl.prayers.capability.DefaultPlayerPrayerUser;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.capability.TalismanPrayerProvider;
import me.superckl.prayers.capability.TickablePrayerProvider;
import me.superckl.prayers.client.ClientEvents;
import me.superckl.prayers.criteria.ApplyBoonCriteria;
import me.superckl.prayers.criteria.OwnAltarCriteriaTrigger;
import me.superckl.prayers.criteria.PrayerLevelCriteria;
import me.superckl.prayers.entity.ai.EntityEventHandler;
import me.superckl.prayers.init.ModBlocks;
import me.superckl.prayers.init.ModEffects;
import me.superckl.prayers.init.ModEntities;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.init.ModLoot;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModPotions;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.integration.curios.CuriosIntegration;
import me.superckl.prayers.inventory.ContainerSlotHelper;
import me.superckl.prayers.inventory.MainInventorySlotHelper;
import me.superckl.prayers.inventory.SlotHelper;
import me.superckl.prayers.item.BonesItem;
import me.superckl.prayers.item.ItemEvents;
import me.superckl.prayers.item.decree.ItemFrameTickManager;
import me.superckl.prayers.network.packet.PacketSetAltarItem;
import me.superckl.prayers.network.packet.PacketSetAltarItemTicks;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketDeactivateInventoryPrayer;
import me.superckl.prayers.network.packet.inventory.PacketInventorySlotChanged;
import me.superckl.prayers.network.packet.inventory.PacketReliquaryState;
import me.superckl.prayers.network.packet.inventory.PacketSetInventoryItemPoints;
import me.superckl.prayers.network.packet.inventory.PacketTalismanState;
import me.superckl.prayers.network.packet.user.PacketActivatePrayer;
import me.superckl.prayers.network.packet.user.PacketDeactivateAllPrayers;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import me.superckl.prayers.network.packet.user.PacketSetEffects;
import me.superckl.prayers.network.packet.user.PacketSetPrayerLevel;
import me.superckl.prayers.network.packet.user.PacketSetPrayerPoints;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.prayer.ActivationCondition;
import me.superckl.prayers.prayer.Prayer;
import me.superckl.prayers.recipe.FullVesselIngredient;
import me.superckl.prayers.recipe.PotionIngredient;
import me.superckl.prayers.server.CommandBoon;
import me.superckl.prayers.server.CommandSet;
import me.superckl.prayers.world.AltarsSavedData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.command.EnumArgument;

@Mod(Prayers.MOD_ID)
public class Prayers {

	public static final String MOD_ID = "prayers";

	public static boolean hasCurios;

	public Prayers() {
		LogHelper.setLogger(LogManager.getFormatterLogger(Prayers.MOD_ID));
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);
		bus.addListener(this::enqueueIMC);
		bus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializer);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::register);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.setup());
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.setup());

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
		ModEntities.REGISTER.register(bus);
		ModLoot.registerConditions();

		Prayers.hasCurios = ModList.get().isLoaded("curios");
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.register(this);
			MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
			MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
			MinecraftForge.EVENT_BUS.register(new ItemEvents());
			MinecraftForge.EVENT_BUS.register(new BoonEventHandler());
			MinecraftForge.EVENT_BUS.register(ItemFrameTickManager.INSTANCE);
			ActivationCondition.registerConditions();

			this.registerPotions();

			CriteriaTriggers.register(OwnAltarCriteriaTrigger.INSTANCE);
			CriteriaTriggers.register(ApplyBoonCriteria.INSTANCE);
			CriteriaTriggers.register(PrayerLevelCriteria.INSTANCE);
		});
		CapabilityManager.INSTANCE.register(PlayerPrayerUser.class, new PlayerPrayerUser.Storage(), () -> new DefaultPlayerPrayerUser(null));
		CapabilityManager.INSTANCE.register(DefaultLivingPrayerUser.class, new TickablePrayerProvider.Storage<DefaultLivingPrayerUser>(), () -> new DefaultLivingPrayerUser(null, 0));
		CapabilityManager.INSTANCE.register(InventoryPrayerProvider.class, new TickablePrayerProvider.Storage<InventoryPrayerProvider>(), () -> new TalismanPrayerProvider(ItemStack.EMPTY));
		int pIndex = 0;
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketActivatePrayer.class,
				PacketActivatePrayer::encode, PacketActivatePrayer::decode, PacketActivatePrayer::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketDeactivatePrayer.class,
				PacketDeactivatePrayer::encode, PacketDeactivatePrayer::decode, PacketDeactivatePrayer::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketDeactivateAllPrayers.class,
				PacketDeactivateAllPrayers::encode, PacketDeactivateAllPrayers::decode, PacketDeactivateAllPrayers::handle);
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
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSetAltarItemTicks.class,
				PacketSetAltarItemTicks::encode, PacketSetAltarItemTicks::decode, PacketSetAltarItemTicks::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketTalismanState.class,
				PacketTalismanState::encode, PacketTalismanState::decode, PacketTalismanState::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketReliquaryState.class,
				PacketReliquaryState::encode, PacketReliquaryState::decode, PacketReliquaryState::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSetInventoryItemPoints.class,
				PacketSetInventoryItemPoints::encode, PacketSetInventoryItemPoints::decode, PacketSetInventoryItemPoints::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketDeactivateInventoryPrayer.class,
				PacketDeactivateInventoryPrayer::encode, PacketDeactivateInventoryPrayer::decode, PacketDeactivateInventoryPrayer::handle);
		PrayersPacketHandler.INSTANCE.registerMessage(pIndex++, PacketSetEffects.class,
				PacketSetEffects::encode, PacketSetEffects::decode, PacketSetEffects::handle);

		SlotHelper.registerHelper("main_inventory", MainInventorySlotHelper.class, MainInventorySlotHelper::deserialize);
		SlotHelper.registerHelper("current_container", ContainerSlotHelper.class, ContainerSlotHelper::deserialize);
		if(Prayers.hasCurios)
			CuriosIntegration.commonSetup(event);
	}

	private void registerPotions() {
		BrewingRecipeRegistry.addRecipe(Ingredient.of(new ItemStack(ModItems.BLESSED_WATER::get)),
				Ingredient.of(new ItemStack(ModItems.GILDED_BONES.get(BonesItem.Type.SMALL)::get)), PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.INSTANT_PRAYER.get()));
		for(final Item item:new Item[] {Items.POTION, Items.LINGERING_POTION, Items.SPLASH_POTION}) {
			BrewingRecipeRegistry.addRecipe(new PotionIngredient(item, ModPotions.INSTANT_PRAYER.get()),
					Ingredient.of(Items.REDSTONE), PotionUtils.setPotion(new ItemStack(item), ModPotions.PRAYER_RENEWAL.get()));
			BrewingRecipeRegistry.addRecipe(new PotionIngredient(item, ModPotions.INSTANT_PRAYER.get()),
					Ingredient.of(Items.GLOWSTONE_DUST), PotionUtils.setPotion(new ItemStack(item), ModPotions.STRONG_INSTANT_PRAYER.get()));
			BrewingRecipeRegistry.addRecipe(new PotionIngredient(item, ModPotions.PRAYER_RENEWAL.get()),
					Ingredient.of(Items.REDSTONE), PotionUtils.setPotion(new ItemStack(item), ModPotions.LONG_PRAYER_RENEWAL.get()));
		}
	}

	public void registerRecipeSerializer(final RegistryEvent.Register<IRecipeSerializer<?>> e) {
		CraftingHelper.register(new ResourceLocation(Prayers.MOD_ID, "full_vessel"), FullVesselIngredient.Serializer.INSTANCE);
		CraftingHelper.register(new ResourceLocation(Prayers.MOD_ID, "potion"), PotionIngredient.Serializer.INSTANCE);
	}

	private void enqueueIMC(final InterModEnqueueEvent e) {
		if(Prayers.hasCurios)
			CuriosIntegration.sendSlotIMC(e);
	}

	@SubscribeEvent
	public void registerCommands(final RegisterCommandsEvent e) {
		final SuggestionProvider<CommandSource> simpleNumberEx = (context, builder) -> builder.suggest(1).suggest(10).buildFuture();
		final LiteralArgumentBuilder<CommandSource> root = Commands.literal(Prayers.MOD_ID).then(Commands.literal("set")
				.then(Commands.literal("points").then(Commands.argument("targets", EntityArgument.entities())
						.then(Commands.argument("amount", FloatArgumentType.floatArg(0)).suggests(simpleNumberEx)
								.executes(CommandSet::prayerPoints)))).then(Commands.literal("level")
										.then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("level", IntegerArgumentType.integer(1))
												.suggests(simpleNumberEx).executes(CommandSet::prayerLevel)))))
				.then(Commands.literal("boon").then(Commands.argument("targets", EntityArgument.entities())
						.then(Commands.argument("boon", EnumArgument.enumArgument(ItemBoon.class)).executes(CommandBoon::applyBoon))));

		e.getDispatcher().register(root);
	}

	//Called to ensure the overworld saved data is the one we use
	public void onServerStarting(final FMLServerStartingEvent e) {
		AltarsSavedData.get(e.getServer().getLevel(World.OVERWORLD));
	}

}
