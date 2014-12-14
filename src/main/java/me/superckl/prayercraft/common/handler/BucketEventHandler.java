package me.superckl.prayercraft.common.handler;

import me.superckl.prayercraft.common.reference.ModFluids;
import me.superckl.prayercraft.common.reference.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BucketEventHandler {

	@SubscribeEvent
	public void onBucketFill(final FillBucketEvent e){
		final World world = e.world;

		final int x = e.target.blockX;
		final int y = e.target.blockY;
		final int z = e.target.blockZ;

		final Fluid fluid = FluidRegistry.lookupFluidForBlock(world.getBlock(x, y, z));
		if((fluid != null) && (fluid == ModFluids.holyWater)){
			final ItemStack stack = new ItemStack(ModItems.bucket);
			ModItems.bucket.fill(stack, new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), true);
			world.setBlockToAir(x, y, z);
			e.result = stack;
			e.setResult(Result.ALLOW);
		}
	}

}
