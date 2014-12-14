package me.superckl.prayercraft.common.reference;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import me.superckl.prayercraft.common.fluid.FluidHolyWater;
import me.superckl.prayercraft.common.fluid.block.BlockFluidHolyWater;

public class ModFluids {

	public static final FluidHolyWater holyWater = new FluidHolyWater("holyWater"); 
	
	public static void init(){
		FluidRegistry.registerFluid(holyWater);
		ModBlocks.holyWater = new BlockFluidHolyWater(holyWater, Material.water);
		GameRegistry.registerBlock(ModBlocks.holyWater, ModBlocks.Names.HOLY_WATER);
		ItemStack filledBucket = new ItemStack(ModItems.bucket);
		ModItems.bucket.fill(filledBucket, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME), true);
		FluidContainerRegistry.registerFluidContainer(holyWater, filledBucket, FluidContainerRegistry.EMPTY_BUCKET);
		ItemStack filledBottle = new ItemStack(ModItems.bottle);
		ModItems.bottle.fill(filledBottle, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4), true);
		FluidContainerRegistry.registerFluidContainer(holyWater, filledBottle, FluidContainerRegistry.EMPTY_BUCKET);
	}
	
}
