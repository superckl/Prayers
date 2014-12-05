package me.superckl.prayercraft.common.entity.tile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayercraft.common.prayer.IPrayerAltar;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

@AllArgsConstructor
public class TileEntityBasicAltar extends TileEntity implements IPrayerAltar{

	@Getter
	@Setter
	private boolean activated;

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		super.readFromNBT(comp);
		this.activated = comp.getBoolean("altarActivated");
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		comp.setBoolean("altarActivated", this.activated);
	}

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound comp = new NBTTagCompound();
		this.writeToNBT(comp);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, comp);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}



}
