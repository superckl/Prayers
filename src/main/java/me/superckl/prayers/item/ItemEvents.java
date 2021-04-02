package me.superckl.prayers.item;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.decree.DecreeData;
import me.superckl.prayers.item.decree.DecreeItem;
import me.superckl.prayers.item.decree.InfertilityDecreeData;
import me.superckl.prayers.item.decree.ItemFrameTickManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
	public void onBreed(BabyEntitySpawnEvent e) {
		if(this.isInfertile(e.getParentA().blockPosition(), e.getParentB().blockPosition()))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onBonemeal(BonemealEvent e) {
		if(this.isInfertile(e.getPos()))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onCropGrow(CropGrowEvent.Pre e) {
		if(this.isInfertile(e.getPos()))
			e.setResult(Result.DENY);
	}
	
	public void onSaplingGrow(SaplingGrowTreeEvent e) {
		if(this.isInfertile(e.getPos()))
			e.setResult(Result.DENY);
	}
	
	private boolean isInfertile(BlockPos... positions) {
		for(DecreeData data:ItemFrameTickManager.INSTANCE.getDataForType(DecreeItem.Type.INFERTILITY)) {
			InfertilityDecreeData iData = (InfertilityDecreeData) data;
			for(BlockPos pos:positions)
				if(iData.isAffected(pos)) {
					return true;
				}
		}
		return false;
	}
	
}
