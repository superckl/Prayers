package me.superckl.prayers.client;

import java.util.Optional;

import me.superckl.prayers.AltarItem;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.InventoryPrayerProvider;
import me.superckl.prayers.client.gui.GuiEventHandler;
import me.superckl.prayers.client.input.KeyBindings;
import me.superckl.prayers.client.particle.PrayerParticle;
import me.superckl.prayers.client.render.AltarRenderer;
import me.superckl.prayers.client.render.CraftingStandRenderer;
import me.superckl.prayers.client.render.OfferingStandRenderer;
import me.superckl.prayers.client.render.RenderEventHandler;
import me.superckl.prayers.init.ModEntities;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEvents {

	public static void register() {
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(ClientEvents::initColors);
		bus.addListener(ClientEvents::clientSetup);
		bus.addListener(ClientEvents::registerParticleFactory);
	}

	private static void initColors(final ColorHandlerEvent.Item e) {
		e.getItemColors().register(new VesselItemColor(), ModItems.VESSEL::get);
	}

	private static void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
			MinecraftForge.EVENT_BUS.register(KeyBindings.class);
			MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
			MinecraftForge.EVENT_BUS.register(ClientEvents.class);
			final IItemPropertyGetter pointsGetter = (stack, level, entity) -> {
				InventoryPrayerProvider provider;
				try {
					provider = CapabilityHandler.getPrayerCapability(stack);
				} catch (final IllegalArgumentException e) {
					provider = CapabilityHandler.getPrayerCapability(stack.copy());
				}
				return (float) (provider.getCurrentPrayerPoints()/provider.getMaxPrayerPoints());
			};
			ItemModelsProperties.register(ModItems.RELIQUARY.get(), new ResourceLocation(Prayers.MOD_ID, "points"), pointsGetter);
			ItemModelsProperties.register(ModItems.TALISMAN.get(), new ResourceLocation(Prayers.MOD_ID, "points"), pointsGetter);
		});
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_PRAYER_GUI);
		ClientRegistry.registerKeyBinding(KeyBindings.TOGGLE_TALISMANS);
		ModTiles.ALTARS.values().forEach(tileTypeObj -> ClientRegistry.bindTileEntityRenderer(tileTypeObj.get(), AltarRenderer::new));
		ClientRegistry.bindTileEntityRenderer(ModTiles.OFFERING_STAND.get(), OfferingStandRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTiles.CRAFTING_STAND.get(), CraftingStandRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.GRENADE.get(), manager -> new SpriteRenderer<>(manager, ClientHelper.getItemRenderer()));
	}

	private static void registerParticleFactory(final ParticleFactoryRegisterEvent e) {
		ClientHelper.getParticleEnginge().register(ModParticles.ALTAR_ACTIVE.get(), PrayerParticle.Factory::new);
		ClientHelper.getParticleEnginge().register(ModParticles.ITEM_SACRIFICE.get(), PrayerParticle.Factory::new);
	}

	@SubscribeEvent
	public static void fillTooltip(final ItemTooltipEvent e) {
		//Altar item text
		final AltarItem aItem = AltarItem.find(e.getItemStack());
		if(aItem != null && (aItem.canOffer() || aItem.canSacrifice())) {
			final String addS = aItem.getOfferPoints() != 1 ? "s":"";
			e.getToolTip().add(new TranslationTextComponent(LangUtil.buildTextLoc("offering")).withStyle(TextFormatting.BLUE));
			if(aItem.canOffer())
				e.getToolTip().add(new TranslationTextComponent(LangUtil.buildTextLoc("offering.points"), aItem.getOfferPoints(), addS).withStyle(TextFormatting.BLUE));
			if(aItem.canSacrifice())
				e.getToolTip().add(new TranslationTextComponent(LangUtil.buildTextLoc("offering.xp"), aItem.getSacrificeXP()).withStyle(TextFormatting.BLUE));
		}

		//Boon text
		final Optional<ItemBoon> boons = ItemBoon.getBoon(e.getItemStack());
		boons.ifPresent(boon -> {
			e.getToolTip().add(boon.getName().withStyle(TextFormatting.LIGHT_PURPLE));
			if(boon.isHasTooltip())
				e.getToolTip().add(boon.getTooltip().withStyle(TextFormatting.LIGHT_PURPLE));
		});
	}

}
