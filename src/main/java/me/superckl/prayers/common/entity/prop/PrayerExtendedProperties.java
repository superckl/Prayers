package me.superckl.prayers.common.entity.prop;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModPotions;
import me.superckl.prayers.common.utility.PrayerHelper;
import me.superckl.prayers.network.MessageUpdatePrayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;

public class PrayerExtendedProperties implements IExtendedEntityProperties{

	@Getter
	private EntityPlayer player;
	@Getter
	@Setter
	private EnumSet<EnumPrayers> activePrayers = EnumSet.noneOf(EnumPrayers.class);
	@Getter
	private final List<String> unlockedPrayers = new ArrayList<String>();
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
		comp.setFloat("maxPoints", this.getBaseMaxPrayerPoints());
		comp.setFloat("points", this.getPrayerPoints());
		comp.setTag("activePrayers", PrayerHelper.toNBT(this.activePrayers));
		final NBTTagList list = new NBTTagList();
		for(final String id:this.unlockedPrayers)
			list.appendTag(new NBTTagString(id));
		comp.setTag("unlocked", list);
		return comp;
	}

	public void readFromNBT(final NBTTagCompound comp){
		this.setBaseMaxPrayerPoints(comp.getFloat("maxPoints"));
		this.setPrayerPoints(comp.getFloat("points"));
		this.activePrayers = PrayerHelper.fromNBT(comp.getCompoundTag("activePrayers"));
		final NBTTagList list = comp.getTagList("unlocked", NBT.TAG_STRING);
		for(int i = 0; i < list.tagCount(); i++)
			this.unlockedPrayers.add(list.getStringTagAt(i));
	}

	@Override
	public void init(final Entity entity, final World world) {
		if(!(entity instanceof EntityPlayer))
			return;
			this.player = (EntityPlayer) entity;
			this.player.getDataWatcher().addObject(27, new Float(10F));
			this.player.getDataWatcher().addObject(28, new Float(this.getMaxPrayerPoints()));
	}

	public void playerTick(){
		if(this.activePrayers.isEmpty())
			return;
		this.tickDelay--;
		if(this.tickDelay <= 0){
			float points = this.getPrayerPoints();
			for(final EnumPrayers prayer:this.activePrayers){
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
		if(syncClient && (this.player instanceof EntityPlayerMP)){
			final NBTTagCompound comp = new NBTTagCompound();
			this.saveNBTData(comp);
			ModData.PRAYER_UPDATE_CHANNEL.sendTo(new MessageUpdatePrayers(comp), (EntityPlayerMP) this.player);
		}
	}

	public float getMaxPrayerPoints(){
		return this.getBaseMaxPrayerPoints()+((this.player.activePotionsMap != null) && this.player.isPotionActive(ModPotions.prayerBoost) ? (this.player.getActivePotionEffect(ModPotions.prayerBoost).getAmplifier()+1)*200F:0F);
	}

	public float getBaseMaxPrayerPoints(){
		return this.player.getDataWatcher().getWatchableObjectFloat(27);
	}

	public void setBaseMaxPrayerPoints(final float points){
		this.player.getDataWatcher().updateObject(27, new Float(points));
	}

	public float getPrayerPoints(){
		return this.player.getDataWatcher().getWatchableObjectFloat(28);
	}

	public void setPrayerPoints(final float points){
		this.player.getDataWatcher().updateObject(28, new Float(points));
	}

}
