package me.superckl.prayers.common.entity.tile;

import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.prayer.Altar;
import me.superckl.prayers.common.reference.ModFluids;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityOfferingTable extends TileEntity{

	@Getter
	private ItemStack currentItem;
	//Not saved to NBT.
	@Getter
	private WeakReference<EntityPlayer> placingPlayer;
	private int[] masterLoc;
	@Setter
	private Altar altar;
	@Getter
	private int waterTimer = 200;

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		super.readFromNBT(comp);
		if(comp.hasKey("currentItem"))
			this.currentItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("currentItem"));
		else
			this.currentItem = null;
		if(comp.hasKey("altar")){
			this.altar = new Altar(this);
			this.altar.readFromNBT(comp.getCompoundTag("altar"));
		}else
			this.altar = null;
		this.waterTimer = comp.getInteger("waterTimer");
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		if(this.currentItem != null)
			comp.setTag("currentItem", this.currentItem.writeToNBT(new NBTTagCompound()));
		if(this.altar != null){
			final NBTTagCompound tag = new NBTTagCompound();
			this.altar.writeToNBT(tag);
			comp.setTag("altar", tag);
		}
		comp.setInteger("waterTimer", this.waterTimer);
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
	public void updateEntity() {
		if(this.isMaster())
			this.altar.updateEntity(this.worldObj);
	}

	private void manageWaterBless(){
		Altar altar;
		if((this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && ((altar = this.getAltar()) != null) && altar.isActivated()){
			if(altar.getPrayerPoints() >= 0.5F){
				this.waterTimer--;
				altar.setPrayerPoints(altar.getPrayerPoints()-0.5F);
			}
			if(this.waterTimer <= 0){
				this.waterTimer = 200;
				this.currentItem = ModFluids.filledHolyBottle();
			}
		}
	}

	public boolean isBlessingWater(){
		return (this.getAltar() != null) && this.getAltar().isActivated() && (this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && (this.getAltar().getPrayerPoints() >= 0.5F) && (this.waterTimer > 0);
	}

	public Altar getAltar(){
		TileEntityOfferingTable master;
		if(this.isMaster())
			return this.altar;
		else if((master = this.getMaster()) != null)
			return master.getAltar();
		return null;
	}

	public boolean isMaster(){
		return this.altar != null;
	}

	public TileEntityOfferingTable getMaster(){
		if(this.masterLoc != null){
			final TileEntity te = this.worldObj.getTileEntity(this.masterLoc[0], this.masterLoc[1], this.masterLoc[2]);
			if((te != null) && (te instanceof TileEntityOfferingTable))
				return (TileEntityOfferingTable) te;
		}
		return null;
	}

	public void onStructureInvalidated(){
		this.waterTimer = 200;
		this.altar = null;
		this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public void setCurrentItem(final ItemStack item, final EntityPlayer player){
		this.currentItem = item;
		this.placingPlayer = new WeakReference<EntityPlayer>(player);
		this.waterTimer = 200;
	}

}
