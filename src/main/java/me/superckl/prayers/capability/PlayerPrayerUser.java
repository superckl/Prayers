package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.PrayerInventoryItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketDeactivateAllPrayers;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import me.superckl.prayers.network.packet.user.PacketSetPrayerPoints;
import me.superckl.prayers.prayer.Prayer;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class PlayerPrayerUser extends LivingPrayerUser<PlayerEntity>{

	protected boolean autoSync;

	public PlayerPrayerUser(final PlayerEntity ref) {
		this(ref, true);
	}

	public PlayerPrayerUser(final PlayerEntity ref, final boolean autoSync) {
		super(ref);
		this.autoSync = autoSync;
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
		final Set<Prayer> activeItems = this.getActiveItems();
		if(activeItems.contains(prayer))
			return Result.NO_ITEM;
		final Set<String> excludes = Sets.newHashSet();
		this.getActivePrayers().forEach(activePrayer -> excludes.addAll(activePrayer.getExclusionTypes()));
		activeItems.forEach(activePrayer -> excludes.addAll(activePrayer.getExclusionTypes()));
		return Collections.disjoint(prayer.getExclusionTypes(), excludes) ? Result.YES:Result.NO_EXLCUDE;
	}

	@Override
	public float setCurrentPrayerPoints(final float currentPoints) {
		final float old = this.getCurrentPrayerPoints();
		final float newVal = super.setCurrentPrayerPoints(currentPoints);
		if(this.autoSync && !this.ref.level.isClientSide && ((ServerPlayerEntity)this.ref).connection != null && (newVal == 0 && old > 0 || old == 0 && newVal > 0  || MathUtil.isIntDifferent(old, newVal)))
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.ref),
					PacketSetPrayerPoints.builder().entityID(this.ref.getId()).amount(newVal).build());
		return newVal;
	}

	@Override
	public boolean deactivateAllPrayers() {
		final boolean changed = super.deactivateAllPrayers();
		if(changed && this.autoSync && !this.ref.level.isClientSide) {
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.ref),
					PacketDeactivateAllPrayers.builder().entityID(this.ref.getId()).build());
			return true;
		}
		return changed;
	}

	@Override
	public boolean deactivatePrayer(final Prayer prayer) {
		final boolean changed = super.deactivatePrayer(prayer);
		if(changed && this.autoSync && !this.ref.level.isClientSide)
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.ref),
					PacketDeactivatePrayer.builder().entityID(this.ref.getId()).prayer(prayer).build());
		return changed;
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
		return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
	}

	@Override
	public boolean isPrayerActive(final Prayer prayer) {
		return this.isPrayerActive(prayer, true);
	}

	public boolean isPrayerActive(final Prayer prayer, final boolean checkItem) {
		return super.isPrayerActive(prayer) || checkItem && this.hasActiveItem(prayer);
	}

	public boolean canUseItemPrayer(final Prayer prayer) {
		return !prayer.isObfusctated(this.ref);
	}

	@Override
	public float drainPoints(final float drain) {
		return this.drainPoints(drain, true);
	}

	public float drainPoints(float drain, final boolean drainReliquaries) {
		final float initDrain = drain;
		drain = 0;
		if(drainReliquaries) {
			drain = this.drainReliquaries(initDrain);
			if(drain == initDrain)
				return initDrain;
		}
		return drain+super.drainPoints(initDrain-drain);
	}

	public float drainReliquaries(float drain) {
		final float initDrain = drain;
		for(final InventoryPrayerProvider provider:this.findReliquaries()) {
			drain = drain - provider.drainPoints(drain);
			if(drain == 0)
				return initDrain;
		}
		return initDrain - drain;
	}

	@Override
	protected float modifyDrain(final float drain) {
		final int drainLvl = this.getPrayerDrainLevel();
		return drain*(.35F+.8F/(drainLvl+1));
	}

	protected int getPrayerDrainLevel() {
		int pieces = 0;
		for(final ItemStack stack:this.ref.inventory.armor)
			if(ItemBoon.PRAYER_DRAIN.has(stack))
				pieces++;
		return pieces;
	}

	public List<InventoryPrayerProvider> findReliquaries(){
		final List<InventoryPrayerProvider> providers = Lists.newArrayList();
		this.ref.inventory.items.forEach(stack -> {
			if(stack.getItem() == ModItems.RELIQUARY.get())
				providers.add(CapabilityHandler.getPrayerCapability(stack));
		});
		return providers;
	}

	public boolean hasActiveItem(final Prayer prayer) {
		final Stream<ItemStack> stream = Stream.concat(this.ref.inventory.items.stream(), this.ref.inventory.armor.stream());
		final Iterator<ItemStack> it = Stream.concat(stream, this.ref.inventory.offhand.stream()).iterator();
		while(it.hasNext()) {
			final ItemStack stack = it.next();
			if(stack.isEmpty() || !(stack.getItem() instanceof PrayerInventoryItem))
				continue;
			if(CapabilityHandler.getPrayerCapability(stack).isPrayerActive(prayer))
				return true;
		}
		return false;
	}

	public Set<Prayer> getActiveItems(){
		final Set<Prayer> active = Sets.newHashSet();
		final Stream<ItemStack> stream = Stream.concat(this.ref.inventory.items.stream(), this.ref.inventory.armor.stream());
		final Iterator<ItemStack> it = Stream.concat(stream, this.ref.inventory.offhand.stream()).iterator();
		while(it.hasNext()) {
			final ItemStack stack = it.next();
			if(stack.isEmpty() || !(stack.getItem() instanceof PrayerInventoryItem))
				continue;
			active.addAll(CapabilityHandler.getPrayerCapability(stack).getActivePrayers());
		}
		active.removeIf(prayer -> !this.canUseItemPrayer(prayer));
		return active;
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
			final boolean autoSync = instance.autoSync;
			instance.autoSync = false;
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
			instance.autoSync = autoSync;
		}

	}

	@RequiredArgsConstructor
	public enum Result{

		YES(1F),
		NO_DISABLED(0.1F),
		NO_TOME(0.2F),
		NO_LEVEL(0.2F),
		NO_POINTS(0.5F),
		NO_ITEM(0.5F),
		NO_EXLCUDE(0.5F);

		@Getter
		private final float renderAlpha;

	}

}
