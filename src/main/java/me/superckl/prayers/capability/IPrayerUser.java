package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public interface IPrayerUser{

	default boolean canActivatePrayer(final Prayer prayer) {
		if(!prayer.isEnabled() || this.getPrayerLevel() < prayer.getLevel() || this.getCurrentPrayerPoints() < prayer.getDrain()/20F)
			return false;
		final Set<String> excludes = Sets.newHashSet();
		this.getActivePrayers().forEach(activePrayer -> excludes.addAll(activePrayer.getExclusionTypes()));
		return Collections.disjoint(prayer.getExclusionTypes(), excludes);
	}

	void activatePrayer(Prayer prayer);

	void deactivatePrayer(Prayer prayer);

	boolean isPrayerActive(Prayer prayer);

	float getMaxPrayerPoints();

	float getCurrentPrayerPoints();

	float addMaxPointsBoost(float boost);

	float setMaxPointsBoost(float boost);

	float getMaxPointsBoost();

	float setCurrentPrayerPoints(float currentPoints);

	int getPrayerLevel();

	int setPrayerLevel(int level);

	Collection<Prayer> getActivePrayers();

	void deactivateAllPrayers();

	default void togglePrayer(final Prayer prayer) {
		if (this.isPrayerActive(prayer))
			this.deactivatePrayer(prayer);
		else
			this.activatePrayer(prayer);
	}

	default void applyDrain() {
		final float drain = (float) this.getActivePrayers().stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			newPoints = 0;
			this.deactivateAllPrayers();
		}
		this.setCurrentPrayerPoints(newPoints);
	}

	static IPrayerUser getUser(final ICapabilityProvider entity) {
		return entity.getCapability(Prayers.PRAYER_USER_CAPABILITY)
				.orElseThrow(() -> new IllegalStateException(String.format("Received entity %s with no prayer capability!", entity.toString())));
	}

	@RequiredArgsConstructor
	public static class Provider implements ICapabilitySerializable<INBT>{

		@Nonnull
		private IPrayerUser instance;
		private LazyOptional<IPrayerUser> holder = LazyOptional.of(() -> this.instance);

		@Override
		public <T> LazyOptional<T> getCapability(final Capability<T> cap, final Direction side) {
			return Prayers.PRAYER_USER_CAPABILITY.orEmpty(cap, this.holder);
		}

		@Override
		public  INBT serializeNBT() {
			return Prayers.PRAYER_USER_CAPABILITY.getStorage().writeNBT(Prayers.PRAYER_USER_CAPABILITY, this.instance, null);
		}

		@Override
		public void deserializeNBT(final INBT nbt) {
			Prayers.PRAYER_USER_CAPABILITY.getStorage().readNBT(Prayers.PRAYER_USER_CAPABILITY, this.instance, null, nbt);
		}

		public void invalidate() {
			this.instance = null;
			this.holder = null;
		}

	}

	public static class Storage implements Capability.IStorage<IPrayerUser>{

		public static final String MAX_BOOST_KEY = "max_prayer_points_boost";
		public static final String LEVEL_KEY = "prayer_level";
		public static final String CURRENT_POINTS_KEY = "current_prayer_points";
		public static final String ENABLED_PRAYERS_KEY = "enabled_prayers";

		@Override
		public CompoundNBT writeNBT(final Capability<IPrayerUser> capability, final IPrayerUser instance, final Direction side) {
			final CompoundNBT parent = new CompoundNBT();
			parent.putInt(Storage.LEVEL_KEY, instance.getPrayerLevel());
			parent.putFloat(Storage.MAX_BOOST_KEY, instance.getMaxPointsBoost());
			parent.putFloat(Storage.CURRENT_POINTS_KEY, instance.getCurrentPrayerPoints());
			final ListNBT enabled = new ListNBT();
			instance.getActivePrayers().forEach(prayer -> enabled.add(StringNBT.valueOf(prayer.getRegistryName().toString())));
			parent.put(Storage.ENABLED_PRAYERS_KEY, enabled);
			return parent;
		}

		@Override
		public void readNBT(final Capability<IPrayerUser> capability, final IPrayerUser instance, final Direction side, final INBT nbt) {
			final CompoundNBT parent = (CompoundNBT) nbt;
			instance.setPrayerLevel(parent.getInt(Storage.LEVEL_KEY));
			instance.setMaxPointsBoost(parent.getFloat(Storage.MAX_BOOST_KEY));
			instance.setCurrentPrayerPoints(parent.getFloat(Storage.CURRENT_POINTS_KEY));
			final ListNBT enabled = parent.getList(Storage.ENABLED_PRAYERS_KEY, StringNBT.valueOf("").getId());
			final IForgeRegistry<Prayer> registry = GameRegistry.findRegistry(Prayer.class);
			enabled.forEach(stringNbt -> instance.activatePrayer(registry.getValue(new ResourceLocation(stringNbt.getString()))));
		}

	}

}
