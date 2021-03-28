package me.superckl.prayers.item;

import java.util.List;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DivineTotemItem extends Item{

	public DivineTotemItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return true;
	}

	@Override
	public Rarity getRarity(final ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip,
			final ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("totem")).withStyle(TextFormatting.GRAY));
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
