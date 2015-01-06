package me.superckl.prayers.common.worldgen;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class PrayersVillageCreationHandler implements IVillageCreationHandler{

	@Override
	public PieceWeight getVillagePieceWeight(final Random random, final int i) {
		return new PieceWeight(this.getComponentClass(), 1, 1);
	}

	@Override
	public Class<?> getComponentClass() {
		return ComponentAltarRoom.class;
	}

	@Override
	public Object buildComponent(final PieceWeight villagePiece, final Start startPiece, final List pieces, final Random random, final int x, final int y, final int z, final int coordBaseMode, final int p5) {
		return ComponentAltarRoom.buildComponent(startPiece, pieces, random, x, y, z, coordBaseMode, p5);
	}

}
