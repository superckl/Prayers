package me.superckl.prayercraft.common.utility;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import me.superckl.prayercraft.common.prayer.Prayers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

@RequiredArgsConstructor
public class PotionEffectHashMap extends HashMap<Integer, PotionEffect>{

	private final EntityLivingBase entity;

	public PotionEffectHashMap(final EntityLivingBase entity, final Map<Integer, PotionEffect> m) {
		super(m);
		this.entity = entity;
	}

	@Override
	public PotionEffect put(final Integer key, final PotionEffect value) {
		LogHelper.info("putting");
		if(PrayerHelper.getActivePrayers(this.entity).contains(Prayers.ENHANCE_POTION)){
			//value = new PotionEffect(value);
			value.duration *= 1.2;
			LogHelper.info("Extended to "+value.duration);
		}
		return super.put(key, value);
	}

	@Override
	public void putAll(final Map<? extends Integer, ? extends PotionEffect> m) {
		if(PrayerHelper.getActivePrayers(this.entity).contains(Prayers.ENHANCE_POTION))
			for(final PotionEffect value:m.values())
				//value = new PotionEffect(value);
				value.duration *= 1.2;
		super.putAll(m);
	}



}