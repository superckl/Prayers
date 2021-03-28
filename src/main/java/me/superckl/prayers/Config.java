package me.superckl.prayers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

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
		final String[] comment = new String[prayerLocs.size()+1];
		comment[0] = "List of prayer resource locations to disable. Defaults prayers are ";
		for(int i = 0; i < prayerLocs.size(); i++)
			comment[i + 1] = prayerLocs.get(i);
		builder.comment(comment);
		this.prayers = builder.define("Prayers", Lists.newArrayList());
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
