package me.superckl.prayers.common.entity.tile;

import java.lang.ref.WeakReference;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
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

public class TileEntityBasicAltar extends TileEntity implements IPrayerAltar{

	/**
	 * Only to be used on the server side.
	 */
	private final Random random = new Random();
	@Getter
	@Setter
	private boolean activated;
	@Getter
	private boolean inRitual;
	@Getter
	@Setter
	private int ritualTimer = 72000; //3 day-night cycles
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
	@Getter
	private int boneTimer = 40;

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
		else
			this.currentItem = null;
		this.inRitual = comp.getBoolean("inRitual");
		this.ritualTimer = comp.getInteger("ritualTimer");
		this.waterTimer = comp.getInteger("waterTimer");
		this.boneTimer = comp.getInteger("boneTimer");
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		comp.setBoolean("altarActivated", this.activated);
		comp.setFloat("prayerPoints", this.prayerPoints);
		if(this.currentItem != null)
			comp.setTag("currentItem", this.currentItem.writeToNBT(new NBTTagCompound()));
		comp.setBoolean("inRitual", this.inRitual);
		comp.setInteger("ritualTimer", this.ritualTimer);
		comp.setInteger("waterTimer", this.waterTimer);
		comp.setInteger("boneTimer", this.boneTimer);
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
		this.manageWaterBless();
		this.manageBoneOffer();
		if(this.inRitual && !this.getWorldObj().isRemote)
			this.manageRitual();
	}

	private void manageWaterBless(){
		if((this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && this.activated){
			if(this.prayerPoints >= 0.5F){
				this.waterTimer--;
				this.prayerPoints -= 0.5F;
			}
			if(this.waterTimer <= 0){
				this.waterTimer = 200;
				this.currentItem = ModFluids.filledHolyBottle();
			}
		}
	}

	private void manageBoneOffer(){
		if((this.currentItem != null) && (this.currentItem.getItem() instanceof IBuryable))
			if(this.activated){
				this.boneTimer--;
				if(this.boneTimer <= 0){
					this.boneTimer = 40;
					if(this.placingPlayer.get() != null){
						final IBuryable bury = (IBuryable) this.currentItem.getItem();
						final PrayerExtendedProperties prop = (PrayerExtendedProperties) this.placingPlayer.get().getExtendedProperties("prayer");
						prop.addXP((int) (bury.getXPFromStack(this.currentItem)*this.getOfferXPBoost(this.currentItem)));
					}
					this.currentItem = null;
				}
			}else if(!this.inRitual && (this.currentItem.getItem() == ModItems.basicBone) && (this.currentItem.getItemDamage() == 3))
				this.inRitual = true;
	}

	/**
	 * Only to be called on the server side
	 */
	private void manageRitual(){
		if(this.activated){
			this.inRitual = false;
			this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			return;
		}
		if(this.ritualTimer <= 0){
			this.activated = true;
			this.inRitual = false;
			this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			//TODO effect
			return;
		}
		this.ritualTimer--;
		if((this.currentItem != null) && (this.currentItem.getItem() == ModItems.basicBone) && (this.currentItem.getItemDamage() == 3)){
			if(this.currentItem.hasTagCompound())
				if(this.currentItem.getTagCompound().getBoolean("soaked")){
					this.ritualTimer -= this.random.nextInt(2);
					if(this.random.nextInt(2400) == 0){
						this.currentItem.setTagCompound(null);
						this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
						this.getWorldObj().spawnParticle("largesmoke", this.xCoord+.5D, this.yCoord+1.2D, this.zCoord+.5D, 0D, 0D, 0D);
						this.getWorldObj().spawnParticle("largesmoke", this.xCoord+.5D, this.yCoord+.8D, this.zCoord+.5D, 0D, 0D, 0D);
						this.getWorldObj().playSoundEffect(this.xCoord + 0.5F, this.yCoord + 0.5F, this.zCoord + 0.5F, "random.fizz", 0.5F, 2.6F + ((this.random.nextFloat() - this.random.nextFloat()) * 0.8F));
					}
				}
			if(this.random.nextInt(7000) == 0){
				this.currentItem = null;
				LogHelper.info("Removed bones");
				this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				this.getWorldObj().playSoundEffect(this.xCoord+0.5F, this.yCoord+0.5F, this.zCoord+0.5F, "mob.endermen.portal", 1.0F, 1.0F);
				for (int l = 0; l < 4; ++l)
				{
					final double d0 = this.xCoord + this.random.nextFloat();
					final double d1 = this.yCoord + this.random.nextFloat();
					final double d2 = this.zCoord + this.random.nextFloat();
					double d3 = 0.0D;
					double d4 = 0.0D;
					double d5 = 0.0D;
					final int i1 = (this.random.nextInt(2) * 2) - 1;
					d3 = (this.random.nextFloat() - 0.5D) * 0.5D;
					d4 = (this.random.nextFloat() - 0.5D) * 0.5D;
					d5 = (this.random.nextFloat() - 0.5D) * 0.5D;

					this.getWorldObj().spawnParticle("portal", d0, d1, d2, d3, d4, d5);
				}
			}
		}else
			this.ritualTimer += 1+(this.random.nextInt(9)/8);
		if(this.ritualTimer >= 100000){
			this.inRitual = false;
			this.ritualTimer = 72000;
			this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
		if((this.ritualTimer % 40) == 0)
			this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord); //TODO Meh... It only happens during the ritual I guess...
	}

	@Override
	public float onRechargePlayer(float points, final EntityPlayer player, final boolean shouldSubtract) {
		if(!this.activated)
			return 0F;
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
		this.waterTimer = 200;
		this.boneTimer = 40;
	}

	public boolean isItemValid(final ItemStack item){
		if(item != null)
			if(!((item.getItem() == Items.potionitem) && (item.getItemDamage() == 0)))
				if((item.getItem() == ModItems.basicBone) && (item.getItemDamage() == 3))
					return true;
				else if(!((item.getItem() instanceof IBuryable) && this.activated))
					return false;
		return true;
	}

	public boolean isBlessingWater(){
		return this.activated && (this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && (this.prayerPoints >= 0.5F) && (this.waterTimer > 0);
	}

	public boolean isOfferingBones(){
		return (this.currentItem != null) && (this.currentItem.getItem() == ModItems.basicBone) && this.activated && (this.boneTimer > 0);
	}

}
