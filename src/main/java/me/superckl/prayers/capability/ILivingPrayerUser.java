package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public interface ILivingPrayerUser extends ITickablePrayerProvider<LivingEntity>{

	default boolean canActivatePrayer(final Prayer prayer) {
		if(!prayer.isEnabled() || prayer.isRequiresTome() && !this.isUnlocked(prayer) || this.getPrayerLevel() < prayer.getLevel() || this.getCurrentPrayerPoints() < prayer.getDrain()/20F)
			return false;
		final Set<String> excludes = Sets.newHashSet();
		this.getActivePrayers().forEach(activePrayer -> excludes.addAll(activePrayer.getExclusionTypes()));
		return Collections.disjoint(prayer.getExclusionTypes(), excludes);
	}

	float addMaxPointsBoost(float boost);

	float setMaxPointsBoost(float boost);

	float getMaxPointsBoost();

	int getPrayerLevel();

	int setPrayerLevel(int level);

	int giveXP(float xp);

	void setXP(float xp);

	float getXP();

	boolean unlockPrayer(Prayer prayer);

	boolean isUnlocked(Prayer prayer);

	Collection<Prayer> getUnlockedPrayers();

	default void computeLevel() {
		float xp = this.getXP();
		while(xp >= this.xpForLevel()) {
			xp -= this.xpForLevel();
			this.setPrayerLevel(this.getPrayerLevel()+1);
		}
		this.setXP(xp);
	}

	default int xpForLevel() {
		final int level = this.getPrayerLevel();
		if (level >= 30)
			return 112 + (level - 30) * 9;
		else
			return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
	}

	@Override
	default void tick(final LivingEntity reference) {
		final float drain = (float) this.getActivePrayers().stream().mapToDouble(Prayer::getDrain).sum();
		float newPoints = this.getCurrentPrayerPoints()-drain/20F;
		if (newPoints < 0) {
			newPoints = 0;
			this.deactivateAllPrayers();
		}
		this.setCurrentPrayerPoints(newPoints);
	}

	static ILivingPrayerUser get(final LivingEntity entity) {
		return entity.getCapability(Prayers.PRAYER_USER_CAPABILITY)
				.orElseThrow(() -> new IllegalStateException(String.format("Received entity %s with no prayer capability!", entity.toString())));
	}

	public static class Storage implements Capability.IStorage<ILivingPrayerUser>{

		public static final String MAX_BOOST_KEY = "max_prayer_points_boost";
		public static final String LEVEL_KEY = "prayer_level";
		public static final String CURRENT_POINTS_KEY = "current_prayer_points";
		public static final String ENABLED_PRAYERS_KEY = "enabled_prayers";
		public static final String XP_KEY = "xp";
		public static final String UNLOCKED_PRAYERS_KEY = "unlocked_prayers";

		@Override
		public CompoundNBT writeNBT(final Capability<ILivingPrayerUser> capability, final ILivingPrayerUser instance, final Direction side) {
			final CompoundNBT parent = new CompoundNBT();
			parent.putInt(Storage.LEVEL_KEY, instance.getPrayerLevel());
			parent.putFloat(Storage.MAX_BOOST_KEY, instance.getMaxPointsBoost());
			parent.putFloat(Storage.CURRENT_POINTS_KEY, instance.getCurrentPrayerPoints());
			parent.putFloat(Storage.XP_KEY, instance.getXP());
			final ListNBT unlocked = new ListNBT();
			instance.getUnlockedPrayers().forEach(prayer -> unlocked.add(StringNBT.valueOf(prayer.getRegistryName().toString())));
			parent.put(Storage.UNLOCKED_PRAYERS_KEY, unlocked);
			final ListNBT enabled = new ListNBT();
			instance.getActivePrayers().forEach(prayer -> enabled.add(StringNBT.valueOf(prayer.getRegistryName().toString())));
			parent.put(Storage.ENABLED_PRAYERS_KEY, enabled);
			return parent;
		}

		@Override
		public void readNBT(final Capability<ILivingPrayerUser> capability, final ILivingPrayerUser instance, final Direction side, final INBT nbt) {
			final CompoundNBT parent = (CompoundNBT) nbt;
			instance.setPrayerLevel(parent.getInt(Storage.LEVEL_KEY));
			instance.setMaxPointsBoost(parent.getFloat(Storage.MAX_BOOST_KEY));
			instance.setCurrentPrayerPoints(parent.getFloat(Storage.CURRENT_POINTS_KEY));
			instance.setXP(parent.getFloat(Storage.XP_KEY));
			final IForgeRegistry<Prayer> registry = GameRegistry.findRegistry(Prayer.class);
			final ListNBT unlocked = parent.getList(Storage.UNLOCKED_PRAYERS_KEY, Constants.NBT.TAG_STRING);
			unlocked.forEach(stringNbt -> instance.unlockPrayer(registry.getValue(new ResourceLocation(stringNbt.getAsString()))));
			final ListNBT enabled = parent.getList(Storage.ENABLED_PRAYERS_KEY, Constants.NBT.TAG_STRING);
			enabled.forEach(stringNbt -> instance.activatePrayer(registry.getValue(new ResourceLocation(stringNbt.getAsString()))));
		}

	}

}
