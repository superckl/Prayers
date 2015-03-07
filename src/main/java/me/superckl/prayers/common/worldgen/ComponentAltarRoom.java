package me.superckl.prayers.common.worldgen;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import me.superckl.prayers.api.AltarRegistry;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.altar.multi.BlockRequirement;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.utility.BlockLocation;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.common.util.ForgeDirection;

public class ComponentAltarRoom extends StructureVillagePieces.House1{

	private int averageGroundLevel = -1;

	public ComponentAltarRoom() {}

	public ComponentAltarRoom(final Start villagePiece, final int par2, final Random par3Random, final StructureBoundingBox sbb, final int coordBaseMode) {
		super();
		this.coordBaseMode = coordBaseMode;
		this.boundingBox = sbb;
	}

	public static ComponentAltarRoom buildComponent(final Start villagePiece, final List pieces, final Random random, final int x, final int y, final int z, final int coordBaseMode, final int p5) {
		final StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 5, 5, coordBaseMode);//TODO actual dimensions
		return Village.canVillageGoDeeper(box) && (StructureComponent.findIntersecting(pieces, box) == null) ? new ComponentAltarRoom(villagePiece, p5, random, box, coordBaseMode) : null;
	}

	@Override
	public boolean addComponentParts(final World world, final Random random, final StructureBoundingBox box) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, box);

			if (this.averageGroundLevel < 0)
				return true;

			this.boundingBox.offset(0, (this.averageGroundLevel - this.boundingBox.maxY) + 5, 0);
		}

		LogHelper.info("Genning at "+box.toString());
		ForgeDirection dir;
		switch(this.coordBaseMode){
		case 0:
		{
			dir = ForgeDirection.NORTH;
			break;
		}
		case 1:
		{
			dir = ForgeDirection.WEST;
			break;
		}
		case 2:
		{
			dir = ForgeDirection.SOUTH;
			break;
		}
		default:
		{
			dir = ForgeDirection.EAST;
			break;
		}
		}
		final BlockLocation base = new BlockLocation(this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ).shift(dir.getOpposite());
		final Map<BlockLocation, BlockRequirement> map = AltarRegistry.getMultiBlock(1, dir);
		LogHelper.info(dir+":"+map.size());
		BlockLocation loc = null;
		for(final Entry<BlockLocation, BlockRequirement> entry:map.entrySet()){
			final BlockRequirement req = entry.getValue();
			if(req.getBlock() == ModBlocks.offeringTable){
				loc = entry.getKey().add(base);
				loc.setBlock(world, ModBlocks.offeringTable);
			}else if(req.getBlock() == Blocks.torch)
				entry.getKey().add(base).setBlock(world, Blocks.torch);
			else if(req.getClazz() == BlockWall.class)
				entry.getKey().add(base).setBlock(world, Blocks.cobblestone_wall);
			else if(req.getClazz() == BlockStairs.class){
				final BlockLocation stair = entry.getKey().add(base);
				stair.setBlock(world, Blocks.oak_stairs);
				this.rotateStairs(world, stair, dir, !this.isStairInDirection(entry.getKey(), dir));
			}else if(req.getMaterial() == Material.wood)
				entry.getKey().add(base).setBlock(world, Blocks.planks);
			else if(req.getMaterial() == Material.rock)
				entry.getKey().add(base).setBlock(world, Blocks.stone);
		}
		if(loc == null){
			LogHelper.error("Failed to generate altar!");
			return true;
		}
		final TileEntityOfferingTable te = (TileEntityOfferingTable) loc.getTileEntity(world);
		final Altar al = new Altar(te);
		if(!al.determineBlocks(world))
			return true;
		al.setActivated(true);
		return true;
	}

	private boolean isStairInDirection(final BlockLocation loc, final ForgeDirection dir){
		if(dir.offsetX != 0)
			return loc.shift(dir.getOpposite()).getX() == 0;
		else if(dir.offsetZ != 0)
			return loc.shift(dir.getOpposite()).getZ() == 0;
		return false;
	}

	private void rotateStairs(final World world, final BlockLocation loc, final ForgeDirection to, final boolean back){
		if((to == ForgeDirection.UP) || (to == ForgeDirection.DOWN) || (to == ForgeDirection.UNKNOWN))
			return;
		int i = 0;
		ForgeDirection temp = ForgeDirection.WEST;
		while(temp != to){
			temp = temp.getRotation(ForgeDirection.UP);
			i++;
		}
		if(back)
			i += 2;
		for(;i > 0; i--)
			Blocks.oak_stairs.rotateBlock(world, loc.getX(), loc.getY(), loc.getZ(), ForgeDirection.UP);
		if(back)
			loc.setMeta(world, loc.getMeta(world)+4);
		//Flipping stairs upside down: They are technically facing the opposite direction, hence i += 2. The fourth bit (4) indicates the stair is upside down, hence meta+4;
	}

}
