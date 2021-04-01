package me.superckl.prayers.item;

import me.superckl.prayers.init.ModItems;
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
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemEvents {

	@SubscribeEvent
	public static void onPlayerKillEntity(final LivingDeathEvent e) {
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
	public static void onPlayerDamage(final LivingDamageEvent e) {
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

}
