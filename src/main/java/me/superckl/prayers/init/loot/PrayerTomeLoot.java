package me.superckl.prayers.init.loot;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IRegistryDelegate;

public class PrayerTomeLoot extends LootModifier{

	private final List<IRegistryDelegate<Prayer>> allowed;

	protected PrayerTomeLoot(final ILootCondition[] conditionsIn, final List<IRegistryDelegate<Prayer>> allowed) {
		super(conditionsIn);
		this.allowed = allowed;
	}

	@Override
	protected List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
		final Prayer prayer = this.allowed.get(context.getRandom().nextInt(this.allowed.size())).get();
		final ItemStack stack = new ItemStack(ModItems.PRAYER_TOME::get);
		ModItems.PRAYER_TOME.get().storePrayer(stack, prayer);
		generatedLoot.add(stack);
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<PrayerTomeLoot>{

		public static final String ALLOWED_PRAYERS = "allowed_prayers";

		@Override
		public PrayerTomeLoot read(final ResourceLocation location, final JsonObject object, final ILootCondition[] ailootcondition) {
			List<IRegistryDelegate<Prayer>> allowed;
			final IForgeRegistry<Prayer> registry = GameRegistry.findRegistry(Prayer.class);
			if(object.has(Serializer.ALLOWED_PRAYERS)) {
				allowed = Lists.newArrayList();
				final JsonArray allowedJson = JSONUtils.getAsJsonArray(object, Serializer.ALLOWED_PRAYERS);
				allowedJson.forEach(element -> allowed.add(registry.getValue(new ResourceLocation(element.getAsString())).delegate));
			}else {
				final Collection<Prayer> prayers = Lists.newArrayList(registry.getValues());
				prayers.removeIf(prayer -> !prayer.isRequiresTome());
				allowed = prayers.stream().map(prayer -> prayer.delegate).collect(Collectors.toList());
			}
			return new PrayerTomeLoot(ailootcondition, allowed);
		}

		@Override
		public JsonObject write(final PrayerTomeLoot instance) {
			final JsonObject obj = this.makeConditions(instance.conditions);
			final JsonArray allowedJson = new JsonArray();
			instance.allowed.forEach(delegate -> allowedJson.add(delegate.name().toString()));
			obj.add(Serializer.ALLOWED_PRAYERS, allowedJson);
			return obj;
		}

	}

}
