package me.superckl.prayers.boon;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;

import me.superckl.prayers.Config;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.criteria.ApplyBoonCriteria;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.RelicItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BoonEventHandler {

	@SubscribeEvent
	public void onAttributes(final ItemAttributeModifierEvent e) {
		if(!(e.getItemStack().getItem() instanceof ArmorItem) || ((ArmorItem) e.getItemStack().getItem()).getSlot() == e.getSlotType())
			ItemBoon.getBoon(e.getItemStack()).ifPresent(boon -> {
				if(ArrayUtils.contains(boon.getTypes(), e.getSlotType()))
					e.addModifier(boon.getAttributeSupplier().get(), boon.getModifierSupplier().apply(e.getSlotType()));
			});
	}

	@SubscribeEvent
	public void onDigSpeedCheck(final PlayerEvent.BreakSpeed e) {
		int numActive = 0;
		if(ItemBoon.USE_SPEED.has(e.getPlayer().getMainHandItem()))
			numActive++;
		if(ItemBoon.USE_SPEED.has(e.getPlayer().getOffhandItem()))
			numActive++;
		if(numActive > 0) {
			final float modifier = Config.getInstance().getDigBoon().get().floatValue();
			e.setNewSpeed(e.getNewSpeed()*(1+numActive*modifier));
		}
	}

	private final Set<String> stepUpCache = Sets.newHashSet();

	@SubscribeEvent
	public void onLivingUpdate(final LivingUpdateEvent e) {
		if(e.getEntity() instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity) e.getEntity();
			final String playerString = this.playerString(player);
			final boolean shouldHaveStep = ItemBoon.STEP_UP.has(player.getItemBySlot(EquipmentSlotType.FEET));
			if(this.stepUpCache.contains(playerString)) {
				if(!shouldHaveStep) {
					this.stepUpCache.remove(playerString);
					player.maxUpStep = 0.6F;
				}else
					player.maxUpStep = player.isCrouching() ? .6001F : 1.25F;
			}else if(shouldHaveStep) {
				this.stepUpCache.add(playerString);
				player.maxUpStep = player.isCrouching() ? .6001F : 1.25F;
			}
		}
	}

	private String playerString(final PlayerEntity player) {
		return new StringBuilder(player.getGameProfile().getName()).append(':').append(player.level.isClientSide).toString();
	}

	@SubscribeEvent
	public void serverLoggedOut(final PlayerEvent.PlayerLoggedOutEvent e) {
		this.stepUpCache.remove(this.playerString(e.getPlayer()));
	}

	@SubscribeEvent
	public void clientLoggedOut(final ClientPlayerNetworkEvent.LoggedOutEvent e) {
		if(e.getPlayer() != null)
			this.stepUpCache.remove(this.playerString(e.getPlayer()));
	}

	@SubscribeEvent
	public void onCraft(final PlayerEvent.ItemCraftedEvent e) {
		if(!e.getPlayer().level.isClientSide && ItemBoon.getBoon(e.getCrafting()).isPresent())
			ApplyBoonCriteria.INSTANCE.trigger((ServerPlayerEntity) e.getPlayer());
	}

	//We have to add boon damage manually since wither skulls don't check for damage modifiers on their source
	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent e) {
		if(e.getSource().getDirectEntity() instanceof WitherSkullEntity && e.getSource().getEntity() instanceof WitherEntity)
			RelicItem.getBoon(e.getSource().getEntity()).ifPresent(boon -> {
				if(boon == ItemBoon.ATTACK_DAMAGE) {
					final AttributeModifier mod = boon.getModifierSupplier().apply(EquipmentSlotType.MAINHAND);
					switch(mod.getOperation()) {
					case ADDITION:
						e.setAmount((float) (e.getAmount()+mod.getAmount()));
						break;
					case MULTIPLY_BASE:
					case MULTIPLY_TOTAL:
						e.setAmount((float) (e.getAmount()*mod.getAmount()));
						break;
					default:
						break;
					}
				}
			});
	}

	@SubscribeEvent
	public void onEntityDrops(final LivingDropsEvent e) {
		final LivingEntity entity = e.getEntityLiving();
		if(entity instanceof WitherEntity) {
			CompoundNBT nbt = entity.getPersistentData();
			if(nbt.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
				nbt = nbt.getCompound(Prayers.MOD_ID);
				if(nbt.contains(RelicItem.BOON_KEY, Constants.NBT.TAG_STRING)) {
					final ItemBoon boon = ItemBoon.valueOf(nbt.getString(RelicItem.BOON_KEY));
					final ItemStack relic = new ItemStack(ModItems.RELICS.get(boon)::get);
					RelicItem.setCharged(relic);
					final ItemEntity item = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), relic);
					e.getDrops().add(item);
				}
			}
		}
	}

}
