package me.superckl.prayers;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.TalismanItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IRegistryDelegate;

public abstract class ActivationCondition {

	private static Map<IRegistryDelegate<Prayer>, List<ActivationCondition>> conditions;

	public static void registerConditions() {
		ActivationCondition.conditions = Maps.newHashMap();
		final ActivationCondition fire = new ActivateFireProt();
		MinecraftForge.EVENT_BUS.register(fire);
		ActivationCondition.conditions.put(Prayer.PROTECT_FIRE.get().delegate, ImmutableList.of(fire));
	}

	public static boolean hasCondition(final Prayer prayer) {
		return ActivationCondition.conditions.containsKey(prayer.delegate) && !ActivationCondition.conditions.get(prayer.delegate).isEmpty();
	}

	public static List<ActivationCondition> getConditions(final Prayer prayer){
		return ActivationCondition.conditions.get(prayer.delegate);
	}

	public abstract ITextComponent getDescription();

	private static class ActivateFireProt extends ActivationCondition{

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onLivingHurt(final LivingAttackEvent e) {
			if(!e.getSource().isFire() || !(e.getEntityLiving() instanceof PlayerEntity) || e.getEntityLiving().hasEffect(Effects.FIRE_RESISTANCE))
				return;
			final PlayerEntity player = (PlayerEntity) e.getEntityLiving();
			final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
			final Prayer fire = Prayer.PROTECT_FIRE.get();
			if(!fire.isEnabled() || user.isPrayerActive(fire) ||
					!CapabilityHandler.getPrayerCapability((PlayerEntity) e.getEntityLiving()).canUseItemPrayer(fire))
				return;
			for(final ItemStack stack:((PlayerEntity)e.getEntityLiving()).inventory.items) {
				final TalismanItem item = ModItems.TALISMAN.get();
				if(!stack.isEmpty() && stack.getItem() == item && item.canAutoActivate(stack)) {
					final Prayer stored = item.getStoredPrayer(stack).orElse(null);
					if(stored == fire) {
						item.activate(stack, player);
						break;
					}
				}
			}
		}

		@Override
		public ITextComponent getDescription() {
			return new TranslationTextComponent(Util.makeDescriptionId("activate_condition", new ResourceLocation(Prayers.MOD_ID, "fire_prot")));
		}

	}

}
