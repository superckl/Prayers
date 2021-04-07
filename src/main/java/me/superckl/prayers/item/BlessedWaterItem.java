package me.superckl.prayers.item;

import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.network.packet.PrayersPacketHandler;
import me.superckl.prayers.network.packet.user.PacketSyncPrayerUser;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class BlessedWaterItem extends Item{

	public BlessedWaterItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack stack, final World level, final LivingEntity entity) {
		if(entity instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity) entity;
			if(player instanceof ServerPlayerEntity)
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
			player.awardStat(Stats.ITEM_USED.get(this));
			final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
			if(!user.isUnlocked()) {
				user.setUnlocked(true);
				if(!level.isClientSide)
					PrayersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
							PacketSyncPrayerUser.from(player));
			}
			if (!player.isCreative()) {
				stack.shrink(1);
				return new ItemStack(Items.GLASS_BOTTLE);
			}
		}
		return stack;
	}

	@Override
	public int getUseDuration(final ItemStack p_77626_1_) {
		return 32;
	}

	@Override
	public UseAction getUseAnimation(final ItemStack p_77661_1_) {
		return UseAction.DRINK;
	}
	
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
	      return DrinkHelper.useDrink(level, player, hand);
	   }

}
