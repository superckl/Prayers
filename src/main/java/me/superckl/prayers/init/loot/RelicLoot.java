package me.superckl.prayers.init.loot;

import java.util.List;

import com.google.gson.JsonObject;

import me.superckl.prayers.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public class RelicLoot extends LootModifier{

	protected RelicLoot(final ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
		generatedLoot.add(new ItemStack(ModItems.RELICS.get(context.getRandom().nextInt(ModItems.RELICS.size()))::get));
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<RelicLoot>{

		@Override
		public RelicLoot read(final ResourceLocation location, final JsonObject object, final ILootCondition[] ailootcondition) {
			return new RelicLoot(ailootcondition);
		}

		@Override
		public JsonObject write(final RelicLoot instance) {
			return new JsonObject();
		}

	}

}
