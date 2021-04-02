package me.superckl.prayers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import lombok.Getter;
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
	private final IntValue fertilityRange;
	@Getter
	private final DoubleValue fertilityChance;

	private Config(final ForgeConfigSpec.Builder builder) {
		final List<String> prayerLocs = Prayer.defaultLocations().stream().map(ResourceLocation::toString).collect(Collectors.toList());
		final String[] comment = new String[prayerLocs.size()+1];
		comment[0] = "List of prayer resource locations to disable. Defaults prayers are ";
		for(int i = 0; i < prayerLocs.size(); i++)
			comment[i + 1] = prayerLocs.get(i);
		builder.comment(comment);
		this.prayers = builder.define("Prayers", Lists.newArrayList());
		builder.comment("Radius, in blocks, of the decree of fertility. (default 9)");
		this.fertilityRange = builder.defineInRange("Decree of Fertility Range", 9, 0, Integer.MAX_VALUE);
		builder.comment("This controls how often the decree of fertility will apply bonemeal to crops within its radius. (default .0003125)",
				"A random value (0,1) is checked against this value every tick; .0003125 means on average every 160 seconds.",
				"This might sound like a lot, but a 9x9 farm has ~80 blocks, so one crop bonemeals every 2 seconds.");
		this.fertilityChance = builder.defineInRange("Decree of Fertility Chance", .0003125, 0, 1);
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
