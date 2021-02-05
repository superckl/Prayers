package me.superckl.prayers.datagen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.superckl.prayers.LogHelper;
import me.superckl.prayers.Prayer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

public class PrayerDataGenerator implements IDataProvider{

	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Prayer.class, new Prayer.Serializer()).setPrettyPrinting().create();
	protected final DataGenerator generator;

	public PrayerDataGenerator(final DataGenerator generatorIn) {
		this.generator = generatorIn;
	}

	@Override
	public void act(final DirectoryCache cache) throws IOException {
		final Path path = this.generator.getOutputFolder();
		final Set<ResourceLocation> set = Sets.newHashSet();
		this.registerPrayers(prayer -> {
			if(!set.add(prayer.getRegistryName()))
				throw new IllegalStateException("Duplicate recipe "+prayer.getRegistryName());
			else
				PrayerDataGenerator.savePrayer(cache, prayer, path.resolve("data/" + prayer.getRegistryName().getNamespace() + "/prayers/" + prayer.getRegistryName().getPath() + ".json"));
		});
	}

	protected static void savePrayer(final DirectoryCache cache, final Prayer prayer, final Path path) {
		try {
			final String s = PrayerDataGenerator.GSON.toJson(prayer);
			final String s1 = IDataProvider.HASH_FUNCTION.hashUnencodedChars(s).toString();
			if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
				Files.createDirectories(path);

				try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
					bufferedwriter.write(s);
				}
			}
		} catch (final IOException e) {
			LogHelper.error("Unable to save prayer to "+path);
			e.printStackTrace();
		}
	}

	protected void registerPrayers(final Consumer<Prayer> consumer) {

	}

	@Override
	public String getName() {
		return "Prayers";
	}

}
