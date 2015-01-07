package me.superckl.prayers.common.prayer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.utility.CalculationEffectType;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AltarRegistry {

	private static final Map<Block, AltarBlockInfo> registeredBlocks = new HashMap<Block, AltarBlockInfo>();
	@Getter
	private static final Set<WeakReference<Altar>> loadedAltars = new HashSet<WeakReference<Altar>>();

	static{
		AltarRegistry.registeredBlocks.put(ModBlocks.altarBase, new AltarBlockInfo(true, true, CalculationEffectType.FIRST_ADDITION, CalculationEffectType.FIRST_ADDITION, 0F, 0F));
	}

	public static boolean isBlockRegistered(final Block block){
		return AltarRegistry.registeredBlocks.containsKey(block);
	}

	public static AltarBlockInfo getBlockInfo(final Block block){
		return AltarRegistry.registeredBlocks.get(block);
	}

	public static void registerBlock(final Block block, final AltarBlockInfo info){
		if(AltarRegistry.registeredBlocks.containsKey(block))
			LogHelper.warn(StringHelper.build("Class "+PSReflectionHelper.retrieveCallingStackTraceElement().getClassName(), " has re-registered an already registered altar block. If this is not an intentional override, there are mod conflictions!"));
		AltarRegistry.registeredBlocks.put(block, info);
	}

	public static Set<Block> getRegisteredBlocks(){
		return AltarRegistry.registeredBlocks.keySet();
	}

	public static Map<Block, AltarBlockInfo> getAllEntries(){
		return AltarRegistry.registeredBlocks;
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
			for(final Vec3 vec:altar.getBlocks())
				if((((int) vec.xCoord) == x) && (((int) vec.yCoord) == y) && (((int) vec.zCoord) == z))
					return altar;
		}
		return null;
	}

}
