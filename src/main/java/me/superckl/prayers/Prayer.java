package me.superckl.prayers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import me.superckl.prayers.effects.DamageEffect;
import me.superckl.prayers.effects.DamageEffect.DamageType;
import me.superckl.prayers.effects.PrayerEffect;
import me.superckl.prayers.user.IPrayerUser;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Getter
public class Prayer extends ForgeRegistryEntry<Prayer>{

	public static final Prayer.PrayerBuilder POTENCY_1 = Prayer.builder().name("Effective")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/effective.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.3F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))
			.registryName(new ResourceLocation(Prayers.MOD_ID, "effective"));
	public static final Prayer.PrayerBuilder POTENCY_2 = Prayer.builder().name("Potent")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/potency.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.5F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))
			.registryName(new ResourceLocation(Prayers.MOD_ID, "potent"));

	public static final Prayer.PrayerBuilder PROTECT_MELEE = Prayer.builder().name("Protect Melee")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmelee.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, true, true, -0.5F)).exclusionType("protect")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "protect_melee"));
	public static final Prayer.PrayerBuilder PROTECT_MAGIC = Prayer.builder().name("Protect Magic")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmagic.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, true, true, -0.5F)).exclusionType("protect")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "protect_magic"));
	public static final Prayer.PrayerBuilder PROTECT_RANGE = Prayer.builder().name("Protect Range")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectrange.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, true, true, -0.5F)).exclusionType("protect")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "protect_range"));
	public static final Prayer.PrayerBuilder PROTECT_ITEM = Prayer.builder().name("Protect Item")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectitem.png"))
			.registryName(new ResourceLocation(Prayers.MOD_ID, "protect_item"));

	public static final Prayer.PrayerBuilder ENHANCE_MELEE_1 = Prayer.builder().name("Might")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee1.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.05F)).exclusionType("enhance_melee")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_melee_1"));
	public static final Prayer.PrayerBuilder ENHANCE_MELEE_2 = Prayer.builder().name("Strength")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee2.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.15F)).exclusionType("enhance_melee")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_melee_2"));
	public static final Prayer.PrayerBuilder ENHANCE_MELEE_3 = Prayer.builder().name("Vigor")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee3.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.25F)).exclusionType("enhance_melee")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_melee_3"));

	public static final Prayer.PrayerBuilder ENHANCE_MAGIC_1 = Prayer.builder().name("Mystic Charge")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic1.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.05F)).exclusionType("enhance_magic")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_magic_1"));
	public static final Prayer.PrayerBuilder ENHANCE_MAGIC_2 = Prayer.builder().name("Mystic Will")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic2.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.15F)).exclusionType("enhance_magic")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_magic_2"));
	public static final Prayer.PrayerBuilder ENHANCE_MAGIC_3 = Prayer.builder().name("Mystic Might")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic3.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.25F)).exclusionType("enhance_magic")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_magic_3"));

	public static final Prayer.PrayerBuilder ENHANCE_RANGE_1 = Prayer.builder().name("Watchful")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange1.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.05F)).exclusionType("enhance_range")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_range_1"));
	public static final Prayer.PrayerBuilder ENHANCE_RANGE_2 = Prayer.builder().name("Sharp Eye")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange2.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.15F)).exclusionType("enhance_range")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_range_2"));
	public static final Prayer.PrayerBuilder ENHANCE_RANGE_3 = Prayer.builder().name("Hawk Eye")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange3.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.25F)).exclusionType("enhance_range")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_range_3"));

	public static final Prayer.PrayerBuilder ENHANCE_DEFENCE_1 = Prayer.builder().name("Tough")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence1.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.05F)).exclusionType("enhance_defence")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_defence_1"));
	public static final Prayer.PrayerBuilder ENHANCE_DEFENCE_2 = Prayer.builder().name("Durable")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence2.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.15F)).exclusionType("enhance_defence")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_defence_2"));
	public static final Prayer.PrayerBuilder ENHANCE_DEFENCE_3 = Prayer.builder().name("Robust")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence3.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.25F)).exclusionType("enhance_defence")
			.registryName(new ResourceLocation(Prayers.MOD_ID, "enhance_defence_3"));

	private final String name;
	private final float drain;
	private final List<PrayerEffect> effects;
	private final List<String> exclusionTypes;
	private final ResourceLocation texture;
	private final List<ITextComponent> tooltipDescription;
	@Setter
	private boolean enabled = true;

	//We require a supplier for effects so that a new instance can be created every time the prayer is built.
	@Builder
	private Prayer(final String name, final float drain, @Singular final List<Supplier<PrayerEffect>> effects, @Singular final List<String> exclusionTypes,
			final ResourceLocation texture, final ResourceLocation registryName) {
		this.name = name;
		this.drain = drain;
		this.texture = texture;
		this.exclusionTypes = exclusionTypes;

		this.effects = effects.stream().map(Supplier::get).collect(Collectors.toList());
		this.effects.forEach(effect -> {
			effect.setOwner(this);
			if(effect.hasListener())
				MinecraftForge.EVENT_BUS.register(effect);
		});
		this.tooltipDescription = Lists.newArrayList(new StringTextComponent(this.name));
		this.effects.forEach(effect -> this.tooltipDescription.add(new StringTextComponent(effect.getDescription())
				.setStyle(Style.EMPTY.setItalic(true))));
		this.setRegistryName(registryName);
	}

	public boolean isActive(final Entity entity) {
		final LazyOptional<IPrayerUser> opt = entity.getCapability(Prayers.PRAYER_USER_CAPABILITY);
		return opt.isPresent() && opt.orElse(null).isPrayerActive(this);
	}

	public static List<Prayer> all(){
		return Lists.newArrayList(Prayer.POTENCY_1, Prayer.POTENCY_2,
				Prayer.ENHANCE_MELEE_1, Prayer.ENHANCE_MELEE_2, Prayer.ENHANCE_MELEE_3,
				Prayer.ENHANCE_MAGIC_1, Prayer.ENHANCE_MAGIC_2, Prayer.ENHANCE_MAGIC_3,
				Prayer.ENHANCE_RANGE_1, Prayer.ENHANCE_RANGE_2, Prayer.ENHANCE_RANGE_3,
				Prayer.ENHANCE_DEFENCE_1, Prayer.ENHANCE_DEFENCE_2, Prayer.ENHANCE_DEFENCE_3,
				Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE).stream().map(PrayerBuilder::build).collect(Collectors.toList());
	}

	public static List<ResourceLocation> allLocations(){
		return Lists.newArrayList(Prayer.POTENCY_1, Prayer.POTENCY_2,
				Prayer.ENHANCE_MELEE_1, Prayer.ENHANCE_MELEE_2, Prayer.ENHANCE_MELEE_3,
				Prayer.ENHANCE_MAGIC_1, Prayer.ENHANCE_MAGIC_2, Prayer.ENHANCE_MAGIC_3,
				Prayer.ENHANCE_RANGE_1, Prayer.ENHANCE_RANGE_2, Prayer.ENHANCE_RANGE_3,
				Prayer.ENHANCE_DEFENCE_1, Prayer.ENHANCE_DEFENCE_2, Prayer.ENHANCE_DEFENCE_3,
				Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE).stream().map(PrayerBuilder::build)
				.map(Prayer::getRegistryName).collect(Collectors.toList());
	}

}
