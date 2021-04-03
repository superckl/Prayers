package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.superckl.prayers.Prayers;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;

public class PersistenceDecreeData extends DecreeData{

	private Set<ChunkPos> loadedPositions;

	public PersistenceDecreeData(final WeakReference<ItemFrameEntity> ref) {
		super(ref);
	}

	@Override
	public void onSetup() {
		final ItemFrameEntity entity = this.ref.get();
		final Stream<ChunkPos> positions = ChunkPos.rangeClosed(new ChunkPos(entity.xChunk-1, entity.zChunk-1), new ChunkPos(entity.xChunk-1, entity.zChunk-1));
		this.loadedPositions = positions.collect(Collectors.toSet());
		this.loadedPositions.forEach(pos -> ForgeChunkManager.forceChunk((ServerWorld) entity.level, Prayers.MOD_ID, entity, pos.x, pos.z, true, true));
	}

	@Override
	public void onRemove() {
		final ItemFrameEntity entity = this.ref.get();
		if(this.loadedPositions != null)
			this.loadedPositions.forEach(pos -> ForgeChunkManager.forceChunk((ServerWorld) entity.level, Prayers.MOD_ID, entity, pos.x, pos.z, false, true));
		this.loadedPositions = null;
	}

}
