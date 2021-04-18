package me.superckl.prayers.item;

import me.superckl.prayers.entity.GrenadeEntity;
import me.superckl.prayers.init.ModEntities;
import me.superckl.prayers.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class GrenadeItem extends Item{

	public GrenadeItem() {
		super(new Item.Properties().stacksTo(16).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public ActionResult<ItemStack> use(final World level, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getItemInHand(hand);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (Item.random.nextFloat() * 0.4F + 0.8F));
		player.getCooldowns().addCooldown(this, 10);
		if(!level.isClientSide) {
			final GrenadeEntity entity = new GrenadeEntity(ModEntities.GRENADE.get(), player, level);
			entity.setItem(stack);
			entity.shootFromRotation(player, player.xRot, player.yRot, 0, 1.5F, 1);
			level.addFreshEntity(entity);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		if(!player.abilities.instabuild)
			stack.shrink(1);

		return ActionResult.sidedSuccess(stack, level.isClientSide);
	}

}
