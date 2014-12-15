package me.superckl.prayers.common.entity.tile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.prayer.IPrayerAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	@Getter
	@Setter
	private float prayerPoints = 500F;

	public TileEntityBasicAltar() {}

	public TileEntityBasicAltar(final boolean activated) {
		this.activated = activated;
	}

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		super.readFromNBT(comp);
		this.activated = comp.getBoolean("altarActivated");
		this.prayerPoints = comp.getFloat("prayerPoints");
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		comp.setBoolean("altarActivated", this.activated);
		comp.setFloat("prayerPoints", this.prayerPoints);
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

	@Override
	public boolean canBlessWater() {
		return this.activated;
	}

	@Override
	public float getMaxPrayerPoints() {
		return 500F;
	}

	private int regenTimer = 200;

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(this.getPrayerPoints() < this.getMaxPrayerPoints()){
			this.regenTimer--;
			if(this.regenTimer <= 0){
				this.regenTimer = 200;
				this.prayerPoints += 1F;
				if(this.prayerPoints > this.getMaxPrayerPoints())
					this.prayerPoints = this.getMaxPrayerPoints();
			}
		}else
			this.regenTimer = 200;
	}

	@Override
	public float onRechargePlayer(float points, final EntityPlayer player, final boolean shouldSubtract) {
		if(points > this.prayerPoints)
			points = this.prayerPoints;
		if(shouldSubtract)
			this.prayerPoints -= points;
		return points;
	}

	@Override
	public float getOfferXPBoost(final ItemStack stack) {
		return 1.5F;
	}

}
