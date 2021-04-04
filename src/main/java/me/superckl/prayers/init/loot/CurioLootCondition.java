package me.superckl.prayers.init.loot;

import java.lang.reflect.InvocationTargetException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import me.superckl.prayers.LogHelper;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.init.ModLoot;
import me.superckl.prayers.util.ReflectionCache;
import me.superckl.prayers.util.ReflectionCache.Methods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class CurioLootCondition implements ILootCondition{

	@Override
	public boolean test(final LootContext t) {
		final Entity victim = t.getParamOrNull(LootParameters.THIS_ENTITY);
		try {
			if(victim instanceof MobEntity && (Boolean) ReflectionCache.INSTANCE.get(Methods.MOB_ENTITY__SHOULD_DESPAWN_IN_PEACEFUL).invoke(victim)) {
				final Entity killer = t.getParamOrNull(LootParameters.KILLER_ENTITY);
				if(killer instanceof LivingEntity) {
					final LivingEntity lEntity = (LivingEntity) killer;
					return ItemBoon.CURIOS.has(lEntity.getMainHandItem()) || ItemBoon.CURIOS.has(lEntity.getOffhandItem());
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LogHelper.info("Failed to process loot for entity "+victim);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public LootConditionType getType() {
		return ModLoot.CURIO_CONDITION;
	}

	public static class Serializer implements ILootSerializer<CurioLootCondition>{

		@Override
		public void serialize(final JsonObject p_230424_1_, final CurioLootCondition p_230424_2_, final JsonSerializationContext p_230424_3_) {}

		@Override
		public CurioLootCondition deserialize(final JsonObject p_230423_1_, final JsonDeserializationContext p_230423_2_) {
			return new CurioLootCondition();
		}

	}

}
