package me.superckl.prayers.common.potion;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionPrayerBoost extends Potion{

	public PotionPrayerBoost(final int id) {
		super(id, false, 0x6cfef9);
		this.setPotionName("potion.prayerboost.name");
	}

	@Override
	public void removeAttributesModifiersFromEntity(final EntityLivingBase entity, final BaseAttributeMap p_111187_2_, final int p_111187_3_) {
		super.removeAttributesModifiersFromEntity(entity, p_111187_2_, p_111187_3_);
		if(!entity.isPotionActive(this) && (entity instanceof EntityPlayer)){
			final EntityPlayer player = (EntityPlayer) entity;
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
			if(prop.getPrayerPoints()>prop.getMaxPrayerPoints())
				prop.setPrayerPoints(prop.getMaxPrayerPoints());
		}
	}

	public float getBoostFor(final EntityLivingBase entity){
		if(!entity.isPotionActive(this))
			return 0F;
		return (entity.getActivePotionEffect(this).getAmplifier()+1)*200F;
	}

}
