package me.superckl.prayers.entity.ai;

import java.util.List;

import com.google.common.collect.Lists;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.LivingPrayerUser;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class WitherUsePrayersGoal extends Goal{

	private final WitherEntity entity;
	private final LivingPrayerUser<?> provider;
	private final List<Prayer> first_stage;
	private final List<Prayer> second_stage;
	private boolean wasPowered;

	public WitherUsePrayersGoal(final WitherEntity entity) {
		this.entity = entity;
		this.provider = CapabilityHandler.getPrayerCapability(entity);
		this.first_stage = Lists.newArrayList(Prayer.PROTECT_RANGE.get());
		this.second_stage = Lists.newArrayList(Prayer.PROTECT_MELEE.get());
	}

	public void addPrayer(final Prayer prayer) {
		this.first_stage.add(prayer);
		this.second_stage.add(prayer);
	}

	@Override
	public boolean canUse() {
		return true;
	}

	@Override
	public boolean isInterruptable() {
		return false;
	}

	@Override
	public void stop() {
		super.stop();
		this.provider.deactivateAllPrayers();
	}

	@Override
	public void start() {
		super.start();
		this.wasPowered = this.entity.isPowered();
		this.provider.deactivateAllPrayers();
		this.activatePrayers(this.wasPowered);
	}

	@Override
	public void tick() {
		if(this.entity.isPowered() != this.wasPowered) {
			this.provider.deactivateAllPrayers();
			this.wasPowered = this.entity.isPowered();
			this.activatePrayers(this.wasPowered);
		}
	}

	protected void activatePrayers(final boolean secondStage) {
		if(secondStage)
			this.second_stage.forEach(this.provider::activatePrayer);
		else
			this.first_stage.forEach(this.provider::activatePrayer);
		PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.entity), PacketSyncPrayerUser.from(this.entity));
	}

}
