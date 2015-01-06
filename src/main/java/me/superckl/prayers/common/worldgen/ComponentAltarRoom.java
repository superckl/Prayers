package me.superckl.prayers.common.worldgen;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;

public class ComponentAltarRoom extends StructureVillagePieces.Village{

	public ComponentAltarRoom() {}

	public ComponentAltarRoom(final Start villagePiece, final int par2, final Random par3Random, final StructureBoundingBox sbb, final int coordBaseMode) {
		super();
		this.coordBaseMode = coordBaseMode;
		this.boundingBox = sbb;
	}

	public static ComponentAltarRoom buildComponent(final Start villagePiece, final List pieces, final Random random, final int x, final int y, final int z, final int coordBaseMode, final int p5) {
		final StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 11, 6, 11, coordBaseMode);//TODO actual dimensions
		return Village.canVillageGoDeeper(box) && (StructureComponent.findIntersecting(pieces, box) == null) ? new ComponentAltarRoom(villagePiece, p5, random, box, coordBaseMode) : null;
	}

	@Override
	public boolean addComponentParts(final World p_74875_1_, final Random p_74875_2_,
			final StructureBoundingBox p_74875_3_) {
		//TODO actual gen
		return false;
	}

}
