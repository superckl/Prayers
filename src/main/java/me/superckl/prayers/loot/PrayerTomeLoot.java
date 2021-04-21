package me.superckl.prayers.loot;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.PrayerTomeItem;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IRegistryDelegate;

public class PrayerTomeLoot extends LootModifier{

	private final List<IRegistryDelegate<Prayer>> disallowed;

	protected PrayerTomeLoot(final ILootCondition[] conditionsIn, final List<IRegistryDelegate<Prayer>> disallowed) {
		super(conditionsIn);
		this.disallowed = disallowed;
	}

	@Override
	protected List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
		final List<Prayer> prayers = Prayer.REGISTRY.get().getValues().stream()
				.filter(Prayer::isRequiresTome).filter(prayer -> !this.disallowed.contains(prayer.delegate)).collect(Collectors.toCollection(Lists::newArrayList));
		final Prayer prayer = prayers.get(context.getRandom().nextInt(prayers.size()));
		final ItemStack stack = new ItemStack(ModItems.PRAYER_TOME::get);
		ModItems.PRAYER_TOME.get();
		PrayerTomeItem.storePrayer(stack, prayer);
		generatedLoot.add(stack);
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<PrayerTomeLoot>{

		public static final String DISALLOWED_PRAYERS = "disallowed_prayers";

		@Override
		public PrayerTomeLoot read(final ResourceLocation location, final JsonObject object, final ILootCondition[] ailootcondition) {
			final List<IRegistryDelegate<Prayer>> disallowed = Lists.newArrayList();
			final IForgeRegistry<Prayer> registry = Prayer.REGISTRY.get();
			if(object.has(Serializer.DISALLOWED_PRAYERS)) {
				final JsonArray allowedJson = JSONUtils.getAsJsonArray(object, Serializer.DISALLOWED_PRAYERS);
				allowedJson.forEach(element -> disallowed.add(registry.getValue(new ResourceLocation(element.getAsString())).delegate));
			}
			return new PrayerTomeLoot(ailootcondition, disallowed);
		}

		@Override
		public JsonObject write(final PrayerTomeLoot instance) {
			final JsonObject obj = this.makeConditions(instance.conditions);
			final JsonArray disallowedJson = new JsonArray();
			instance.disallowed.forEach(delegate -> disallowedJson.add(delegate.name().toString()));
			obj.add(Serializer.DISALLOWED_PRAYERS, disallowedJson);
			return obj;
		}

	}

}
