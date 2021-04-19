package me.superckl.prayers.loot;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootSerializers;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public class GenericItemLoot extends LootModifier{

	private final ILootFunction[] functions;
	private final ItemStack stack;

	protected GenericItemLoot(final ILootCondition[] conditionsIn, final ILootFunction[] functions, final ItemStack item) {
		super(conditionsIn);
		this.functions = functions;
		this.stack = item;
	}

	@Override
	protected List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
		ItemStack modifiedStack = this.stack.copy();
		for(final ILootFunction function:this.functions)
			modifiedStack = function.apply(modifiedStack, context);
		generatedLoot.add(modifiedStack);
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<GenericItemLoot>{

		private final Gson functionSerializer = LootSerializers.createFunctionSerializer().create();

		@Override
		public GenericItemLoot read(final ResourceLocation location, final JsonObject object, final ILootCondition[] ailootcondition) {
			final ILootFunction[] functions;
			if(object.has("functions")) {
				final JsonArray array = JSONUtils.getAsJsonArray(object, "functions");
				functions = new ILootFunction[array.size()];
				for(int i = 0; i < functions.length; i++)
					functions[i] = this.functionSerializer.fromJson(array.get(i), ILootFunction.class);
			}else
				functions = new ILootFunction[0];
			final ItemStack item = new ItemStack(JSONUtils.getAsItem(object, "item"));
			return new GenericItemLoot(ailootcondition, functions, item);
		}

		@Override
		public JsonObject write(final GenericItemLoot instance) {
			final JsonObject obj = super.makeConditions(instance.conditions);
			if(instance.functions.length != 0) {
				final JsonArray array = new JsonArray();
				for(final ILootFunction function:instance.functions)
					array.add(this.functionSerializer.toJson(function));
				obj.add("functions", array);
			}
			obj.addProperty("item", instance.stack.getItem().getRegistryName().toString());
			return obj;
		}

	}

}
