package me.superckl.prayers.block;

import me.superckl.prayers.ModTiles;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAltar extends TileEntity implements ITickableTileEntity{

	public TileEntityAltar() {
		super(ModTiles.ALTAR_BLOCK.get());
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public CompoundNBT write(final CompoundNBT compound) {
		return super.write(compound);
	}

	@Override
	public void read(final BlockState state, final CompoundNBT nbt) {
		super.read(state, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.read(state, tag);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return super.getUpdatePacket();
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
	}

}
