package me.superckl.prayers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import me.superckl.prayers.capability.ILivingPrayerUser;
import me.superckl.prayers.effects.DamageEffect;
import me.superckl.prayers.effects.DamageEffect.DamageType;
import me.superckl.prayers.effects.PrayerEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Getter
public class Prayer extends ForgeRegistryEntry<Prayer>{

	public static final DeferredRegister<Prayer> REGISTER = DeferredRegister.create(Prayer.class, Prayers.MOD_ID);

	public static final RegistryObject<Prayer> POTENCY_1 = Prayer.REGISTER.register("effective", Prayer.builder().name("Effective").drain(2.5F).level(60)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/effective.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.3F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))::build);
	public static final RegistryObject<Prayer> POTENCY_2 = Prayer.REGISTER.register("potent", Prayer.builder().name("Potent").drain(5F).level(80)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/potency.png")).requiresTome(true)
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.5F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))::build);

	public static final RegistryObject<Prayer> PROTECT_MELEE = Prayer.REGISTER.register("protect_melee", Prayer.builder().name("Protect Melee").drain(2.5F).level(43)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmelee.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_MAGIC = Prayer.REGISTER.register("protect_magic", Prayer.builder().name("Protect Magic").drain(2.5F).level(37)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmagic.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_RANGE = Prayer.REGISTER.register("protect_range", Prayer.builder().name("Protect Range").drain(2.5F).level(40)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectrange.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_ITEM = Prayer.REGISTER.register("protect_item", Prayer.builder().name("Protect Item").drain(0.2F).level(25)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectitem.png"))::build);

	public static final RegistryObject<Prayer> ENHANCE_MELEE_1 = Prayer.REGISTER.register("enhance_melee_1", Prayer.builder().name("Might").drain(0.2F).level(4)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee1.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.05F)).exclusionType("enhance_melee")::build);
	public static final RegistryObject<Prayer> ENHANCE_MELEE_2 = Prayer.REGISTER.register("enhance_melee_2", Prayer.builder().name("Strength").drain(1F).level(13)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee2.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.15F)).exclusionType("enhance_melee")::build);
	public static final RegistryObject<Prayer> ENHANCE_MELEE_3 = Prayer.REGISTER.register("enhance_melee_3", Prayer.builder().name("Vigor").drain(3F).level(31)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee3.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.25F)).exclusionType("enhance_melee")::build);

	public static final RegistryObject<Prayer> ENHANCE_MAGIC_1 = Prayer.REGISTER.register("enhance_magic_1", Prayer.builder().name("Mystic Charge").drain(0.2F).level(9)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic1.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.05F)).exclusionType("enhance_magic")::build);
	public static final RegistryObject<Prayer> ENHANCE_MAGIC_2 = Prayer.REGISTER.register("enhance_magic_2", Prayer.builder().name("Mystic Will").drain(1F).level(27)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic2.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.15F)).exclusionType("enhance_magic")::build);
	public static final RegistryObject<Prayer> ENHANCE_MAGIC_3 = Prayer.REGISTER.register("enhance_magic_3", Prayer.builder().name("Mystic Might").drain(3F).level(45)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic3.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.25F)).exclusionType("enhance_magic")::build);

	public static final RegistryObject<Prayer> ENHANCE_RANGE_1 = Prayer.REGISTER.register("enhance_range_1", Prayer.builder().name("Watchful").drain(0.2F).level(8)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange1.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.05F)).exclusionType("enhance_range")::build);
	public static final RegistryObject<Prayer> ENHANCE_RANGE_2 = Prayer.REGISTER.register("enhance_range_2", Prayer.builder().name("Sharp Eye").drain(1F).level(26)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange2.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.15F)).exclusionType("enhance_range")::build);
	public static final RegistryObject<Prayer> ENHANCE_RANGE_3 = Prayer.REGISTER.register("enhance_range_3", Prayer.builder().name("Hawk Eye").drain(3F).level(44)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange3.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.25F)).exclusionType("enhance_range")::build);

	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_1 = Prayer.REGISTER.register("enhance_defence_1", Prayer.builder().name("Tough").drain(0.16F).level(1)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence1.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.05F)).exclusionType("enhance_defence")::build);
	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_2 = Prayer.REGISTER.register("enhance_defence_2", Prayer.builder().name("Durable").drain(0.7F).level(10)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence2.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.15F)).exclusionType("enhance_defence")::build);
	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_3 = Prayer.REGISTER.register("enhance_defence_3", Prayer.builder().name("Robust").drain(2F).level(28)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence3.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.25F)).exclusionType("enhance_defence")::build);

	private final String name;
	private final float drain;
	private final int level;
	private final List<PrayerEffect> effects;
	private final List<String> exclusionTypes;
	private final boolean requiresTome;
	private final ResourceLocation texture;
	private final List<ITextComponent> tooltipDescription;
	@Setter
	private boolean enabled = true;

	//We require a supplier for effects so that a new instance can be created every time the prayer is built.
	@Builder
	private Prayer(final String name, final float drain, final int level, @Singular final List<Supplier<PrayerEffect>> effects, @Singular final List<String> exclusionTypes,
			final boolean requiresTome, final ResourceLocation texture) {
		this.name = name;
		this.drain = drain;
		this.level = level;
		this.texture = texture;
		this.exclusionTypes = exclusionTypes;
		this.requiresTome = requiresTome;

		this.effects = effects.stream().map(Supplier::get).collect(Collectors.toList());
		this.effects.forEach(effect -> {
			effect.setOwner(this);
			if(effect.hasListener())
				MinecraftForge.EVENT_BUS.register(effect);
		});
		this.tooltipDescription = Lists.newArrayList(new StringTextComponent(this.name));
		this.effects.forEach(effect -> this.tooltipDescription.add(new StringTextComponent(effect.getDescription())
				.setStyle(Style.EMPTY.withItalic(true))));
	}

	public boolean isActive(final LivingEntity entity) {
		return ILivingPrayerUser.get(entity).isPrayerActive(this);
	}

	public static List<Prayer> defaults(){
		return Lists.newArrayList(Prayer.POTENCY_1, Prayer.POTENCY_2,
				Prayer.ENHANCE_MELEE_1, Prayer.ENHANCE_MELEE_2, Prayer.ENHANCE_MELEE_3,
				Prayer.ENHANCE_MAGIC_1, Prayer.ENHANCE_MAGIC_2, Prayer.ENHANCE_MAGIC_3,
				Prayer.ENHANCE_RANGE_1, Prayer.ENHANCE_RANGE_2, Prayer.ENHANCE_RANGE_3,
				Prayer.ENHANCE_DEFENCE_1, Prayer.ENHANCE_DEFENCE_2, Prayer.ENHANCE_DEFENCE_3,
				Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE).stream().map(RegistryObject::get).collect(Collectors.toList());
	}

	public static List<ResourceLocation> defaultLocations(){
		return Lists.newArrayList(Prayer.POTENCY_1, Prayer.POTENCY_2,
				Prayer.ENHANCE_MELEE_1, Prayer.ENHANCE_MELEE_2, Prayer.ENHANCE_MELEE_3,
				Prayer.ENHANCE_MAGIC_1, Prayer.ENHANCE_MAGIC_2, Prayer.ENHANCE_MAGIC_3,
				Prayer.ENHANCE_RANGE_1, Prayer.ENHANCE_RANGE_2, Prayer.ENHANCE_RANGE_3,
				Prayer.ENHANCE_DEFENCE_1, Prayer.ENHANCE_DEFENCE_2, Prayer.ENHANCE_DEFENCE_3,
				Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE).stream().map(RegistryObject::getId).collect(Collectors.toList());
	}

}
