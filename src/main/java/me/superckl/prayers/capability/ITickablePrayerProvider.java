package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public interface ITickablePrayerProvider<T> {

	void activatePrayer(Prayer prayer);

	void deactivatePrayer(Prayer prayer);

	boolean isPrayerActive(Prayer prayer);

	float setCurrentPrayerPoints(float currentPoints);

	float getMaxPrayerPoints();

	float getCurrentPrayerPoints();

	Collection<Prayer> getActivePrayers();

	void tick(T reference);

	@RequiredArgsConstructor
	public static class Provider<T extends ITickablePrayerProvider<?>> implements ICapabilitySerializable<INBT>{

		@Nonnull
		private T instance;
		private LazyOptional<T> holder = LazyOptional.of(() -> this.instance);
		@Nonnull
		private Supplier<Capability<T>> capabilitySupplier;

		@Override
		public <V> LazyOptional<V> getCapability(final Capability<V> cap, final Direction side) {
			return this.capabilitySupplier.get().orEmpty(cap, this.holder);
		}

		@Override
		public  INBT serializeNBT() {
			return this.capabilitySupplier.get().writeNBT(this.instance, null);
		}

		@Override
		public void deserializeNBT(final INBT nbt) {
			this.capabilitySupplier.get().readNBT(this.instance, null, nbt);
		}

		public void invalidate() {
			this.instance = null;
			this.holder = null;
			this.capabilitySupplier = null;
		}

	}

	public static class Storage<T extends ITickablePrayerProvider<?>> implements Capability.IStorage<T>{

		public static final String CURRENT_POINTS_KEY = "current_prayer_points";
		public static final String ENABLED_PRAYERS_KEY = "enabled_prayers";

		@Override
		public CompoundNBT writeNBT(final Capability<T> capability, final T instance, final Direction side) {
			final CompoundNBT parent = new CompoundNBT();
			parent.putFloat(Storage.CURRENT_POINTS_KEY, instance.getCurrentPrayerPoints());
			final ListNBT enabled = new ListNBT();
			instance.getActivePrayers().forEach(prayer -> enabled.add(StringNBT.valueOf(prayer.getRegistryName().toString())));
			parent.put(Storage.ENABLED_PRAYERS_KEY, enabled);
			return parent;
		}

		@Override
		public void readNBT(final Capability<T> capability, final T instance, final Direction side, final INBT nbt) {
			final CompoundNBT parent = (CompoundNBT) nbt;
			instance.setCurrentPrayerPoints(parent.getFloat(Storage.CURRENT_POINTS_KEY));
			final IForgeRegistry<Prayer> registry = GameRegistry.findRegistry(Prayer.class);
			final ListNBT enabled = parent.getList(Storage.ENABLED_PRAYERS_KEY, Constants.NBT.TAG_STRING);
			enabled.forEach(stringNbt -> instance.activatePrayer(registry.getValue(new ResourceLocation(stringNbt.getAsString()))));
		}

	}

}
