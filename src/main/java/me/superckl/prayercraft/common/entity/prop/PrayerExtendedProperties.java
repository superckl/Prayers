package me.superckl.prayercraft.common.entity.prop;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PrayerExtendedProperties implements IExtendedEntityProperties{

	@Getter
	private EntityPlayer player;

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
		return comp;
	}

	public void readFromNBT(final NBTTagCompound comp){
		this.setPrayerLevel(comp.getInteger("level"));
		this.setPrayerPoints(comp.getFloat("points"));
		this.setActivePrayers(comp.getInteger("activePrayers"));
	}

	@Override
	public void init(final Entity entity, final World world) {
		if(!(entity instanceof EntityPlayer))
			return;
		this.player = (EntityPlayer) entity;
		this.player.getDataWatcher().addObject(27, new Integer(1));
		this.player.getDataWatcher().addObject(28, new Float(100));
		this.player.getDataWatcher().addObject(29, new Integer(0));
	}

	public void playerTick(){

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

}
