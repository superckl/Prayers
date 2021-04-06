package me.superckl.prayers.integration.jei.wither;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.world.World;

public class FakeWitherEntity extends WitherEntity{

	public FakeWitherEntity(final World world) {
		super(EntityType.WITHER, world);
	}

	@Override
	public float getHeadYRot(final int p_82207_1_) {
		return this.yHeadRot;
	}

	@Override
	public float getHeadXRot(final int p_82210_1_) {
		return this.xRot;
	}

}
