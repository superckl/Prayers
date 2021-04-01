package me.superckl.prayers.effects;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.entity.ai.AnimalFollowPrayerGoal;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TemptAnimalEffect extends PrayerEffect{

	private final ResourceLocation tagLoc = new ResourceLocation(Prayers.MOD_ID, "prayer_temptable");

	@SubscribeEvent
	public void onEntityJoinWorld(final EntityJoinWorldEvent e) {
		if(e.getEntity() instanceof MobEntity && e.getEntity().getType().getTags().contains(this.tagLoc))
			((MobEntity)e.getEntity()).goalSelector.addGoal(3, new AnimalFollowPrayerGoal((MobEntity) e.getEntity()));
	}

	@Override
	public boolean hasListener() {
		return true;
	}

	@Override
	public IFormattableTextComponent getDescription() {
		return new TranslationTextComponent(Util.makeDescriptionId("prayer_effect", new ResourceLocation(Prayers.MOD_ID, "tempt_animal")));
	}

}
