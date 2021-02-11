package me.superckl.prayers.world;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import me.superckl.prayers.Prayers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class AltarsSavedData extends WorldSavedData{

	private static AltarsSavedData INSTANCE;
	public static String DATA_NAME = Prayers.MOD_ID+"_altars";

	public static String ALTAR_DATA_KEY = "altar_data";
	public static String PLAYER_ID_KEY = "player_id";
	public static String BLOCK_POS_KEY = "position";
	public static String PENDING_XP_KEY = "pending_xp";
	public static String XP_KEY = "xp";

	private final Map<UUID, BlockPos> ownedAltars = Maps.newHashMap();
	private final Object2FloatMap<UUID> pendingXP = new Object2FloatOpenHashMap<>();

	private AltarsSavedData() {
		super(AltarsSavedData.DATA_NAME);
		if(AltarsSavedData.INSTANCE != null)
			throw new IllegalStateException("Saved data has already been created!");
		AltarsSavedData.INSTANCE = this;
	}

	public boolean ownsAltar(final UUID id) {
		return this.ownedAltars.containsKey(id);
	}

	public BlockPos setAltar(final UUID id, final BlockPos pos) {
		this.markDirty();
		return this.ownedAltars.put(id, pos.toImmutable());
	}

	public BlockPos removeAltar(final UUID id) {
		this.markDirty();
		return this.ownedAltars.remove(id);
	}

	public boolean hasPendingXP(final UUID id) {
		return this.pendingXP.containsKey(id);
	}

	public float getAndRemoveXP(final UUID id) {
		return this.pendingXP.removeFloat(id);
	}

	public void addPendingXP(final UUID id, final float xp) {
		if(this.hasPendingXP(id))
			this.pendingXP.put(id, this.pendingXP.getFloat(id)+xp);
		else
			this.pendingXP.put(id, xp);
	}

	@Override
	public void read(final CompoundNBT nbt) {
		final ListNBT altars = nbt.getList(AltarsSavedData.ALTAR_DATA_KEY, Constants.NBT.TAG_COMPOUND);
		altars.forEach(subNBT -> {
			final CompoundNBT playerData = (CompoundNBT) subNBT;
			final UUID playerID = playerData.getUniqueId(AltarsSavedData.PLAYER_ID_KEY);
			final int[] posData = playerData.getIntArray(AltarsSavedData.BLOCK_POS_KEY);
			this.ownedAltars.put(playerID, new BlockPos(posData[0], posData[1], posData[2]));
		});
		final ListNBT pending = nbt.getList(AltarsSavedData.PENDING_XP_KEY, Constants.NBT.TAG_COMPOUND);
		pending.forEach(subNBT -> {
			final CompoundNBT playerData = (CompoundNBT) subNBT;
			final UUID playerID = playerData.getUniqueId(AltarsSavedData.PLAYER_ID_KEY);
			final float xp = playerData.getFloat(AltarsSavedData.XP_KEY);
			this.pendingXP.put(playerID, xp);
		});
	}

	@Override
	public CompoundNBT write(final CompoundNBT nbt) {
		final ListNBT altars = new ListNBT();
		this.ownedAltars.forEach((id, pos) -> {
			final CompoundNBT playerData = new CompoundNBT();
			playerData.putUniqueId(AltarsSavedData.PLAYER_ID_KEY, id);
			playerData.putIntArray(AltarsSavedData.BLOCK_POS_KEY, new int[] {pos.getX(), pos.getY(), pos.getZ()});
			altars.add(playerData);
		});
		nbt.put(AltarsSavedData.ALTAR_DATA_KEY, altars);
		final ListNBT pending = new ListNBT();
		this.pendingXP.forEach((id, xp) -> {
			final CompoundNBT playerData = new CompoundNBT();
			playerData.putUniqueId(AltarsSavedData.PLAYER_ID_KEY, id);
			playerData.putFloat(AltarsSavedData.XP_KEY, xp);
			pending.add(playerData);
		});
		nbt.put(AltarsSavedData.PENDING_XP_KEY, pending);
		return nbt;
	}

	/**
	 * Gets the saved data from the world, creating a new one if it does not exist.
	 * Once the data has been retrieved once, it is stored statically and used as the default
	 * for future creations. This ensures only one instance of the saved data exists and is
	 * the same for all worlds. The overworld data should be retrieved during server starting to
	 * ensure it is always the first to load.
	 * @param world The world from which to retrieve the saved data
	 * @return The singular saved data instance
	 */
	public static AltarsSavedData get(final ServerWorld world) {
		return  world.getSavedData().getOrCreate(() -> AltarsSavedData.INSTANCE == null ? new AltarsSavedData() : AltarsSavedData.INSTANCE, AltarsSavedData.DATA_NAME);
	}

}
