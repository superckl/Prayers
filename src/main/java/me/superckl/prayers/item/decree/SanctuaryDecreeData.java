package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;

import me.superckl.prayers.Config;
import me.superckl.prayers.item.decree.DecreeItem.Type;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SanctuaryDecreeData extends DecreeData{

	public SanctuaryDecreeData(final WeakReference<ItemFrameEntity> ref) {
		super(ref);
	}

	public boolean isAffected(final BlockPos pos) {
		return this.ref.get().getRotation() == 0 && pos.distSqr(this.ref.get().getPos()) <= MathHelper.square(Config.getInstance().getDecreeRanges().get(Type.SANCTUARY).get());
	}

}
