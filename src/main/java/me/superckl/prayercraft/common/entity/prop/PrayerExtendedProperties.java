package me.superckl.prayercraft.common.entity.prop;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayercraft.common.prayer.Prayers;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.utility.PrayerHelper;
import me.superckl.prayercraft.network.MessageUpdatePrayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PrayerExtendedProperties implements IExtendedEntityProperties{

	@Getter
	private EntityPlayer player;
	@Getter
	private int nextXP;
	@Getter
	@Setter
	private List<Prayers> activePrayers = new ArrayList<Prayers>();
	private int tickDelay = 20;

	@Override
	public void saveNBTData(final NBTTagCompound compound) {
		compound.setTag("prayerData", this.writeToNBT());
	}

	@Override
	public void loadNBTData(final NBTTagCompound compound) {
		this.readFromNBT(compound.getCompoundTag("prayerData"));
	}

	public NBTTagCompound writeToNBT(){
		final NBTTagCompound comp = new NBTTagCompound();
		comp.setInteger("level", this.getPrayerLevel());
		comp.setFloat("points", this.getPrayerPoints());
		comp.setTag("activePrayers", PrayerHelper.toNBT(this.activePrayers));
		comp.setInteger("xp", this.getCurrentXP());
		return comp;
	}

	public void readFromNBT(final NBTTagCompound comp){
		this.setPrayerLevel(comp.getInteger("level"));
		this.setPrayerPoints(comp.getFloat("points"));
		this.activePrayers = PrayerHelper.fromNBT(comp.getCompoundTag("activePrayers"));
		this.setCurrentXP(comp.getInteger("xp"));
		this.nextXP = PrayerHelper.calculateXP(this.getPrayerLevel()+1);
	}

	@Override
	public void init(final Entity entity, final World world) {
		if(!(entity instanceof EntityPlayer))
			return;
		this.nextXP = PrayerHelper.calculateXP(2);
		this.player = (EntityPlayer) entity;
		this.player.getDataWatcher().addObject(27, new Integer(1));
		this.player.getDataWatcher().addObject(28, new Float(10F));
		this.player.getDataWatcher().addObject(29, new Integer(0));
	}

	public void playerTick(){
		if(this.activePrayers.isEmpty())
			return;
		this.tickDelay--;
		if(this.tickDelay <= 0){
			float points = this.getPrayerPoints();
			for(final Prayers prayer:this.activePrayers){
				points -= prayer.getDrain();
				if(points <= 0F){
					points = 0F;
					break;
				}
			}
			this.setPrayerPoints(points);
			if(points <= 0F)
				this.disableAllPrayers(true);
			this.tickDelay = 20;
		}
	}

	public void disableAllPrayers(final boolean syncClient){
		this.activePrayers.clear();
		if(syncClient && (this.player instanceof EntityPlayerMP))
			ModData.PRAYER_UPDATE_CHANNEL.sendTo(new MessageUpdatePrayers(this.activePrayers), (EntityPlayerMP) this.player);
	}

	public void addXP(final int xp){
		int newXP = this.getCurrentXP()+xp;
		if(newXP >= this.nextXP){
			newXP -= this.nextXP;
			this.setPrayerLevel(this.getPrayerLevel()+1);
			this.nextXP = PrayerHelper.calculateXP(this.getPrayerLevel()+1);
		}
		this.setCurrentXP(newXP);
	}

	public int getPrayerLevel(){
		return this.player.getDataWatcher().getWatchableObjectInt(27);
	}

	public void setPrayerLevel(final int level){
		this.player.getDataWatcher().updateObject(27, new Integer(level));
	}

	public float getPrayerPoints(){
		return this.player.getDataWatcher().getWatchableObjectFloat(28);
	}

	public void setPrayerPoints(final float points){
		this.player.getDataWatcher().updateObject(28, new Float(points));
	}

	public int getCurrentXP(){
		return this.player.getDataWatcher().getWatchableObjectInt(29);
	}

	public void setCurrentXP(final int xp){
		this.player.getDataWatcher().updateObject(29, new Integer(xp));
	}

}
