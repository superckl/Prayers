package me.superckl.prayers.prayer;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.inventory.PlayerInventoryHelper;
import me.superckl.prayers.inventory.SlotAwareIterator;
import me.superckl.prayers.item.TalismanItem;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.inventory.PacketTalismanState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IRegistryDelegate;

public abstract class ActivationCondition {

	private static Map<IRegistryDelegate<Prayer>, List<ActivationCondition>> conditions;

	public static void registerConditions() {
		ActivationCondition.conditions = Maps.newHashMap();
		final ActivationCondition fire = new ActivateFireProt();
		MinecraftForge.EVENT_BUS.register(fire);
		ActivationCondition.conditions.put(Prayer.PROTECT_FIRE.get().delegate, ImmutableList.of(fire));
		final ActivationCondition poison = new ActivatePoisonProt();
		MinecraftForge.EVENT_BUS.register(poison);
		ActivationCondition.conditions.put(Prayer.PROTECT_POISON.get().delegate, ImmutableList.of(poison));
	}

	public static boolean hasCondition(final Prayer prayer) {
		return ActivationCondition.conditions.containsKey(prayer.delegate) && !ActivationCondition.conditions.get(prayer.delegate).isEmpty();
	}

	public static List<ActivationCondition> getConditions(final Prayer prayer){
		return ActivationCondition.conditions.get(prayer.delegate);
	}

	protected void findAndActivateTalisman(final Prayer prayer, final PlayerEntity player) {
		if(player.level.isClientSide)
			return;
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		if(!prayer.isEnabled() || user.isPrayerActive(prayer) || !user.canUseItemPrayer(prayer))
			return;
		final SlotAwareIterator<?> it = PlayerInventoryHelper.allItems(player);
		while(it.hasNext()) {
			final ItemStack stack = it.next();
			final TalismanItem item = ModItems.TALISMAN.get();
			if(!stack.isEmpty() && stack.getItem() == item && TalismanItem.canAutoActivate(stack)) {
				final Prayer stored = TalismanItem.getStoredPrayer(stack).orElse(null);
				if(stored == prayer && item.applyState(stack, player, TalismanItem.State.ACTIVATE)) {
					PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
							PacketTalismanState.builder().entityID(player.getId()).slot(it.getHelper()).state(TalismanItem.State.ACTIVATE).build());
					break;
				}
			}
		}
	}

	public abstract ITextComponent getDescription();

	private static class ActivateFireProt extends ActivationCondition {

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onLivingHurt(final LivingAttackEvent e) {
			if(!e.getSource().isFire() || !(e.getEntityLiving() instanceof PlayerEntity) || e.getEntityLiving().hasEffect(Effects.FIRE_RESISTANCE))
				return;
			this.findAndActivateTalisman(Prayer.PROTECT_FIRE.get(), (PlayerEntity) e.getEntityLiving());
		}

		@Override
		public ITextComponent getDescription() {
			return new TranslationTextComponent(Util.makeDescriptionId("activate_condition", new ResourceLocation(Prayers.MOD_ID, "fire_prot")));
		}

	}

	private static class ActivatePoisonProt extends ActivationCondition {

		@SubscribeEvent(priority = EventPriority.LOW)
		public void onPotionCheck(final PotionApplicableEvent e) {
			if(e.getPotionEffect().getEffect() != Effects.POISON || !(e.getEntityLiving() instanceof PlayerEntity))
				return;
			this.findAndActivateTalisman(Prayer.PROTECT_POISON.get(), (PlayerEntity) e.getEntityLiving());
		}

		@Override
		public ITextComponent getDescription() {
			return new TranslationTextComponent(Util.makeDescriptionId("activate_condition", new ResourceLocation(Prayers.MOD_ID, "poison_prot")));
		}

	}

}
