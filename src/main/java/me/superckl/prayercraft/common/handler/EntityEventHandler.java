package me.superckl.prayercraft.common.handler;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.Prayers;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.reference.ModItems;
import me.superckl.prayercraft.common.utility.PCReflectionHelper;
import me.superckl.prayercraft.common.utility.PotionEffectHashMap;
import me.superckl.prayercraft.common.utility.PrayerHelper;
import me.superckl.prayercraft.network.MessageUpdatePrayers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityConstruction(final EntityConstructing e){
		if(e.entity instanceof EntityPlayer)
			e.entity.registerExtendedProperties("prayer", new PrayerExtendedProperties());
	}

	@SubscribeEvent
	public void onEntitySpawn(final EntityJoinWorldEvent e){
		if((e.entity instanceof EntityLivingBase) == false)
			return;
		PCReflectionHelper.setPrivateFinalValue(EntityLivingBase.class, (EntityLivingBase) e.entity, new PotionEffectHashMap((EntityLivingBase) e.entity), "activePotionsMap", "field_70713_bf");
		if(!e.world.isRemote && (e.entity instanceof EntityPlayerMP))
			ModData.PRAYER_UPDATE_CHANNEL.sendTo(new MessageUpdatePrayers(PrayerHelper.getActivePrayers((EntityLivingBase) e.entity)), (EntityPlayerMP) e.entity);
	}

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent e){
		if(e.source.isUnblockable())
			return;
		if(e.entityLiving instanceof EntityPlayer){
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) e.entityLiving.getExtendedProperties("prayer");
			for(final Prayers prayer:prop.getActivePrayers())
				switch(prayer){
				case PROTECT_MAGIC:
				{
					if(e.source.isMagicDamage())
						e.ammount /= 2F;
					break;
				}
				case PROTECT_RANGE:
				{
					if(e.source.isProjectile() && !e.source.isMagicDamage())
						e.ammount /= 2F;
					break;
				}
				case PROTECT_MELEE:
				{
					if((e.source.getSourceOfDamage() != null) && (e.source.getSourceOfDamage() instanceof EntityLiving))
						e.ammount /= 2F;
					break;
				}
				default:
					break;
				}
		}
	}

	@SubscribeEvent
	public void onLivingDeath(final LivingDropsEvent e){
		if(e.entityLiving.getRNG().nextInt(4) == 0)
			return;
		if((e.entityLiving.width*e.entityLiving.height) < 2.0F)
			e.drops.add(new EntityItem(e.entityLiving.worldObj, e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, new ItemStack(ModItems.basicBone, e.lootingLevel+1, 0)));
		else
			e.drops.add(new EntityItem(e.entityLiving.worldObj, e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, new ItemStack(ModItems.basicBone, e.lootingLevel+1, 1)));
	}

}
