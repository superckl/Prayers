package me.superckl.prayers.common.altar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import me.superckl.prayers.common.altar.crafting.OfferingTableCraftingHandler;
import me.superckl.prayers.common.altar.multi.BlockRequirement;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.utility.BlockLocation;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.NumberHelper;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class AltarRegistry {
	@Getter
	private static final Set<WeakReference<Altar>> loadedAltars = new HashSet<WeakReference<Altar>>();
	private static final List<OfferingTableCraftingHandler> registeredRecipes = new ArrayList<OfferingTableCraftingHandler>();
	@Getter
	private static final Map<Integer, Map<ForgeDirection, Map<BlockLocation, BlockRequirement>>> multiblocks = new HashMap<Integer, Map<ForgeDirection, Map<BlockLocation,BlockRequirement>>>();

	private AltarRegistry(){};

	public static boolean hasMultiBlocksOfTier(final int tier){
		return AltarRegistry.multiblocks.containsKey(tier);
	}

	public static boolean hasMultiBlock(final int tier, final ForgeDirection direction){
		return AltarRegistry.hasMultiBlocksOfTier(tier) ? AltarRegistry.multiblocks.get(tier).containsKey(direction):false;
	}

	public static Map<BlockLocation, BlockRequirement> getMultiBlock(final int tier, final ForgeDirection direction){
		return AltarRegistry.hasMultiBlocksOfTier(tier) ? AltarRegistry.multiblocks.get(tier).get(direction):null;
	}

	public static Altar findAltarAt(final World world, final int x, final int y, final int z){
		final Iterator<WeakReference<Altar>> it = AltarRegistry.loadedAltars.iterator();
		while(it.hasNext()){
			final WeakReference<Altar> weakR = it.next();
			if(weakR.get() == null){
				it.remove();
				continue;
			}
			final Altar altar = weakR.get();
			if(!altar.getHolder().getWorldObj().equals(world) || (altar.getBlocks() == null))
				continue;
			for(final BlockLocation vec:altar.getBlocks())
				if(((vec.getX()) == x) && ((vec.getY()) == y) && ((vec.getZ()) == z))
					return altar;
		}
		return null;
	}

	public static List<OfferingTableCraftingHandler> getRegisteredRecipes(){
		return new ArrayList<OfferingTableCraftingHandler>(AltarRegistry.registeredRecipes);
	}

	public static void registerOfferingTableRecipe(final OfferingTableCraftingHandler recipe){
		AltarRegistry.registerOfferingTableRecipe(recipe, false);
	}

	public static OfferingTableCraftingHandler getRecipeById(final int id){
		if(id >= AltarRegistry.registeredRecipes.size())
			return null;
		return AltarRegistry.registeredRecipes.get(id);
	}

	public static void registerOfferingTableRecipe(final OfferingTableCraftingHandler recipe, final boolean override){
		if(AltarRegistry.registeredRecipes.contains(recipe)){
			LogHelper.warn(StringHelper.build("Class ", PSReflectionHelper.retrieveCallingStackTraceElement().getClassName(), " has re-registered an already registered recipe: ", recipe.getClass().getCanonicalName(), ". There are mod conflictions!"));
			return;
		}
		final ItemStack base = recipe.getBaseIngredient();
		final List<ItemStack> tertiary = recipe.getTertiaryIngredients();
		if((base == null) || (tertiary == null)){
			LogHelper.warn(StringHelper.build("Recipe ", recipe.getClass().getCanonicalName(), " is invalid! It contains one or more null ingredients! It will not be registered."));
			return;
		}
		for(final ItemStack stack:tertiary)
			if(stack == null){
				LogHelper.warn(StringHelper.build("Recipe ", recipe.getClass().getCanonicalName(), " is invalid! It contains one or more null tertiary ingredients! It will not be registered."));
				return;
			}
		final Iterator<OfferingTableCraftingHandler> it = AltarRegistry.registeredRecipes.iterator();
		while(it.hasNext()){
			final OfferingTableCraftingHandler handler = it.next();
			if(handler.checkCompletion(base, tertiary)){
				LogHelper.warn(StringHelper.build("Recipe ", recipe.getClass().getCanonicalName(), " requires ingredients that conflict with an already registered recipe. ", override ? "The registered recipe will be overriden.": "It will not be registered."));
				if(override){
					it.remove();
					break;
				}
				return;
			}
		}
		AltarRegistry.registeredRecipes.add(recipe);
		recipe.setRecipeID(AltarRegistry.registeredRecipes.size()-1);
	}

	public static void registerMultiBlocks(){
		AltarRegistry.multiblocks.clear();
		final Map<ForgeDirection, Map<BlockLocation, BlockRequirement>> tier1 = new HashMap<ForgeDirection, Map<BlockLocation,BlockRequirement>>();

		tier1.put(ForgeDirection.WEST, AltarRegistry.fillTier1West());

		final Map<BlockLocation, BlockRequirement> south = new HashMap<BlockLocation, BlockRequirement>();
		for(final Entry<BlockLocation, BlockRequirement> entry:AltarRegistry.fillTier1West().entrySet())
			south.put(entry.getKey().rotate(NumberHelper.COS_90, NumberHelper.SIN_90), entry.getValue());
		tier1.put(ForgeDirection.SOUTH, south);

		final Map<BlockLocation, BlockRequirement> east = new HashMap<BlockLocation, BlockRequirement>();
		for(final Entry<BlockLocation, BlockRequirement> entry:AltarRegistry.fillTier1West().entrySet())
			east.put(entry.getKey().rotate(NumberHelper.COS_180, NumberHelper.SIN_180), entry.getValue());
		tier1.put(ForgeDirection.EAST, east);

		final Map<BlockLocation, BlockRequirement> north = new HashMap<BlockLocation, BlockRequirement>();
		for(final Entry<BlockLocation, BlockRequirement> entry:AltarRegistry.fillTier1West().entrySet())
			north.put(entry.getKey().rotate(NumberHelper.COS_270, NumberHelper.SIN_270), entry.getValue());
		tier1.put(ForgeDirection.NORTH, north);

		AltarRegistry.multiblocks.put(1, tier1);
	}

	private static Map<BlockLocation, BlockRequirement> fillTier1West(){
		final Map<BlockLocation, BlockRequirement> west = new HashMap<BlockLocation, BlockRequirement>();
		final BlockRequirement offeringTable = new BlockRequirement(ModBlocks.offeringTable);
		west.put(new BlockLocation(0, 0, 0), offeringTable);
		west.put(new BlockLocation(0, 0, -1), offeringTable);
		BlockRequirement stone = new BlockRequirement(Material.rock, true);
		west.put(new BlockLocation(0, 0, 1), stone);
		west.put(new BlockLocation(0, 0, -2), stone);
		west.put(new BlockLocation(1, -1, -2), stone);
		west.put(new BlockLocation(1, -1, 1), stone);
		stone = new BlockRequirement(Material.rock);
		west.put(new BlockLocation(1, -2, 1), stone);
		west.put(new BlockLocation(0, -2, 1), stone);
		west.put(new BlockLocation(-1, -2, 1), stone);
		west.put(new BlockLocation(1, -2, 0), stone);
		west.put(new BlockLocation(0, -2, 0), stone);
		west.put(new BlockLocation(-1, -2, 0), stone);
		west.put(new BlockLocation(1, -2, -1), stone);
		west.put(new BlockLocation(0, -2, -1), stone);
		west.put(new BlockLocation(-1, -2, -1), stone);
		west.put(new BlockLocation(1, -2, -2), stone);
		west.put(new BlockLocation(0, -2, -2), stone);
		west.put(new BlockLocation(-1, -2, -2), stone);
		final BlockRequirement wood = new BlockRequirement(Material.wood);
		west.put(new BlockLocation(0, -1, 0), wood);
		west.put(new BlockLocation(0, -1, -1), wood);
		final BlockRequirement woodStairs = new BlockRequirement(Material.wood, BlockStairs.class);
		west.put(new BlockLocation(1, -1, 0), woodStairs);
		west.put(new BlockLocation(1, -1, -1), woodStairs);
		west.put(new BlockLocation(-1, -1, 0), woodStairs);
		west.put(new BlockLocation(-1, -1, -1), woodStairs);
		final BlockRequirement torch = new BlockRequirement(Blocks.torch);
		west.put(new BlockLocation(0, 1, 1), torch);
		west.put(new BlockLocation(0, 1, -2), torch);
		final BlockRequirement wall = new BlockRequirement(Material.rock, BlockWall.class, true);
		west.put(new BlockLocation(1, 0, 1), wall);
		west.put(new BlockLocation(1, 0, 0), wall);
		west.put(new BlockLocation(1, 0, -1), wall);
		west.put(new BlockLocation(1, 0, -2), wall);
		west.put(new BlockLocation(0, -1, 1), stone);
		west.put(new BlockLocation(0, -1, -2), stone);
		return west;
	}

}
