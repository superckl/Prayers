package me.superckl.prayers.criteria;

import com.google.gson.JsonObject;

import me.superckl.prayers.Prayers;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class PrayerLevelCriteria extends AbstractCriterionTrigger<PrayerLevelCriteria.Instance>{

	public static final String LEVEL_KEY = "level";
	public static final ResourceLocation ID = new ResourceLocation(Prayers.MOD_ID, "prayer_level");
	public static final PrayerLevelCriteria INSTANCE = new PrayerLevelCriteria();

	@Override
	public ResourceLocation getId() {
		return PrayerLevelCriteria.ID;
	}

	public void trigger(final ServerPlayerEntity player, final int level) {
		this.trigger(player, inst -> level >= inst.level);
	}

	@Override
	protected Instance createInstance(final JsonObject obj, final AndPredicate pred,
			final ConditionArrayParser parser) {
		return new Instance(this.getId(), pred, JSONUtils.getAsInt(obj, PrayerLevelCriteria.LEVEL_KEY));
	}

	public static class Instance extends CriterionInstance{

		private final int level;

		public Instance(final ResourceLocation rLoc, final AndPredicate pred, final int level) {
			super(rLoc, pred);
			this.level = level;
		}

		@Override
		public JsonObject serializeToJson(final ConditionArraySerializer p_230240_1_) {
			final JsonObject obj = super.serializeToJson(p_230240_1_);
			obj.addProperty(PrayerLevelCriteria.LEVEL_KEY, this.level);
			return obj;
		}

	}

}
