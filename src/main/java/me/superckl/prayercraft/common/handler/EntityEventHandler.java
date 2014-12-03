package me.superckl.prayercraft.common.handler;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.prayer.Prayers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityConstruction(final EntityConstructing e){
		if(e.entity instanceof EntityPlayer)
			e.entity.registerExtendedProperties("prayer", new PrayerExtendedProperties());
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

}
