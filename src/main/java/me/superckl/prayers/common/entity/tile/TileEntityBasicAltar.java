package me.superckl.prayers.common.entity.tile;

import java.lang.ref.WeakReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.prayer.IBuryable;
import me.superckl.prayers.common.prayer.IPrayerAltar;
import me.superckl.prayers.common.reference.ModFluids;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PCReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
	@Getter
	private ItemStack currentItem;
	//Not saved to NBT.
	@Getter
	private WeakReference<EntityPlayer> placingPlayer;
	@Getter
	private int waterTimer = 200;

	public TileEntityBasicAltar() {}

	public TileEntityBasicAltar(final boolean activated) {
		this.activated = activated;
	}

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		super.readFromNBT(comp);
		this.activated = comp.getBoolean("altarActivated");
		this.prayerPoints = comp.getFloat("prayerPoints");
		if(comp.hasKey("currentItem"))
			this.currentItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("currentItem"));
		this.waterTimer = comp.getInteger("waterTimer");
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		comp.setBoolean("altarActivated", this.activated);
		comp.setFloat("prayerPoints", this.prayerPoints);
		if(this.currentItem != null)
			comp.setTag("currentItem", this.currentItem.writeToNBT(new NBTTagCompound()));
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
		if(this.currentItem != null)
			if((this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && this.activated){
				if(this.prayerPoints >= 0.5F)
					this.waterTimer--;
				if(this.waterTimer <= 0){
					this.waterTimer = 200;
					this.currentItem = ModFluids.filledHolyBottle();
				}
			}
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

	@Override
	public boolean canBlessInstantly() {
		return false;
	}

	public void setCurrentItem(final ItemStack item, final EntityPlayer player){
		if(!this.isItemValid(item)){
			LogHelper.error("Incompatible object was placed on an altar! Call isItemValid before calling setCurrentItem! Calling class: "+PCReflectionHelper.retrieveCallingStackTraceElement().getClassName());
			return;
		}
		this.currentItem = item;
		this.placingPlayer = new WeakReference<EntityPlayer>(player);
	}

	public boolean isItemValid(final ItemStack item){
		if(item != null)
			if(!((item.getItem() == Items.potionitem) && (item.getItemDamage() == 0)))
				if(!((item.getItem() instanceof IBuryable) && this.activated))
					return false;
				else if((item.getItem() == ModItems.basicBone) && (item.getItemDamage() != 2))
					return false;
		return true;
	}

	public boolean isBlessingWater(){
		return this.activated && (this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && (this.prayerPoints >= 0.5F) && (this.waterTimer > 0);
	}

}
