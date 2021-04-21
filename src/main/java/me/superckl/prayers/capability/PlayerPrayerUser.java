package me.superckl.prayers.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.criteria.PrayerLevelCriteria;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.inventory.PlayerInventoryHelper;
import me.superckl.prayers.item.PrayerInventoryItem;
import me.superckl.prayers.item.ReliquaryItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketDeactivateAllPrayers;
import me.superckl.prayers.network.packet.user.PacketDeactivatePrayer;
import me.superckl.prayers.network.packet.user.PacketSetEffects;
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
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class PlayerPrayerUser extends LivingPrayerUser<PlayerEntity>{

	protected boolean autoSync;
	@Getter
	@Setter
	protected boolean unlocked;
	private int updateCounter;
	private Set<Prayer> activeItemCache;

	public PlayerPrayerUser(final PlayerEntity ref) {
		this(ref, true);
	}

	public PlayerPrayerUser(final PlayerEntity ref, final boolean autoSync) {
		super(ref);
		this.autoSync = autoSync;
	}

	public Result canActivatePrayer(final Prayer prayer) {
		if(!this.unlocked)
			return Result.NO_LOCKED;
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
	public double setCurrentPrayerPoints(final double currentPoints) {
		final double old = this.getCurrentPrayerPoints();
		final double newVal = super.setCurrentPrayerPoints(currentPoints);
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

	public abstract double addMaxPointsBoost(double boost);

	public abstract double setMaxPointsBoost(double boost);

	public abstract double getMaxPointsBoost();

	public abstract int getPrayerLevel();

	public abstract int setPrayerLevel(int level);

	public abstract int giveXP(float xp);

	public abstract void setXP(float xp);

	public abstract float getXP();

	public abstract boolean unlockPrayer(Prayer prayer);

	public abstract boolean isUnlocked(Prayer prayer);

	public abstract Collection<Prayer> getUnlockedPrayers();

	@Override
	public void tick() {
		if(this.activeItemCache == null || this.updateCounter++ >= 20)
			this.updateItemCache();
		super.tick();
	}

	public void computeLevel() {
		float xp = this.getXP();
		while(xp >= this.xpForLevel()) {
			xp -= this.xpForLevel();
			this.setPrayerLevel(this.getPrayerLevel()+1);
			if(this.ref instanceof ServerPlayerEntity)
				PrayerLevelCriteria.INSTANCE.trigger((ServerPlayerEntity) this.ref, this.getPrayerLevel());
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
	public double drainPoints(final double drain) {
		return this.drainPoints(drain, true);
	}

	public double drainPoints(double drain, final boolean drainReliquaries) {
		final double initDrain = drain;
		drain = 0;
		if(drainReliquaries) {
			drain = this.drainReliquaries(initDrain);
			if(drain == initDrain)
				return initDrain;
		}
		return drain+super.drainPoints(initDrain-drain);
	}

	public double drainReliquaries(double drain) {
		final double initDrain = drain;
		for(final InventoryPrayerProvider provider:this.findReliquaries()) {
			drain = drain - provider.drainPoints(drain);
			if(drain == 0)
				return initDrain;
		}
		return initDrain - drain;
	}

	@Override
	protected double modifyDrain(final double drain) {
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
		PlayerInventoryHelper.allItemsStream(this.ref).forEach(stack -> {
			if(stack.getItem() == ModItems.RELIQUARY.get() && ReliquaryItem.isActive(stack))
				providers.add(CapabilityHandler.getPrayerCapability(stack));
		});
		return providers;
	}

	public boolean hasActiveItem(final Prayer prayer) {
		//The client doesn't do ticking, so we have to iterate the inventory every time
		if(this.ref.level.isClientSide)
			return this.getActiveItems().contains(prayer);
		if(this.activeItemCache == null)
			this.updateItemCache();
		return this.activeItemCache.contains(prayer);
	}

	protected Set<Prayer> getActiveItems(){
		final Set<Prayer> active = Sets.newHashSet();
		final Iterator<ItemStack> it = PlayerInventoryHelper.allItems(this.ref);
		while(it.hasNext()) {
			final ItemStack stack = it.next();
			if(stack.isEmpty() || !(stack.getItem() instanceof PrayerInventoryItem))
				continue;
			active.addAll(CapabilityHandler.getPrayerCapability(stack).getActivePrayers());
		}
		active.removeIf(prayer -> !this.canUseItemPrayer(prayer));
		return active;
	}

	protected void updateItemCache() {
		this.updateCounter = 0;
		final Set<Prayer> previous = this.activeItemCache == null ? Sets.newHashSet():this.activeItemCache;
		this.activeItemCache = this.getActiveItems();
		//Only do this logic on server side
		if(!this.ref.level.isClientSide && previous.size() != this.activeItemCache.size()) {
			//Uh-oh, we're missing some information due to an item appearing/disappearing
			//We'll have to do some work to establish the correct effects state
			final Set<Prayer> missingItems = Sets.difference(previous, this.activeItemCache);
			final Set<Prayer> newItems = Sets.difference(this.activeItemCache, previous);
			missingItems.forEach(this::itemDeactivated);
			newItems.forEach(this::itemActivated);
		}
	}

	public void itemActivated(final Prayer prayer) {
		if(this.activeItemCache == null)
			this.updateItemCache();
		this.activeItemCache.add(prayer);
		this.attachEffects(prayer);
		//This is one place where desync is possible since ticking only happens on server.
		//Thus, we need to update the client.
		if(!this.ref.level.isClientSide)
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.ref),
					PacketSetEffects.builder().entityID(this.ref.getId()).prayer(prayer).attach(true).build());
	}

	public void itemDeactivated(final Prayer prayer) {
		if(this.activeItemCache == null)
			this.updateItemCache();
		this.activeItemCache.remove(prayer);
		this.detachEffects(prayer);
		//This is one place where desync is possible since ticking only happens on server.
		//Thus, we need to update the client.
		if(!this.ref.level.isClientSide)
			PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.ref),
					PacketSetEffects.builder().entityID(this.ref.getId()).prayer(prayer).attach(false).build());
	}

	public static class Storage implements Capability.IStorage<PlayerPrayerUser>{

		public static final String MAX_BOOST_KEY = "max_prayer_points_boost";
		public static final String LEVEL_KEY = "prayer_level";
		public static final String UNLOCKED_KEY = "unlocked";
		public static final String CURRENT_POINTS_KEY = "current_prayer_points";
		public static final String ENABLED_PRAYERS_KEY = "enabled_prayers";
		public static final String XP_KEY = "xp";
		public static final String UNLOCKED_PRAYERS_KEY = "unlocked_prayers";

		@Override
		public CompoundNBT writeNBT(final Capability<PlayerPrayerUser> capability, final PlayerPrayerUser instance, final Direction side) {
			final CompoundNBT parent = new CompoundNBT();
			parent.putInt(Storage.LEVEL_KEY, instance.getPrayerLevel());
			parent.putDouble(Storage.MAX_BOOST_KEY, instance.getMaxPointsBoost());
			parent.putDouble(Storage.CURRENT_POINTS_KEY, instance.getCurrentPrayerPoints());
			parent.putFloat(Storage.XP_KEY, instance.getXP());
			parent.putBoolean(Storage.UNLOCKED_KEY, instance.unlocked);
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
			instance.setMaxPointsBoost(parent.getDouble(Storage.MAX_BOOST_KEY));
			instance.setCurrentPrayerPoints(parent.getDouble(Storage.CURRENT_POINTS_KEY));
			instance.setXP(parent.getFloat(Storage.XP_KEY));
			instance.setUnlocked(parent.getBoolean(Storage.UNLOCKED_KEY));
			final IForgeRegistry<Prayer> registry = Prayer.REGISTRY.get();
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
		NO_EXLCUDE(0.5F),
		NO_LOCKED(0.2F);

		@Getter
		private final float renderAlpha;

	}

}
