package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;

import net.minecraft.entity.item.ItemFrameEntity;

public class DecreeData {

	protected final WeakReference<ItemFrameEntity> ref;

	public DecreeData(final WeakReference<ItemFrameEntity> ref) {
		this.ref = ref;
	}

	public void setup() {

	}

	public void onRemove() {

	}

	public void tick() {

	}

}
