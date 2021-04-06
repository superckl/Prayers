package me.superckl.prayers.init.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.LivingPrayerUser;
import me.superckl.prayers.init.ModLoot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

@RequiredArgsConstructor
public class PrayerActiveCondition implements ILootCondition{

	private final int level;

	@Override
	public boolean test(final LootContext t) {
		final Entity entity = t.getParamOrNull(LootParameters.THIS_ENTITY);
		if(entity instanceof LivingEntity) {
			final LivingPrayerUser<?> user = CapabilityHandler.getPrayerCapability((LivingEntity) entity);
			return user.getActivePrayers().stream().anyMatch(prayer -> prayer.getLevel() >= this.level);
		}
		return false;
	}

	@Override
	public LootConditionType getType() {
		return ModLoot.PRAYER_ACTIVE_CONDITION;
	}

	public static class Serializer implements ILootSerializer<PrayerActiveCondition>{

		public static final String LEVEL_KEY = "level";

		@Override
		public void serialize(final JsonObject obj, final PrayerActiveCondition condition, final JsonSerializationContext context) {
			obj.addProperty(Serializer.LEVEL_KEY, condition.level);
		}

		@Override
		public PrayerActiveCondition deserialize(final JsonObject obj, final JsonDeserializationContext context) {
			return new PrayerActiveCondition(JSONUtils.getAsInt(obj, Serializer.LEVEL_KEY));
		}

	}

}
