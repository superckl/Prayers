package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayer;
import net.minecraft.entity.player.PlayerEntity;
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

public abstract class PlayerPrayerUser extends TickablePrayerProvider<PlayerEntity>{

	public PlayerPrayerUser(final PlayerEntity ref) {
		super(ref);
	}

	public Result canActivatePrayer(final Prayer prayer) {
		if(!prayer.isEnabled())
			return Result.NO_DISABLED;
		if(prayer.isRequiresTome() && !this.isUnlocked(prayer))
			return Result.NO_TOME;
		if(this.getPrayerLevel() < prayer.getLevel())
			return Result.NO_LEVEL;
		if(this.getCurrentPrayerPoints() < prayer.getDrain()/20F)
			return Result.NO_POINTS;
		final Set<String> excludes = Sets.newHashSet();
		this.getActivePrayers().forEach(activePrayer -> excludes.addAll(activePrayer.getExclusionTypes()));
		return Collections.disjoint(prayer.getExclusionTypes(), excludes) ? Result.YES:Result.NO_EXLCUDE;
	}

	public abstract float addMaxPointsBoost(float boost);

	public abstract float setMaxPointsBoost(float boost);

	public abstract float getMaxPointsBoost();

	public abstract int getPrayerLevel();

	public abstract int setPrayerLevel(int level);

	public abstract int giveXP(float xp);

	public abstract void setXP(float xp);

	public abstract float getXP();

	public abstract boolean unlockPrayer(Prayer prayer);

	public abstract boolean isUnlocked(Prayer prayer);

	public abstract Collection<Prayer> getUnlockedPrayers();

	public void computeLevel() {
		float xp = this.getXP();
		while(xp >= this.xpForLevel()) {
			xp -= this.xpForLevel();
			this.setPrayerLevel(this.getPrayerLevel()+1);
		}
		this.setXP(xp);
	}

	public int xpForLevel() {
		final int level = this.getPrayerLevel();
		if (level >= 30)
			return 112 + (level - 30) * 9;
		else
			return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
	}

	public static class Storage implements Capability.IStorage<PlayerPrayerUser>{

		public static final String MAX_BOOST_KEY = "max_prayer_points_boost";
		public static final String LEVEL_KEY = "prayer_level";
		public static final String CURRENT_POINTS_KEY = "current_prayer_points";
		public static final String ENABLED_PRAYERS_KEY = "enabled_prayers";
		public static final String XP_KEY = "xp";
		public static final String UNLOCKED_PRAYERS_KEY = "unlocked_prayers";

		@Override
		public CompoundNBT writeNBT(final Capability<PlayerPrayerUser> capability, final PlayerPrayerUser instance, final Direction side) {
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
		public void readNBT(final Capability<PlayerPrayerUser> capability, final PlayerPrayerUser instance, final Direction side, final INBT nbt) {
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

	@RequiredArgsConstructor
	public enum Result{

		YES(1F),
		NO_DISABLED(0.1F),
		NO_TOME(0.2F),
		NO_LEVEL(0.2F),
		NO_POINTS(0.5F),
		NO_EXLCUDE(0.5F);

		@Getter
		private final float renderAlpha;

	}

}
