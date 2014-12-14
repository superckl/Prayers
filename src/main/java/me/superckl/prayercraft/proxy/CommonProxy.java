package me.superckl.prayercraft.proxy;

import me.superckl.prayercraft.PrayerCraft;
import me.superckl.prayercraft.common.entity.tile.TileEntityBasicAltar;
import me.superckl.prayercraft.common.gui.GuiHandler;
import me.superckl.prayercraft.common.handler.BucketEventHandler;
import me.superckl.prayercraft.common.handler.EntityEventHandler;
import me.superckl.prayercraft.common.handler.PlayerTickHandler;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModFluids;
import me.superckl.prayercraft.common.reference.ModItems;
import me.superckl.prayercraft.network.MessageDisablePrayer;
import me.superckl.prayercraft.network.MessageEnablePrayer;
import me.superckl.prayercraft.network.MessageHandlerDisablePrayerServer;
import me.superckl.prayercraft.network.MessageHandlerEnablePrayerServer;
import me.superckl.prayercraft.network.MessageHandlerOpenPrayerGui;
import me.superckl.prayercraft.network.MessageOpenPrayerGui;
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
		FMLCommonHandler.instance().bus().register(PrayerCraft.getInstance().getConfig());
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		MinecraftForge.EVENT_BUS.register(new BucketEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(PrayerCraft.getInstance(), new GuiHandler());
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
