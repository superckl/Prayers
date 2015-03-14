package me.superckl.prayers.common.potion;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionPrayerDrain extends Potion{

	public PotionPrayerDrain(final int id) {
		super(id, true, 0x7f0303);
		this.setPotionName("potion.prayerdrain.name");
	}

	@Override
	public void performEffect(final EntityLivingBase entity, final int amplifier) {
		if((entity instanceof EntityPlayer) == false)
			return;
		final EntityPlayer player = (EntityPlayer) entity;
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		final float points = prop.getPrayerPoints();
		final float toDrain = (amplifier+1);
		if((points - toDrain) <= 0)
			prop.setPrayerPoints(0);
		else
			prop.setPrayerPoints(points-toDrain);

	}

	@Override
	public boolean isReady(final int tick, final int modifier) {
		final int k = 50 >> modifier;
		return k > 0 ? (tick % k) == 0 : true;
	}

}
