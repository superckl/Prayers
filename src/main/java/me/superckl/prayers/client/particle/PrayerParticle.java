package me.superckl.prayers.client.particle;

import me.superckl.prayers.init.ModParticles;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class PrayerParticle extends SpriteTexturedParticle{

	private final float rotSpeed;
	private float ageOnGround;

	protected PrayerParticle(final ClientWorld world, final double x, final double y, final double z) {
		super(world, x, y, z);
		this.rotSpeed = ((float)Math.random() - 0.5F) * 0.005F;
		this.yd = -.012;
		this.lifetime = 1000;
		this.scale(0.15F);
	}

	protected PrayerParticle(final ClientWorld world, final double x, final double y, final double z,
			final double xSpeed, final double ySpeed, final double zSpeed) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.rotSpeed = ((float)Math.random() - 0.5F) * 0.005F;
		this.lifetime = 20;
		this.hasPhysics = false;
		this.scale(0.1F);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.ageParticle();
		if (!this.removed) {
			this.move(this.xd, this.yd, this.zd);
			if(!this.onGround) {
				this.oRoll = this.roll;
				this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
			}else if(this.ageOnGround++ >= 10)
				this.remove();
		}
	}

	protected void ageParticle() {
		if (this.lifetime-- <= 0)
			this.remove();
	}

	public static class Factory implements IParticleFactory<BasicParticleType>{

		protected final IAnimatedSprite spriteSet;

		public Factory(final IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(final BasicParticleType typeIn, final ClientWorld worldIn, final double x, final double y, final double z,
				final double xSpeed, final double ySpeed, final double zSpeed) {
			PrayerParticle particle;
			if(typeIn == ModParticles.ITEM_SACRIFICE.get())
				particle = new PrayerParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			else
				particle = new PrayerParticle(worldIn, x, y, z);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

	}

}
