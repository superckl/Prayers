package me.superckl.prayers.common.potion;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionPrayerRestore extends Potion{

	public PotionPrayerRestore(final int id) {
		super(id, false, 0x7f0303);
		this.setPotionName("potion.prayerrestore.name");
		//this.setPotionName(I18n.format("potion.prayerrestore.name"));
	}

	@Override
	public void performEffect(final EntityLivingBase entity, final int amplifier) {
		if((entity instanceof EntityPlayer) == false)
			return;
		final EntityPlayer player = (EntityPlayer) entity;
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) player.getExtendedProperties("prayer");
		final float max = prop.getMaxPrayerPoints();
		final float points = prop.getPrayerPoints();
		if(max <= points)
			return;
		final float toRestore = 2F*(amplifier+1);
		if((max - points) <= toRestore)
			prop.setPrayerPoints(max);
		prop.setPrayerPoints(points+toRestore);

	}

	@Override
	public boolean isReady(final int tick, final int modifier) {
		final int k = 50 >> modifier;
		return k > 0 ? (tick % k) == 0 : true;
	}



}
