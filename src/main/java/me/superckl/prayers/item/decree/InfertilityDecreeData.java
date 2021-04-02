package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;

import me.superckl.prayers.Config;
import me.superckl.prayers.item.decree.DecreeItem.Type;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class InfertilityDecreeData extends DecreeData{

	public InfertilityDecreeData(WeakReference<ItemFrameEntity> ref) {
		super(ref);
	}
	
	public boolean isAffected(BlockPos pos) {
		return this.ref.get().getRotation() == 0 && pos.distSqr(this.ref.get().getPos()) <= MathHelper.square(Config.getInstance().getDecreeRanges().get(Type.INFERTILITY).get());
	}

}
