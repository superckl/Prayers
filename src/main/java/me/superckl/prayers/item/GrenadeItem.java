package me.superckl.prayers.item;

import me.superckl.prayers.Config;
import me.superckl.prayers.LogHelper;
import me.superckl.prayers.entity.GrenadeEntity;
import me.superckl.prayers.init.ModEntities;
import me.superckl.prayers.init.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class GrenadeItem extends Item{

	public GrenadeItem() {
		super(new Item.Properties().stacksTo(16).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public void releaseUsing(final ItemStack stack, final World level, final LivingEntity livingEntity, final int remainingTicks) {
		final int ticks = this.getUseDuration(stack) - remainingTicks;
		level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (Item.random.nextFloat() * 0.4F + 0.8F));
		if(livingEntity instanceof PlayerEntity && !level.isClientSide) {
			final GrenadeEntity entity = new GrenadeEntity(ModEntities.GRENADE.get(), livingEntity, level);
			entity.setItem(stack);
			entity.shootFromRotation(livingEntity, livingEntity.xRot, livingEntity.yRot, 0, 1.5F, 1);
			entity.subtractCookTime(ticks);
			level.addFreshEntity(entity);
		}
		boolean shrink = true;
		if(livingEntity instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity) livingEntity;
			player.getCooldowns().addCooldown(this, 10);
			player.awardStat(Stats.ITEM_USED.get(this));
			shrink = !player.abilities.instabuild;
		}
		if(shrink)
			stack.shrink(1);
	}

	@Override
	public void onUseTick(final World level, final LivingEntity entity, final ItemStack stack, final int remainingTicks) {
		final int ticks = this.getUseDuration(stack) - remainingTicks;
		if(ticks >= Config.getInstance().getGrenadeTime().get()) {
			entity.stopUsingItem();
			boolean shrink = true;
			if(entity instanceof PlayerEntity) {
				final PlayerEntity player = (PlayerEntity) entity;
				shrink = !player.abilities.instabuild;
			}
			if(shrink)
				stack.shrink(1);
			final Vector3d front = entity.getLookAngle().normalize().scale(.1);
			level.explode(null, new GrenadeEntity.GrenadeDamageSource("explosion.player", entity, true), null, entity.getX()+front.x, entity.getEyeY()+front.y, entity.getZ()+front.z, 2.5F, false, Explosion.Mode.NONE);
			LogHelper.info(level.isClientSide);
		}
	}

	@Override
	public ActionResult<ItemStack> use(final World level, final PlayerEntity player, final Hand hand) {
		player.startUsingItem(hand);
		return ActionResult.consume(player.getItemInHand(hand));
	}

	@Override
	public int getUseDuration(final ItemStack stack) {
		return 72000;
	}

	@Override
	public UseAction getUseAnimation(final ItemStack stack) {
		return UseAction.BOW;
	}

}
