package me.superckl.prayercraft.common.entity.prop;

import lombok.Getter;
import me.superckl.prayercraft.common.utility.PrayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PrayerExtendedProperties implements IExtendedEntityProperties{

	@Getter
	private EntityPlayer player;
	@Getter
	private int nextXP;

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
		comp.setInteger("activePrayers", this.getActivePrayers());
		comp.setInteger("xp", this.getCurrentXP());
		return comp;
	}

	public void readFromNBT(final NBTTagCompound comp){
		this.setPrayerLevel(comp.getInteger("level"));
		this.setPrayerPoints(comp.getFloat("points"));
		this.setActivePrayers(comp.getInteger("activePrayers"));
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
		this.player.getDataWatcher().addObject(28, new Float(100F));
		this.player.getDataWatcher().addObject(29, new Integer(0));
		this.player.getDataWatcher().addObject(30, new Integer(0));
	}

	public void playerTick(){

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

	public int getActivePrayers(){
		return this.player.getDataWatcher().getWatchableObjectInt(29);
	}

	public void setActivePrayers(final int prayers){
		this.player.getDataWatcher().updateObject(29, new Integer(prayers));
	}

	public int getCurrentXP(){
		return this.player.getDataWatcher().getWatchableObjectInt(30);
	}

	public void setCurrentXP(final int xp){
		this.player.getDataWatcher().updateObject(30, new Integer(xp));
	}

}
