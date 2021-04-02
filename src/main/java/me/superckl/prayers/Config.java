package me.superckl.prayers;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import lombok.Getter;
import me.superckl.prayers.item.decree.DecreeItem;
import me.superckl.prayers.item.decree.DecreeItem.Type;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {

	@Getter
	private static Config instance;
	@Getter
	private final ConfigValue<List<String>> prayers;
	@Getter
	private final Map<DecreeItem.Type, IntValue> decreeRanges = new EnumMap<>(DecreeItem.Type.class);
	@Getter
	private final DoubleValue fertilityCropChance;
	@Getter
	private final DoubleValue fertilityAnimalChance;

	private Config(final ForgeConfigSpec.Builder builder) {
		final List<String> prayerLocs = Prayer.defaultLocations().stream().map(ResourceLocation::toString).collect(Collectors.toList());
		final String[] comment = new String[prayerLocs.size()+1];
		comment[0] = "List of prayer resource locations to disable. Defaults prayers are ";
		for(int i = 0; i < prayerLocs.size(); i++)
			comment[i + 1] = prayerLocs.get(i);
		builder.comment(comment);
		this.prayers = builder.define("Prayers", Lists.newArrayList());

		builder.comment("Radius, in blocks, of the decree of fertility. (default 9)");
		this.decreeRanges.put(Type.FERTILITY, builder.defineInRange("Decree.Decree of Fertility Range", 9, 0, Integer.MAX_VALUE));
		builder.comment("This controls how often the decree of fertility will apply bonemeal to crops within its radius. (default .0003125)",
				"A random value (0,1) is checked against this value every tick; .0003125 means on average every 160 seconds.",
				"This might sound like a lot, but a 9x9 farm has ~80 blocks, so one crop bonemeals every 2 seconds.");
		this.fertilityCropChance = builder.defineInRange("Decree.Decree of Fertility Crop Chance", .0003125, 0, 1);
		builder.comment("This controls how often the decree of fertility will apply food to animals. (default .0003125)",
				"A random value (0,1) is checked against this value every tick; .0003125 means on average every 160 seconds.",
				"This might sound like a lot, but with 10 animals one will get fed every 16 seconds.");
		this.fertilityAnimalChance = builder.defineInRange("Decree.Decree of Fertility Animal Chance", .0003125, 0, 1);

		builder.comment("Radius, in blocks, of the decree of infertility. (default 9)");
		this.decreeRanges.put(Type.INFERTILITY, builder.defineInRange("Decree.Decree of Infertility Range", 9, 0, Integer.MAX_VALUE));

		builder.comment("Radius, in blocks, of the decree of sanctuary. (default 30)");
		this.decreeRanges.put(Type.SANCTUARY, builder.defineInRange("Decree.Decree of Sanctuary Range", 30, 0, Integer.MAX_VALUE));
	}

	public static ForgeConfigSpec setup() {
		if (Config.instance != null)
			throw new IllegalStateException("Config has already been setup!");
		final Pair<Config, ForgeConfigSpec> specPair =  new ForgeConfigSpec.Builder().configure(Config::new);
		Config.instance = specPair.getKey();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config.instance::onConfigLoad);
		return specPair.getValue();
	}

	private void onConfigLoad(final ModConfigEvent e) {
		this.applyConfig();
	}

	private void applyConfig() {
		final List<String> disabled = this.prayers.get();
		GameRegistry.findRegistry(Prayer.class).getValues().forEach(prayer ->
		prayer.setEnabled(!disabled.contains(prayer.getRegistryName().toString())));
	}

}
