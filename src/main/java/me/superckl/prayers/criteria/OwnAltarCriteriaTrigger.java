package me.superckl.prayers.criteria;

import com.google.common.base.Predicates;
import com.google.gson.JsonObject;

import me.superckl.prayers.Prayers;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class OwnAltarCriteriaTrigger extends AbstractCriterionTrigger<OwnAltarCriteriaTrigger.Instance>{

	public static final ResourceLocation ID = new ResourceLocation(Prayers.MOD_ID, "own_altar");
	public static final OwnAltarCriteriaTrigger INSTANCE = new OwnAltarCriteriaTrigger();

	@Override
	public ResourceLocation getId() {
		return OwnAltarCriteriaTrigger.ID;
	}

	public void trigger(final ServerPlayerEntity player) {
		this.trigger(player, Predicates.alwaysTrue());
	}

	@Override
	protected Instance createInstance(final JsonObject obj, final AndPredicate pred,
			final ConditionArrayParser parser) {
		return new Instance(this.getId(), pred);
	}

	public static class Instance extends CriterionInstance{

		public Instance(final ResourceLocation rLoc, final AndPredicate pred) {
			super(rLoc, pred);
		}

	}

}
