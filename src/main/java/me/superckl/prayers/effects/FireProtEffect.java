package me.superckl.prayers.effects;

import me.superckl.prayers.Prayers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FireProtEffect extends PrayerEffect{

	@SubscribeEvent(priority =  EventPriority.HIGH)
	public void onLivingHurt(final LivingAttackEvent e) {
		if(e.getSource().isFire() && this.getOwner().isActive(e.getEntityLiving()))
			e.setCanceled(true);
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public ITextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "fire_prot")));
	}

}
