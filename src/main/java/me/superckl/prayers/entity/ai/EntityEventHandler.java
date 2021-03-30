package me.superckl.prayers.entity.ai;

import java.util.Random;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.item.TalismanItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onJoinWorld(final EntityJoinWorldEvent e) {
		if(e.getEntity() instanceof WitherEntity) {
			CompoundNBT nbt = e.getEntity().getPersistentData();
			if(nbt.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
				nbt = nbt.getCompound(Prayers.MOD_ID);
				if(nbt.contains(TalismanItem.TALISMAN_KEY, Constants.NBT.TAG_COMPOUND)) {
					final ItemStack talisman = ItemStack.of(nbt.getCompound(TalismanItem.TALISMAN_KEY));
					final WitherUsePrayersGoal goal = new WitherUsePrayersGoal((WitherEntity) e.getEntity());
					ModItems.TALISMAN.get().getStoredPrayer(talisman).ifPresent(goal::addPrayer);
					((WitherEntity)e.getEntity()).goalSelector.addGoal(0, goal);
					CapabilityHandler.getPrayerCapability((LivingEntity) e.getEntity()).setShouldDrain(false);
				}
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
					ModItems.TALISMAN.get().setAutoActivate(talisman);
					final ItemEntity item = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), talisman);
					e.getDrops().add(item);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityTick(final LivingUpdateEvent e) {
		final LivingEntity entity = e.getEntityLiving();
		if(!entity.level.isClientSide && entity instanceof WitherEntity) {
			CompoundNBT nbt = entity.getPersistentData();
			if(nbt.contains(Prayers.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
				nbt = nbt.getCompound(Prayers.MOD_ID);
				if(nbt.contains(TalismanItem.TALISMAN_KEY, Constants.NBT.TAG_COMPOUND)) {
					if(entity.getRandom().nextFloat() < .15) {
						Vector3d pos = this.randomPointIn(entity.getBoundingBox(), entity.getRandom());
						((ServerWorld)entity.level).sendParticles(ModParticles.ALTAR_ACTIVE.get(), pos.x, pos.y, pos.z, 0, 0, 0, 0, 0);
					}
				}
			}
		}
	}
	
	private Vector3d randomPointIn(AxisAlignedBB bb, Random random) {
		return bb.getCenter().add(bb.getXsize()*(random.nextDouble()-1D/2D),
				bb.getYsize()*(random.nextDouble()-1D/2D), bb.getZsize()*(random.nextDouble()-1D/2D));
	}
	
}
