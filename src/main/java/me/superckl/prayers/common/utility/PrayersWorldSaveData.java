package me.superckl.prayers.common.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class PrayersWorldSaveData extends WorldSavedData{

	private final Map<UUID, NBTTagCompound> playerData = new HashMap<UUID, NBTTagCompound>();

	public PrayersWorldSaveData(final String id) {
		super(id);
	}

	public void storePlayerData(final EntityPlayer player, final NBTTagCompound compound){
		this.playerData.put(player.getGameProfile().getId(), compound);
		this.markDirty();
	}

	public NBTTagCompound getPlayerData(final EntityPlayer player, final boolean remove){
		if(remove){
			this.markDirty();
			return this.playerData.remove(player.getGameProfile().getId());
		}else
			return this.playerData.get(player.getGameProfile().getId());
	}

	public boolean hasDataForPlayer(final EntityPlayer player){
		return this.playerData.containsKey(player.getGameProfile().getId());
	}

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		final NBTTagCompound sub = comp.getCompoundTag("prayersextendedsave");
		final NBTTagList listUUID = sub.getTagList("UUIDs", NBT.TAG_STRING);
		final NBTTagList listNBT = sub.getTagList("NBTs", NBT.TAG_COMPOUND);
		for(int i = 0; i < listUUID.tagCount(); i++)
			this.playerData.put(UUID.fromString(listUUID.getStringTagAt(i)), listNBT.getCompoundTagAt(i));
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		final NBTTagList listUUID = new NBTTagList();
		final NBTTagList listNBT = new NBTTagList();
		for(final Entry<UUID, NBTTagCompound> entry:this.playerData.entrySet()){
			listUUID.appendTag(new NBTTagString(entry.getKey().toString()));
			listNBT.appendTag(entry.getValue().copy());
		}
		final NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("UUIDs", listUUID);
		compound.setTag("NBTs", listNBT);
		comp.setTag("prayersextendedsave", compound);
	}

}
