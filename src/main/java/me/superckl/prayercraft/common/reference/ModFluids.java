package me.superckl.prayercraft.common.reference;

import me.superckl.prayercraft.common.fluid.FluidHolyWater;
import me.superckl.prayercraft.common.fluid.block.BlockFluidHolyWater;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModFluids {

	public static final FluidHolyWater holyWater = new FluidHolyWater("holyWater");

	public static void init(){
		FluidRegistry.registerFluid(ModFluids.holyWater);
		ModBlocks.holyWater = new BlockFluidHolyWater(ModFluids.holyWater, Material.water);
		GameRegistry.registerBlock(ModBlocks.holyWater, ModBlocks.Names.HOLY_WATER);
		final ItemStack filledBucket = new ItemStack(ModItems.bucket);
		ModItems.bucket.fill(filledBucket, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME), true);
		FluidContainerRegistry.registerFluidContainer(ModFluids.holyWater, filledBucket, FluidContainerRegistry.EMPTY_BUCKET);
		final ItemStack filledBottle = new ItemStack(ModItems.bottle);
		ModItems.bottle.fill(filledBottle, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4), true);
		FluidContainerRegistry.registerFluidContainer(ModFluids.holyWater, filledBottle, FluidContainerRegistry.EMPTY_BUCKET);
	}

}
