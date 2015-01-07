package me.superckl.prayers.common.potion;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionRaiseMaxPoints extends Potion{

	public PotionRaiseMaxPoints(final int id) {
		super(id, false, 0x7f0303);
		this.setPotionName("potion.maxpointsraise.name");
	}

	@Override
	public void affectEntity(final EntityLivingBase thrower, final EntityLivingBase entity, final int amplifier, final double p_76402_4_) {
		if((entity instanceof EntityPlayer) == false)
			return;
		final EntityPlayer player = (EntityPlayer) entity;
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		prop.setBaseMaxPrayerPoints(prop.getBaseMaxPrayerPoints()+(10F*(amplifier+1)));
	}

	@Override
	public boolean isInstant() {
		return true;
	}

}
