package me.superckl.prayers.proxy;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.entity.tile.TileEntityBasicAltar;
import me.superckl.prayers.common.gui.GuiHandler;
import me.superckl.prayers.common.handler.BucketEventHandler;
import me.superckl.prayers.common.handler.EntityEventHandler;
import me.superckl.prayers.common.handler.PlayerTickHandler;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.network.MessageDisablePrayer;
import me.superckl.prayers.network.MessageEnablePrayer;
import me.superckl.prayers.network.MessageHandlerDisablePrayerServer;
import me.superckl.prayers.network.MessageHandlerEnablePrayerServer;
import me.superckl.prayers.network.MessageHandlerOpenPrayerGui;
import me.superckl.prayers.network.MessageOpenPrayerGui;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public abstract class CommonProxy implements IProxy{

	@Override
	public void registerHandlers() {
		FMLCommonHandler.instance().bus().register(Prayers.getInstance().getConfig());
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		MinecraftForge.EVENT_BUS.register(new BucketEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(Prayers.getInstance(), new GuiHandler());
		ModData.PRAYER_UPDATE_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("prayerUpdate");
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerEnablePrayerServer.class, MessageEnablePrayer.class, 0, Side.SERVER);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerDisablePrayerServer.class, MessageDisablePrayer.class, 1, Side.SERVER);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerOpenPrayerGui.class, MessageOpenPrayerGui.class, 2, Side.SERVER);
	}

	@Override
	public void registerEntities() {
		GameRegistry.registerTileEntity(TileEntityBasicAltar.class, "tileEntityBasicAltar");
	}

	@Override
	public void registerRecipes(){
		final ItemStack filledBucket = new ItemStack(ModItems.bucket);
		ModItems.bucket.fill(filledBucket, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME), true);
		final ItemStack filledBottle = new ItemStack(ModItems.bottle);
		ModItems.bottle.fill(filledBottle, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4), true);
		GameRegistry.addShapelessRecipe(filledBucket, Items.bucket, filledBottle, filledBottle, filledBottle, filledBottle);
	}

}
