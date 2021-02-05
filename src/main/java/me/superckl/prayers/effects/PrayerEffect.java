package me.superckl.prayers.effects;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import net.minecraft.util.ResourceLocation;

public abstract class PrayerEffect implements Cloneable{

	private static final Map<Class<? extends PrayerEffect>, EffectEntry<?>> CLASS_LOOKUP = new IdentityHashMap<>();
	private static final Map<ResourceLocation, EffectEntry<?>> NAME_LOOKUP = new HashMap<>();

	static {
		PrayerEffect.registerEffect(DamageEffect.class, new ResourceLocation(Prayers.MOD_ID, "damage_modifier"), new DamageEffect.Serializer());
	}

	@Getter
	private Prayer owner;

	public void setOwner(final Prayer owner) {
		if(this.owner != null)
			throw new IllegalStateException("Owner has already been set!");
		this.owner = owner;
	}

	public abstract boolean hasListener();

	public abstract String getDescription();

	@Override
	public abstract PrayerEffect clone();

	public static <T extends PrayerEffect> void registerEffect(final Class<T> clazz, final ResourceLocation name, final EffectSerializer<T> serializer) {
		PrayerEffect.CLASS_LOOKUP.put(clazz, new EffectEntry<>(name, clazz, serializer));
		PrayerEffect.NAME_LOOKUP.put(name, new EffectEntry<>(name, clazz, serializer));
	}

	@SuppressWarnings("unchecked")
	public static <T extends PrayerEffect> EffectEntry<T> lookup(final Class<T> clazz){
		return (EffectEntry<T>) PrayerEffect.CLASS_LOOKUP.get(clazz);
	}

	public static EffectEntry<?> lookup(final ResourceLocation name){
		return PrayerEffect.NAME_LOOKUP.get(name);
	}

	public static GsonBuilder registerGsonAdapters(final GsonBuilder builder) {
		for (final EffectEntry<?> e: PrayerEffect.CLASS_LOOKUP.values())
			builder.registerTypeAdapter(e.getEffectClass(), e.getSerializer());
		return builder;
	}

	public interface EffectSerializer<T extends PrayerEffect> extends JsonSerializer<T>, JsonDeserializer<T> {}

	@RequiredArgsConstructor
	@Getter
	public static class EffectEntry<T extends PrayerEffect>{

		private final ResourceLocation name;
		private final Class<T> effectClass;
		private final EffectSerializer<T> serializer;

	}

}
