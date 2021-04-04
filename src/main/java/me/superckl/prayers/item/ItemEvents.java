package me.superckl.prayers.item;

import me.superckl.prayers.init.ModItems;
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
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
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
			for(final ItemStack stack:killer.inventory.items) {
				if(stack.getItem() != soulItem)
					continue;
				if(soulItem.onKill(type, stack))
					break;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerDamage(final LivingDamageEvent e) {
		if(!(e.getEntityLiving() instanceof PlayerEntity) || e.getSource().isBypassInvul())
			return;
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
