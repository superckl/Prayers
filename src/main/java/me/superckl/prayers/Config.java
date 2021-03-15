package me.superckl.prayers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {

	@Getter
	private static Config instance;
	@Getter
	private final ConfigValue<List<String>> prayers;

	private Config(final ForgeConfigSpec.Builder builder) {
		final List<String> prayerLocs = Prayer.defaultLocations().stream().map(ResourceLocation::toString).collect(Collectors.toList());
		builder.comment("List of prayer resource resource locations to enable. Defaults are ").comment(prayerLocs.toArray(new String[prayerLocs.size()]));
		this.prayers = builder.define("Prayers", prayerLocs);
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
		final List<String> enabled = this.prayers.get();
		GameRegistry.findRegistry(Prayer.class).getValues().forEach(prayer ->
		prayer.setEnabled(enabled.contains(prayer.getRegistryName().toString()))
				);
	}

}
