package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

import lombok.Getter;
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
import net.minecraftforge.registries.IRegistryDelegate;

public abstract class TickablePrayerProvider<T> {

	@Getter
	protected float currentPrayerPoints;
	private final Set<IRegistryDelegate<Prayer>> activePrayers = Sets.newIdentityHashSet();
	protected final T ref;

	public TickablePrayerProvider(final T ref) {
		this.ref = ref;
		this.currentPrayerPoints = this.getMaxPrayerPoints();
	}

	public float setCurrentPrayerPoints(final float currentPoints) {
		if(currentPoints < 0)
			this.currentPrayerPoints = 0;
		else
			this.currentPrayerPoints = currentPoints;
		return this.currentPrayerPoints;
	}

	public void activatePrayer(final Prayer prayer) {
		if(!this.isPrayerActive(prayer))
			this.activePrayers.add(prayer.delegate);
	}

	public void deactivatePrayer(final Prayer prayer) {
		this.activePrayers.remove(prayer.delegate);
	}

	public void deactivateAllPrayers() {
		this.activePrayers.clear();
	}

	public boolean isPrayerActive(final Prayer prayer) {
		return this.activePrayers.contains(prayer.delegate);
	}

	public Collection<Prayer> getActivePrayers() {
		return this.activePrayers.stream().map(IRegistryDelegate::get).collect(Collectors.toSet());
	}

	public abstract float getMaxPrayerPoints();

	public void tick() {
		final float drain = (float) this.getActivePrayers().stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			newPoints = 0;
			this.deactivateAllPrayers();
		}
		this.setCurrentPrayerPoints(newPoints);
	}

	public void togglePrayer(final Prayer prayer) {
		if (this.isPrayerActive(prayer))
			this.deactivatePrayer(prayer);
		else
			this.activatePrayer(prayer);
	}

	public float addPoints(final float points) {
		final float toAdd = Math.min(points, this.getMaxPrayerPoints()-this.getCurrentPrayerPoints());
		this.setCurrentPrayerPoints(this.getCurrentPrayerPoints()+toAdd);
		return toAdd;
	}

	@RequiredArgsConstructor
	public static class Provider<T> implements ICapabilitySerializable<INBT>{

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

	public static class Storage<T extends TickablePrayerProvider<?>> implements Capability.IStorage<T>{

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