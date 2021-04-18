package me.superckl.prayers.item;

import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.entity.GrenadeEntity.GrenadeDamageSource;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.inventory.PlayerInventoryHelper;
import me.superckl.prayers.item.decree.DecreeData;
import me.superckl.prayers.item.decree.DecreeItem;
import me.superckl.prayers.item.decree.InfertilityDecreeData;
import me.superckl.prayers.item.decree.ItemFrameTickManager;
import me.superckl.prayers.item.decree.SanctuaryDecreeData;
import me.superckl.prayers.util.ReflectionCache;
import me.superckl.prayers.util.ReflectionCache.Methods;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemEvents {

	@SubscribeEvent
	public void onPlayerKillEntity(final LivingDeathEvent e) {
		final EntityType<?> type = e.getEntityLiving().getType();
		if(!VesselItem.REQ_MOBS.contains(type.getRegistryName()))
			return;
		final Entity source = e.getSource().getDirectEntity();
		if(source instanceof PlayerEntity) {
			final PlayerEntity killer = (PlayerEntity) source;
			final VesselItem soulItem = ModItems.VESSEL.get();
			for(final ItemStack stack:PlayerInventoryHelper.allItems(killer).iterable()) {
				if(stack.getItem() != soulItem)
					continue;
				if(soulItem.onKill(type, stack))
					break;
			}
		}
	}

	@SubscribeEvent
	public void onEntityDrops(final LivingDropsEvent e) {
		final LivingEntity entity = e.getEntityLiving();
		if(entity instanceof WitherEntity) {
			CompoundNBT nbt = entity.getPersistentData();
			if(nbt.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
				nbt = nbt.getCompound(Prayers.MOD_ID);
				if(nbt.contains(TalismanItem.TALISMAN_KEY, Constants.NBT.TAG_COMPOUND)) {
					final ItemStack talisman = ItemStack.of(nbt.getCompound(TalismanItem.TALISMAN_KEY));
					TalismanItem.setAutoActivate(talisman);
					final ItemEntity item = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), talisman);
					e.getDrops().add(item);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerDamage(final LivingDamageEvent e) {
		if(e.getEntityLiving() instanceof PlayerEntity && !e.getSource().isBypassInvul()) {
			final PlayerEntity player = (PlayerEntity) e.getEntityLiving();
			if(e.getAmount() >= player.getHealth()) {
				final int slot = player.inventory.findSlotMatchingItem(new ItemStack(ModItems.DIVINE_TOTEM::get));
				if(slot != -1) {
					e.setCanceled(true);
					player.inventory.removeItem(slot, 1);

					if (player instanceof ServerPlayerEntity) {
						final ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
						serverplayerentity.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
						CriteriaTriggers.USED_TOTEM.trigger(serverplayerentity, new ItemStack(Items.TOTEM_OF_UNDYING));
					}

					player.setHealth(player.getMaxHealth());
					player.removeAllEffects();
					player.addEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
					player.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
					player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
					player.level.broadcastEntityEvent(player, (byte)35);
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(final PlayerEvent.ItemPickupEvent e) {
		if(e.getStack().getItem() instanceof PrayerInventoryItem<?>)
			CapabilityHandler.getPrayerCapability(e.getStack()).pickedUp(e.getPlayer());
	}

	//Cancel any damage from fake players to upgraded withers to prevent cheesing
	//Also cancel damage to non-monster entities from grenade
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingAttack(final LivingAttackEvent e) {
		if(Config.getInstance().getPreventWitherCheese().get() && e.getEntity() instanceof WitherEntity &&
				e.getSource().getEntity() instanceof FakePlayer &&
				(TalismanItem.hasStoredTalisman(e.getEntity()) || RelicItem.getBoon(e.getEntity()).isPresent()))
			e.setCanceled(true);
		if(e.getSource() instanceof GrenadeDamageSource && !((GrenadeDamageSource)e.getSource()).isDamageFriendly())
			if(!(e.getEntity() instanceof RabbitEntity) && !(e.getEntity() instanceof MonsterEntity))
				e.setCanceled(true);
	}

	@SubscribeEvent
	public void onMobSpawn(final LivingSpawnEvent.CheckSpawn e) {
		try {
			if(e.getEntityLiving() instanceof MobEntity && this.isSanctuary(e.getEntityLiving().blockPosition()) &&
					(Boolean) ReflectionCache.INSTANCE.get(Methods.MOB_ENTITY__SHOULD_DESPAWN_IN_PEACEFUL).invoke(e.getEntityLiving()))
				e.setResult(Result.DENY);
		} catch (final Exception e1) {
			throw new IllegalStateException("Failed to access despawn check method of mob entity!", e1);
		}
	}

	@SubscribeEvent
	public void onSpecialSpawn(final LivingSpawnEvent.SpecialSpawn e) {
		try {
			if(e.getEntityLiving() instanceof MobEntity && this.isSanctuary(e.getEntityLiving().blockPosition()) &&
					(Boolean) ReflectionCache.INSTANCE.get(Methods.MOB_ENTITY__SHOULD_DESPAWN_IN_PEACEFUL).invoke(e.getEntityLiving()))
				e.setCanceled(true);
		} catch (final Exception e1) {
			throw new IllegalStateException("Failed to access despawn check method of mob entity!", e1);
		}
	}

	@SubscribeEvent
	public void onBreed(final BabyEntitySpawnEvent e) {
		if(this.isInfertile(e.getParentA().blockPosition(), e.getParentB().blockPosition()))
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void onBonemeal(final BonemealEvent e) {
		if(this.isInfertile(e.getPos()))
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void onCropGrow(final CropGrowEvent.Pre e) {
		if(this.isInfertile(e.getPos()))
			e.setResult(Result.DENY);
	}

	public void onSaplingGrow(final SaplingGrowTreeEvent e) {
		if(this.isInfertile(e.getPos()))
			e.setResult(Result.DENY);
	}

	private boolean isSanctuary(final BlockPos... positions) {
		for(final DecreeData data:ItemFrameTickManager.INSTANCE.getDataForType(DecreeItem.Type.SANCTUARY)) {
			final SanctuaryDecreeData iData = (SanctuaryDecreeData) data;
			for(final BlockPos pos:positions)
				if(iData.isAffected(pos))
					return true;
		}
		return false;
	}

	private boolean isInfertile(final BlockPos... positions) {
		for(final DecreeData data:ItemFrameTickManager.INSTANCE.getDataForType(DecreeItem.Type.INFERTILITY)) {
			final InfertilityDecreeData iData = (InfertilityDecreeData) data;
			for(final BlockPos pos:positions)
				if(iData.isAffected(pos))
					return true;
		}
		return false;
	}

}
