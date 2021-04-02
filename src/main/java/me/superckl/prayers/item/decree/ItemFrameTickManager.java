package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ItemFrameTickManager{

	public static final ItemFrameTickManager INSTANCE = new ItemFrameTickManager();

	private final Map<WeakReference<ItemFrameEntity>, DecreeData> frames = new IdentityHashMap<>();

	private ItemFrameTickManager() {}

	public void addTrackedEntity(final ItemFrameEntity entity, final ItemStack stack) {
		final WeakReference<ItemFrameEntity> wr =new WeakReference<>(entity);
		final DecreeData data = ((DecreeItem)stack.getItem()).getType().getDataSupplier().apply(wr);
		this.frames.put(wr, data);
		data.setup();
	}

	public void removeTrackedEntity(final ItemFrameEntity frame) {
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			if(entry.getKey().get() == frame) {
				final DecreeData data = entry.getValue();
				if(data != null)
					data.onRemove();
				it.remove();
				break;
			}
		}
	}

	public List<DecreeData> getDataForType(DecreeItem.Type type){
		this.purge();
		List<DecreeData> data = Lists.newArrayList();
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			if(((DecreeItem)entry.getKey().get().getItem().getItem()).getType() == type)
				data.add(entry.getValue());
		}
		return data;
	}
	
	private boolean isValid(final ItemFrameEntity entity) {
		return entity != null && entity.isAlive() && entity.isAddedToWorld() && entity.level != null && entity.level.isLoaded(entity.getPos()) && !entity.getItem().isEmpty()
				&& entity.getItem().getItem() instanceof DecreeItem;
	}
	
	//Helper method to ensure integrity of the stored data when needed
	private void purge() {
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			if(!this.isValid(entry.getKey().get())) {
				final DecreeData data = entry.getValue();
				if(data != null)
					data.onRemove();
				it.remove();
				break;
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(final WorldEvent.Unload e) {
		if(e.getWorld().isClientSide())
			return;
		final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
			final ItemFrameEntity entity = entry.getKey().get();
			if(entity == null || entity.level == e.getWorld()) {
				final DecreeData data = entry.getValue();
				if(data != null)
					data.onRemove();
				it.remove();
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(final WorldTickEvent e) {
		if(e.phase == Phase.END && e.side == LogicalSide.SERVER) {
			this.purge();
			final Iterator<Entry<WeakReference<ItemFrameEntity>, DecreeData>> it = this.frames.entrySet().iterator();
			while(it.hasNext()) {
				final Entry<WeakReference<ItemFrameEntity>, DecreeData> entry = it.next();
				if(entry.getKey().get().getRotation() == 0)
					entry.getValue().tick();
			}
		}
	}

	@SuppressWarnings("resource")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityInteract(final PlayerInteractEvent.EntityInteract e) {
		if(!e.getWorld().isClientSide && e.getTarget() instanceof ItemFrameEntity) {
			final ItemStack stack = e.getPlayer().getItemInHand(e.getHand());
			if(!stack.isEmpty() && stack.getItem() instanceof DecreeItem && ((ItemFrameEntity)e.getTarget()).getItem().isEmpty())
				this.addTrackedEntity((ItemFrameEntity) e.getTarget(), stack);
		}
	}

	@SuppressWarnings("resource")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(final EntityJoinWorldEvent e) {
		if(!e.getWorld().isClientSide && e.getEntity() instanceof ItemFrameEntity) {
			final ItemFrameEntity entity = (ItemFrameEntity) e.getEntity();
			final ItemStack stack = entity.getItem();
			if(!stack.isEmpty() && stack.getItem() instanceof DecreeItem)
				this.addTrackedEntity(entity, entity.getItem());
		}
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public void onEntityLeaveWorld(final EntityLeaveWorldEvent e) {
		if(!e.getWorld().isClientSide && e.getEntity() instanceof ItemFrameEntity)
			this.removeTrackedEntity((ItemFrameEntity) e.getEntity());
	}

}
