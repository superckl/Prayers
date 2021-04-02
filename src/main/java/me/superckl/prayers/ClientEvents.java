package me.superckl.prayers;

import me.superckl.prayers.client.AltarRenderer;
import me.superckl.prayers.client.CraftingStandRenderer;
import me.superckl.prayers.client.OfferingStandRenderer;
import me.superckl.prayers.client.RenderEventHandler;
import me.superckl.prayers.client.VesselItemColor;
import me.superckl.prayers.client.gui.GuiEventHandler;
import me.superckl.prayers.client.input.KeyBindings;
import me.superckl.prayers.client.particle.PrayerParticle;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModTiles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
		});
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_PRAYER_GUI);
		ClientRegistry.registerKeyBinding(KeyBindings.TOGGLE_TALISMANS);
		ModTiles.ALTARS.values().forEach(tileTypeObj -> ClientRegistry.bindTileEntityRenderer(tileTypeObj.get(), AltarRenderer::new));
		ClientRegistry.bindTileEntityRenderer(ModTiles.OFFERING_STAND.get(), OfferingStandRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTiles.CRAFTING_STAND.get(), CraftingStandRenderer::new);
	}

	@SuppressWarnings("resource")
	private static void registerParticleFactory(final ParticleFactoryRegisterEvent e) {
		Minecraft.getInstance().particleEngine.register(ModParticles.ALTAR_ACTIVE.get(), PrayerParticle.Factory::new);
		Minecraft.getInstance().particleEngine.register(ModParticles.ITEM_SACRIFICE.get(), PrayerParticle.Factory::new);
	}

	@SubscribeEvent
	public static void fillTooltip(final ItemTooltipEvent e) {
		final AltarItem aItem = AltarItem.find(e.getItemStack());
		if(aItem == null)
			return;
		e.getToolTip().add(new StringTextComponent("Altar offering:").withStyle(TextFormatting.BLUE));
		e.getToolTip().add(new StringTextComponent(String.format("- %.1f point(s)", aItem.getOfferPoints())).withStyle(TextFormatting.BLUE));
		e.getToolTip().add(new StringTextComponent(String.format("- %.1f xp", aItem.getSacrificeXP())).withStyle(TextFormatting.BLUE));
	}

}