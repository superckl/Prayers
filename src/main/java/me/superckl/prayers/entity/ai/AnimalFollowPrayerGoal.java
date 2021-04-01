package me.superckl.prayers.entity.ai;

import java.util.EnumSet;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;

public class AnimalFollowPrayerGoal extends Goal{

	private static final EntityPredicate TEMP_TARGETING = new EntityPredicate().range(10.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();
	protected final MobEntity mob;
	protected PlayerEntity player;
	private int calmDown;
	private boolean isRunning;

	public AnimalFollowPrayerGoal(final MobEntity entity) {
		this.mob = entity;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		if (!(entity.getNavigation() instanceof GroundPathNavigator) && !(entity.getNavigation() instanceof FlyingPathNavigator))
			throw new IllegalArgumentException("Unsupported mob type for FollowPrayerGoal");
	}

	@Override
	public boolean canUse() {
		if (this.calmDown > 0) {
			--this.calmDown;
			return false;
		}
		this.player = this.mob.level.getNearestPlayer(AnimalFollowPrayerGoal.TEMP_TARGETING, this.mob);
		if (this.player == null)
			return false;
		return CapabilityHandler.getPrayerCapability(this.player).isPrayerActive(Prayer.ARK.get());
	}

	@Override
	public boolean canContinueToUse() {
		return this.canUse();
	}

	@Override
	public void start() {
		this.isRunning = true;
	}

	@Override
	public void stop() {
		this.player = null;
		this.mob.getNavigation().stop();
		this.calmDown = 100;
		this.isRunning = false;
	}

	@Override
	public void tick() {
		this.mob.getLookControl().setLookAt(this.player, this.mob.getMaxHeadYRot() + 20, this.mob.getMaxHeadXRot());
		if (this.mob.distanceToSqr(this.player) < 6.25D)
			this.mob.getNavigation().stop();
		else
			this.mob.getNavigation().moveTo(this.player, 1.1);

	}

	public boolean isRunning() {
		return this.isRunning;
	}

}
