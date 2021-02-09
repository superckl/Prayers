package me.superckl.prayers.block;

import me.superckl.prayers.ModTiles;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAltar extends TileEntity implements ITickableTileEntity{

	private final AltarTypes type;

	public TileEntityAltar(final AltarTypes type) {
		super(ModTiles.ALTARS.get(type).get());
		this.type = type;
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		super.onLoad();
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
