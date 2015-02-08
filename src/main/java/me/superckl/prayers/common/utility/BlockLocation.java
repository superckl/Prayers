package me.superckl.prayers.common.utility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BlockLocation implements Cloneable{

	private final int x, y, z;

	public BlockLocation add(final int x, final int y, final int z){
		return new BlockLocation(this.x+x, this.y+y, this.z+z);
	}

	public BlockLocation add(final BlockLocation loc){
		return this.add(loc.x, loc.y, loc.z);
	}

	public BlockLocation subtract(final int x, final int y, final int z){
		return new BlockLocation(this.x-x, this.y-y, this.z-z);
	}

	public BlockLocation subtract(final BlockLocation loc){
		return this.subtract(loc.x, loc.y, loc.z);
	}

	public Block getBlock(final World world){
		return world.getBlock(this.x, this.y, this.z);
	}

	public int getMeta(final World world){
		return world.getBlockMetadata(this.x, this.y, this.z);
	}

	public TileEntity getTileEntity(final World world){
		return world.getTileEntity(this.x, this.y, this.z);
	}

	public BlockLocation setBlock(final World world, final Block block){
		world.setBlock(this.x, this.y, this.z, block);
		return this;
	}

	public BlockLocation setMeta(final World world, final int meta){
		world.setBlockMetadataWithNotify(this.x, this.y, this.z, meta, 3);
		return this;
	}

	public BlockLocation shift(final ForgeDirection dir){
		return this.add(dir.offsetX, dir.offsetY, dir.offsetZ);
	}

	public BlockLocation rotate(final double cos, final double sin){
		return new BlockLocation((int) Math.round((this.x*cos)+(this.z*sin)), this.y, (int) Math.round((-this.x*sin)+(this.z*cos)));
	}

	@Override
	public boolean equals(final Object o){
		if((o instanceof BlockLocation) == false)
			return false;
		final BlockLocation loc = (BlockLocation) o;
		return (loc.x == this.x) && (loc.y == this.y) && (loc.z == this.z);
	}

	@Override
	public int hashCode(){
		int hash = 1;
		final String octal = Integer.toOctalString(this.y);
		hash = (hash*17) + this.x;
		hash = ((hash*31)+octal.hashCode())^(octal.hashCode() >>> 32);
		hash = (hash*59) + this.z;
		return hash;
	}

	@Override
	public BlockLocation clone(){
		return new BlockLocation(this.x, this.y, this.z);
	}

	public static BlockLocation fromTileEntity(final TileEntity te){
		return new BlockLocation(te.xCoord, te.yCoord, te.zCoord);
	}

}
