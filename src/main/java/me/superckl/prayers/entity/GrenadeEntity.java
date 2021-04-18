package me.superckl.prayers.entity;

import me.superckl.prayers.Config;
import me.superckl.prayers.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class GrenadeEntity extends ProjectileItemEntity{

	private int timeRemaining = Config.getInstance().getGrenadeTime().get();

	public GrenadeEntity(final EntityType<? extends GrenadeEntity> type, final World level) {
		super(type, level);
	}

	public GrenadeEntity(final EntityType<? extends GrenadeEntity> type, final LivingEntity owner,final World level) {
		super(type, owner, level);
	}

	public void subtractCookTime(final int time) {
		this.timeRemaining -= time;
	}

	@Override
	public void tick() {
		if (!this.isNoGravity())
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));

		this.move(MoverType.SELF, this.getDeltaMovement());
		this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
		if (this.onGround)
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, -0.5D, 0.5D));
		if(this.timeRemaining-- <= 0) {
			this.explode();
			this.remove();
			return;
		}
		this.updateInWaterStateAndDoFluidPushing();
		if (this.level.isClientSide && this.random.nextBoolean())
			this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
	}

	protected void explode() {
		this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 4.0F, Explosion.Mode.NONE);
	}

	@Override
	public boolean shouldBlockExplode(final Explosion explosion, final IBlockReader level, final BlockPos position,
			final BlockState state, final float number) {
		return false;
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.GRENADE.get();
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
