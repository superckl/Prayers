package me.superckl.prayers.proxy;

import java.util.Random;

import me.superckl.prayers.client.gui.InventoryTabPrayers;
import me.superckl.prayers.client.handler.EntityRenderHandler;
import me.superckl.prayers.client.handler.InputHandler;
import me.superckl.prayers.client.handler.RenderTickHandler;
import me.superckl.prayers.client.render.RenderBlockOfferingTable;
import me.superckl.prayers.client.render.RenderTileOfferingTable;
import me.superckl.prayers.client.render.RenderUndeadWizardPriest;
import me.superckl.prayers.client.render.RenderWizardSpell;
import me.superckl.prayers.common.entity.EntityUndeadWizardPriest;
import me.superckl.prayers.common.entity.EntityWizardSpell;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.reference.KeyBindings;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.RenderData;
import me.superckl.prayers.network.MessageDisablePrayer;
import me.superckl.prayers.network.MessageEnablePrayer;
import me.superckl.prayers.network.MessageHandlerDisablePrayerClient;
import me.superckl.prayers.network.MessageHandlerEnablePrayerClient;
import me.superckl.prayers.network.MessageHandlerUpdatePrayers;
import me.superckl.prayers.network.MessageUpdatePrayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{

	private final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void registerHandlers() {
		super.registerHandlers();
		MinecraftForge.EVENT_BUS.register(new EntityRenderHandler());
		MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
		MinecraftForge.EVENT_BUS.register(new TabRegistry());
		FMLCommonHandler.instance().bus().register(new InputHandler());
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerEnablePrayerClient.class, MessageEnablePrayer.class, 0, Side.CLIENT);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerDisablePrayerClient.class, MessageDisablePrayer.class, 1, Side.CLIENT);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerUpdatePrayers.class, MessageUpdatePrayers.class, 4, Side.CLIENT);
	}

	@Override
	public void setupGuis() {
		if(!Loader.isModLoaded("TConstruct") && !Loader.isModLoaded("Galacticraft")){
			boolean found = false;
			for(final AbstractTab tab:TabRegistry.getTabList())
				if(tab instanceof InventoryTabVanilla){
					found = true;
					break;
				}
			if(!found)
				TabRegistry.registerTab(new InventoryTabVanilla());
		}
		TabRegistry.registerTab(new InventoryTabPrayers());

	}

	@Override
	public void registerBindings() {
		KeyBindings.OPEN_PRAYERS = new KeyBinding("key.prayers.openprayers", Keyboard.KEY_P, "key.categories.prayers");
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_PRAYERS);
	}

	@Override
	public void renderEffect(final String name, final Object ... args) {
		if(name.equalsIgnoreCase("waterBless")){
			final float x = ((Float)args[1]).floatValue();
			final float y = ((Float)args[2]).floatValue();
			final float z = ((Float)args[3]).floatValue();
			final Random rand = (Random) args[4];
			final float startX = (x+(rand.nextFloat()*3))-1;
			final float startY = y+(rand.nextFloat()*.5F)+1;//TODO actual height
			final float startZ = (z+(rand.nextFloat()*3))-1;
			final EntityFireworkSparkFX ent = new EntityFireworkSparkFX((World) args[0], startX, startY, startZ, ((x+.5F)-startX)*.05F, (y-startY)*.05F, ((z+.5F)-startZ)*.05F, this.mc.effectRenderer);
			ent.setRBGColorF(251F/255F, 253F/255F, 219F/255F);
			ent.noClip = true;
			this.mc.effectRenderer.addEffect(ent);
		}
	}

	@Override
	public void registerRenderers() {
		RenderData.BlockIDs.OFFERING_TABLE = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(RenderData.BlockIDs.OFFERING_TABLE, new RenderBlockOfferingTable());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOfferingTable.class, new RenderTileOfferingTable());
		RenderingRegistry.registerEntityRenderingHandler(EntityUndeadWizardPriest.class, new RenderUndeadWizardPriest());
		RenderingRegistry.registerEntityRenderingHandler(EntityWizardSpell.class, new RenderWizardSpell());
	}

}
