package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;

import lombok.Getter;
import net.minecraft.entity.item.ItemFrameEntity;

public class DecreeData {

	protected final WeakReference<ItemFrameEntity> ref;
	@Getter
	private boolean hasSetup;

	public DecreeData(final WeakReference<ItemFrameEntity> ref) {
		this.ref = ref;
	}

	public final void setup() {
		if(this.hasSetup)
			throw new IllegalStateException("Decree data has already been setup!");
		this.hasSetup = true;
		this.onSetup();
	}

	protected void onSetup() {

	}

	public void onRemove() {

	}

	public void tick() {

	}

}
