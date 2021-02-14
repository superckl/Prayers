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
		this.motionY = -.012;
		this.maxAge = 1000;
		this.multiplyParticleScaleBy(0.15F);
	}

	protected PrayerParticle(final ClientWorld world, final double x, final double y, final double z,
			final double xSpeed, final double ySpeed, final double zSpeed) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		this.motionX = xSpeed;
		this.motionY = ySpeed;
		this.motionZ = zSpeed;
		this.rotSpeed = ((float)Math.random() - 0.5F) * 0.005F;
		this.maxAge = 20;
		this.canCollide = false;
		this.multiplyParticleScaleBy(0.1F);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.ageParticle();
		if (!this.isExpired) {
			this.move(this.motionX, this.motionY, this.motionZ);
			if(!this.onGround) {
				this.prevParticleAngle = this.particleAngle;
				this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0F;
			}else if(this.ageOnGround++ >= 10)
				this.setExpired();
		}
	}

	protected void ageParticle() {
		if (this.maxAge-- <= 0)
			this.setExpired();
	}

	public static class Factory implements IParticleFactory<BasicParticleType>{

		protected final IAnimatedSprite spriteSet;

		public Factory(final IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle makeParticle(final BasicParticleType typeIn, final ClientWorld worldIn, final double x, final double y, final double z,
				final double xSpeed, final double ySpeed, final double zSpeed) {
			PrayerParticle particle;
			if(typeIn == ModParticles.ITEM_SACRIFICE.get())
				particle = new PrayerParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			else
				particle = new PrayerParticle(worldIn, x, y, z);
			particle.selectSpriteRandomly(this.spriteSet);
			return particle;
		}

	}

}
