package me.superckl.prayers.init.loot;

import java.util.List;

import com.google.gson.JsonObject;

import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public class SpawnerCurioLoot extends LootModifier{

	protected SpawnerCurioLoot(final ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
		generatedLoot.add(new ItemStack(ModItems.RELICS.get(ItemBoon.CURIOS)::get));
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<SpawnerCurioLoot>{

		@Override
		public SpawnerCurioLoot read(final ResourceLocation location, final JsonObject object, final ILootCondition[] ailootcondition) {
			return new SpawnerCurioLoot(ailootcondition);
		}

		@Override
		public JsonObject write(final SpawnerCurioLoot instance) {
			return super.makeConditions(instance.conditions);
		}

	}

}
