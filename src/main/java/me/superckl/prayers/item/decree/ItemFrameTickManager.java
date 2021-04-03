package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ItemFrameTickManager{

	public static final ItemFrameTickManager INSTANCE = new ItemFrameTickManager();

	//Map of item frames to their active decree data. WeakReferences are VERY important here, since
	//complete entity tracking is essentially impossible. Frames are not removed from this map when
	//world, chunks, etc. unload. Rather, they are removed during ticking when either the decree is
	//no longer active or the weak reference expires (the entity has been forgotten by Minecraft).
	private final Map<WeakReference<ItemFrameEntity>, DecreeData> frames = new IdentityHashMap<>();
	//Set of pending frames that seem like they will be valid, but we couldn't be completely sure
	private final Set<WeakReference<ItemFrameEntity>> candidates = Sets.newConcurrentHashSet();

	private ItemFrameTickManager() {}

	public void addTrackedEntity(final ItemFrameEntity entity) {
		final WeakReference<ItemFrameEntity> wr =new WeakReference<>(entity);
		final DecreeData data = ((DecreeItem)entity.getItem().getItem()).getType().getDataSupplier().apply(wr);
		this.frames.put(wr, data);
	}

	public void removeTrackedEntity(final ItemFrameEntity frame) {
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			if(entry.getKey().get() == frame) {
				this.notifyRemove(entry.getValue());
				it.remove();
				break;
			}
		}
	}

	public boolean isTracked(final ItemFrameEntity entity) {
		this.purge();
		final Iterator<WeakReference<ItemFrameEntity>> it = this.frames.keySet().iterator();
		while(it.hasNext())
			if(it.next().get() == entity)
				return true;
		return false;
	}

	public List<DecreeData> getDataForType(final DecreeItem.Type type){
		this.purge();
		final List<DecreeData> data = Lists.newArrayList();
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			if(((DecreeItem)entry.getKey().get().getItem().getItem()).getType() == type)
				data.add(entry.getValue());
		}
		return data;
	}

	private boolean canTick(final ItemFrameEntity entity) {
		return entity != null && entity.isAlive() && entity.level != null && entity.level.isLoaded(entity.getPos());
	}

	public boolean hasActiveDecree(final ItemFrameEntity entity, final boolean checkRot) {
		return entity != null && (!checkRot || entity.getRotation() == 0) && !entity.getItem().isEmpty() && entity.getItem().getItem() instanceof DecreeItem;
	}

	//Helper method to ensure integrity of the stored data when needed
	private void purge() {
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			//Note that we do not purge based on loaded chunks, simply stop ticking the frame
			//Entities are hard to track since they are not necessarily "forgotten" when a chunk stops ticking
			if(!this.hasActiveDecree(entry.getKey().get(), true)) {
				this.notifyRemove(entry.getValue());
				it.remove();
			}
		}
	}

	private void notifyRemove(final DecreeData data) {
		if(data != null && data.isHasSetup())
			data.onRemove();
	}

	@SubscribeEvent
	public void onWorldTick(final WorldTickEvent e) {
		if(e.phase == Phase.END && e.side == LogicalSide.SERVER) {
			//Add any pending candidates before we tick
			for(final WeakReference<ItemFrameEntity> wr:this.candidates)
				if(this.hasActiveDecree(wr.get(), true))
					this.addTrackedEntity(wr.get());
			this.candidates.clear();

			//Purge invalid frames and then tick
			this.purge();
			final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
			while(it.hasNext()) {
				final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
				if(!entry.getValue().isHasSetup())
					entry.getValue().setup();
				//Only tick if the chunk is loaded
				if(this.canTick(entry.getKey().get()))
					entry.getValue().tick();
			}
		}
	}

	//Find new item frames from putting items in and track rotations
	@SuppressWarnings("resource")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityInteract(final PlayerInteractEvent.EntityInteract e) {
		if(!e.getWorld().isClientSide && e.getTarget() instanceof ItemFrameEntity) {
			final ItemFrameEntity entity = (ItemFrameEntity) e.getTarget();
			final ItemStack stack = e.getPlayer().getItemInHand(e.getHand());
			if(!stack.isEmpty() && stack.getItem() instanceof DecreeItem && entity.getItem().isEmpty())
				this.candidates.add(new WeakReference<>(entity));
			else if(this.hasActiveDecree(entity, false))
				if(entity.getRotation() == 0) {
					//The entity is probably going to be rotated and deactivated
					//call onRemove and make a fresh instance that will be checked for validity next tick
					//in case it isn't rotated
					this.removeTrackedEntity(entity);
					this.candidates.add(new WeakReference<>(entity));
				}else if(entity.getRotation() == 7)
					//The entity is probably going to rotated and activated
					//Add a fresh instance to be setup next tick
					this.candidates.add(new WeakReference<>(entity));
		}
	}

	//Find new item frames entering the world
	@SuppressWarnings("resource")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(final EntityJoinWorldEvent e) {
		if(!e.getWorld().isClientSide && e.getEntity() instanceof ItemFrameEntity) {
			final ItemFrameEntity entity = (ItemFrameEntity) e.getEntity();
			if(this.hasActiveDecree(entity, true))
				this.addTrackedEntity(entity);
		}
	}

	//Cleanup method to remove invalid frames outside of purging
	@SuppressWarnings("resource")
	@SubscribeEvent
	public void onEntityLeaveWorld(final EntityLeaveWorldEvent e) {
		if(!e.getWorld().isClientSide && e.getEntity() instanceof ItemFrameEntity)
			this.removeTrackedEntity((ItemFrameEntity) e.getEntity());
	}

}
