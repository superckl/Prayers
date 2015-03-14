package me.superckl.prayers.common.potion;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionPrayerRestoreInstant extends Potion{

	public PotionPrayerRestoreInstant(final int id) {
		super(id, false, 0x6cfef9);
		this.setPotionName("potion.prayerrestoreinstant.name");
	}

	@Override
	public void affectEntity(final EntityLivingBase thrower, final EntityLivingBase entity, final int amplifier, final double p_76402_4_) {
		if((entity instanceof EntityPlayer) == false)
			return;
		final EntityPlayer player = (EntityPlayer) entity;
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		final float max = prop.getMaxPrayerPoints();
		final float points = prop.getPrayerPoints();
		if(max <= points)
			return;
		final float toRestore = 150F*(amplifier+1);
		if((max - points) <= toRestore)
			prop.setPrayerPoints(max);
		else
			prop.setPrayerPoints(points+toRestore);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

}
