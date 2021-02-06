package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.Getter;
import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class DefaultPrayerUser implements IPrayerUser{

	@Getter
	private float currentPrayerPoints;
	@Getter
	private int prayerLevel;
	@Getter
	private float maxPointsBoost;
	private float maxPoints;

	private final Set<Prayer> activePrayers = Sets.newIdentityHashSet();

	public DefaultPrayerUser() {
		this.prayerLevel = 1;
		this.maxPointsBoost = 0;
		this.maxPoints = this.computeMaxPoints();
		this.currentPrayerPoints = this.maxPoints;
	}

	@Override
	public void activatePrayer(final Prayer prayer) {
		this.activePrayers.add(prayer);
	}

	@Override
	public void deactivatePrayer(final Prayer prayer) {
		this.activePrayers.remove(prayer);
	}

	@Override
	public void deactivateAllPrayers() {
		this.activePrayers.clear();
	}

	@Override
	public boolean isPrayerActive(final Prayer prayer) {
		return this.activePrayers.contains(prayer);
	}

	@Override
	public float getMaxPrayerPoints() {
		return this.maxPoints;
	}

	@Override
	public float addMaxPointsBoost(final float boost) {
		this.maxPointsBoost += boost;
		if(this.maxPointsBoost < 0)
			this.maxPointsBoost = 0;
		this.maxPoints = this.computeMaxPoints();
		return this.maxPointsBoost;
	}

	@Override
	public float setMaxPointsBoost(final float boost) {
		if(boost < 0)
			this.maxPointsBoost = 0;
		else
			this.maxPointsBoost = boost;
		this.maxPoints = this.computeMaxPoints();
		return this.maxPointsBoost;
	}

	@Override
	public float setCurrentPrayerPoints(final float currentPoints) {
		if(currentPoints < 0)
			this.currentPrayerPoints = 0;
		else
			this.currentPrayerPoints = currentPoints;
		return this.currentPrayerPoints;
	}

	@Override
	public int setPrayerLevel(final int level) {
		if(level < 1)
			this.prayerLevel = 1;
		else
			this.prayerLevel = level;
		this.maxPoints = this.computeMaxPoints();
		return this.prayerLevel;
	}

	@Override
	public Collection<Prayer> getActivePrayers() {
		return ImmutableSet.copyOf(this.activePrayers);
	}

	private float computeMaxPoints() {
		return 10*this.prayerLevel+this.maxPointsBoost;
	}

	public static class Provider implements ICapabilitySerializable<INBT>{

		private IPrayerUser impl = Prayers.PRAYER_USER_CAPABILITY.getDefaultInstance();
		private LazyOptional<IPrayerUser> holder = LazyOptional.of(() -> this.impl);

		@Override
		public <T> LazyOptional<T> getCapability(final Capability<T> cap, final Direction side) {
			return Prayers.PRAYER_USER_CAPABILITY.orEmpty(cap, this.holder);
		}

		@Override
		public  INBT serializeNBT() {
			return Prayers.PRAYER_USER_CAPABILITY.getStorage().writeNBT(Prayers.PRAYER_USER_CAPABILITY, this.impl, null);
		}

		@Override
		public void deserializeNBT(final INBT nbt) {
			Prayers.PRAYER_USER_CAPABILITY.getStorage().readNBT(Prayers.PRAYER_USER_CAPABILITY, this.impl, null, nbt);
		}

		public void invalidate() {
			this.impl = null;
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
