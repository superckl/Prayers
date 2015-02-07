package me.superckl.prayers.proxy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.altar.AltarRegistry;
import me.superckl.prayers.common.altar.crafting.BasicTableCraftingHandler;
import me.superckl.prayers.common.entity.EntityUndeadWizardPriest;
import me.superckl.prayers.common.entity.EntityWizardSpell;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.gui.GuiHandler;
import me.superckl.prayers.common.handler.EntityEventHandler;
import me.superckl.prayers.common.handler.PlayerTickHandler;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.worldgen.ComponentAltarRoom;
import me.superckl.prayers.common.worldgen.PrayersVillageCreationHandler;
import me.superckl.prayers.network.MessageDisablePrayer;
import me.superckl.prayers.network.MessageEnablePrayer;
import me.superckl.prayers.network.MessageHandlerDisablePrayerServer;
import me.superckl.prayers.network.MessageHandlerEnablePrayerServer;
import me.superckl.prayers.network.MessageHandlerOpenPrayerGui;
import me.superckl.prayers.network.MessageOpenPrayerGui;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

public abstract class CommonProxy implements IProxy{

	@Override
	public void registerHandlers() {
		FMLCommonHandler.instance().bus().register(Prayers.getInstance().getConfig());
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(Prayers.getInstance(), new GuiHandler());
		ModData.PRAYER_UPDATE_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("prayerUpdate");
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerEnablePrayerServer.class, MessageEnablePrayer.class, 0, Side.SERVER);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerDisablePrayerServer.class, MessageDisablePrayer.class, 1, Side.SERVER);
		ModData.PRAYER_UPDATE_CHANNEL.registerMessage(MessageHandlerOpenPrayerGui.class, MessageOpenPrayerGui.class, 2, Side.SERVER);
		MapGenStructureIO.func_143031_a(ComponentAltarRoom.class, "altarRoom");
		VillagerRegistry.instance().registerVillageCreationHandler(new PrayersVillageCreationHandler());
	}

	@Override
	public void registerEntities() {
		GameRegistry.registerTileEntity(TileEntityOfferingTable.class, "tileEntityOfferingTable");
		final int wizardID = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityUndeadWizardPriest.class, "prayersundeadpriest", wizardID, Color.GRAY.getRGB(), Color.BLACK.getRGB());
		EntityRegistry.registerModEntity(EntityUndeadWizardPriest.class, "prayersundeadpriest", wizardID, Prayers.getInstance(), 80, 3, true);
		final int spellID = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityWizardSpell.class, "prayerswizardspell", spellID);
		EntityRegistry.registerModEntity(EntityWizardSpell.class, "prayerswizardspell", spellID, Prayers.getInstance(), 80, 3, true);
	}

	@Override
	public void registerEntitySpawns(){
		for(final BiomeGenBase gen:BiomeGenBase.getBiomeGenArray())
			if(gen != null)
				EntityRegistry.addSpawn(EntityUndeadWizardPriest.class, 2, 1, 1, EnumCreatureType.monster, gen);
	}

	@Override
	public void registerRecipes(){
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 3, 15), new ItemStack(ModItems.basicBone));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 3, 15), new ItemStack(ModItems.basicBone, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 3, 15), new ItemStack(ModItems.basicBone, 1, 2));
		final ItemStack filledBottle = new ItemStack(ModItems.bottle);
		final ItemStack soakedBones = new ItemStack(ModItems.basicBone, 1, 3);
		final NBTTagCompound comp = new NBTTagCompound();
		comp.setBoolean("soaked", true);
		soakedBones.setTagCompound(comp);
		GameRegistry.addShapelessRecipe(soakedBones, new ItemStack(ModItems.basicBone, 1, 3), filledBottle);
		AltarRegistry.registerOfferingTableRecipe(new BasicTableCraftingHandler(filledBottle, new ItemStack(Items.potionitem, 1, 0), Collections.EMPTY_LIST, 200, 0.5F));
		AltarRegistry.registerOfferingTableRecipe(new BasicTableCraftingHandler(new ItemStack(Items.golden_apple, 1, 1), new ItemStack(Items.golden_apple),
				Arrays.asList(filledBottle, filledBottle, filledBottle, filledBottle, filledBottle), 6000, .083F));
		AltarRegistry.registerOfferingTableRecipe(new BasicTableCraftingHandler(new ItemStack(ModItems.aTome), new ItemStack(Items.book), new ArrayList<ItemStack>(), 100, 0.5F));
	}

}
