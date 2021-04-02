package me.superckl.prayers.prayer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.effects.DamageEffect;
import me.superckl.prayers.effects.DamageEffect.DamageType;
import me.superckl.prayers.effects.FireProtEffect;
import me.superckl.prayers.effects.FlightEffect;
import me.superckl.prayers.effects.PoisonProtEffect;
import me.superckl.prayers.effects.PrayerEffect;
import me.superckl.prayers.effects.TemptAnimalEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Getter
public class Prayer extends ForgeRegistryEntry<Prayer>{

	public static final DeferredRegister<Prayer> REGISTER = DeferredRegister.create(Prayer.class, Prayers.MOD_ID);

	public static final RegistryObject<Prayer> POTENCY_1 = Prayer.REGISTER.register("effective", Prayer.builder().drain(2.5F).level(60)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/effective.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.3F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))::build);
	public static final RegistryObject<Prayer> POTENCY_2 = Prayer.REGISTER.register("potent", Prayer.builder().drain(5F).level(80)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/potency.png")).requiresTome(true)
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.5F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))::build);

	public static final RegistryObject<Prayer> PROTECT_MELEE = Prayer.REGISTER.register("protect_melee", Prayer.builder().drain(2.5F).level(43)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmelee.png")).overhead(true)
			.effect(() -> new DamageEffect(DamageType.MELEE, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_MAGIC = Prayer.REGISTER.register("protect_magic", Prayer.builder().drain(2.5F).level(37)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmagic.png")).overhead(true)
			.effect(() -> new DamageEffect(DamageType.MAGIC, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_RANGE = Prayer.REGISTER.register("protect_range", Prayer.builder().drain(2.5F).level(40)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectrange.png")).overhead(true)
			.effect(() -> new DamageEffect(DamageType.RANGE, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_ITEM = Prayer.REGISTER.register("protect_item", Prayer.builder().drain(0.2F).level(25)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectitem.png"))::build);
	public static final RegistryObject<Prayer> PROTECT_POISON = Prayer.REGISTER.register("protect_poison", Prayer.builder().drain(1F).level(20)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectpoison.png")).effect(PoisonProtEffect::new)::build);
	public static final RegistryObject<Prayer> PROTECT_FIRE = Prayer.REGISTER.register("protect_fire", Prayer.builder().drain(2.5F).level(35)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectfire.png")).effect(FireProtEffect::new)::build);

	public static final RegistryObject<Prayer> ENHANCE_MELEE_1 = Prayer.REGISTER.register("enhance_melee_1", Prayer.builder().drain(0.2F).level(4)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee1.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.05F)).exclusionType("enhance_melee")::build);
	public static final RegistryObject<Prayer> ENHANCE_MELEE_2 = Prayer.REGISTER.register("enhance_melee_2", Prayer.builder().drain(1F).level(13)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee2.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.15F)).exclusionType("enhance_melee")::build);
	public static final RegistryObject<Prayer> ENHANCE_MELEE_3 = Prayer.REGISTER.register("enhance_melee_3", Prayer.builder().drain(3F).level(31)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee3.png"))
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.25F)).exclusionType("enhance_melee")::build);

	public static final RegistryObject<Prayer> ENHANCE_MAGIC_1 = Prayer.REGISTER.register("enhance_magic_1", Prayer.builder().drain(0.2F).level(9)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic1.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.05F)).exclusionType("enhance_magic")::build);
	public static final RegistryObject<Prayer> ENHANCE_MAGIC_2 = Prayer.REGISTER.register("enhance_magic_2", Prayer.builder().drain(1F).level(27)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic2.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.15F)).exclusionType("enhance_magic")::build);
	public static final RegistryObject<Prayer> ENHANCE_MAGIC_3 = Prayer.REGISTER.register("enhance_magic_3", Prayer.builder().drain(3F).level(45)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic3.png"))
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.25F)).exclusionType("enhance_magic")::build);

	public static final RegistryObject<Prayer> ENHANCE_RANGE_1 = Prayer.REGISTER.register("enhance_range_1", Prayer.builder().drain(0.2F).level(8)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange1.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.05F)).exclusionType("enhance_range")::build);
	public static final RegistryObject<Prayer> ENHANCE_RANGE_2 = Prayer.REGISTER.register("enhance_range_2", Prayer.builder().drain(1F).level(26)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange2.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.15F)).exclusionType("enhance_range")::build);
	public static final RegistryObject<Prayer> ENHANCE_RANGE_3 = Prayer.REGISTER.register("enhance_range_3", Prayer.builder().drain(3F).level(44)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange3.png"))
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.25F)).exclusionType("enhance_range")::build);

	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_1 = Prayer.REGISTER.register("enhance_defence_1", Prayer.builder().drain(0.16F).level(1)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence1.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.05F)).exclusionType("enhance_defence")::build);
	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_2 = Prayer.REGISTER.register("enhance_defence_2", Prayer.builder().drain(0.7F).level(10)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence2.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.15F)).exclusionType("enhance_defence")::build);
	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_3 = Prayer.REGISTER.register("enhance_defence_3", Prayer.builder().drain(2F).level(28)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence3.png"))
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.25F)).exclusionType("enhance_defence")::build);

	public static final RegistryObject<Prayer> ARK = Prayer.REGISTER.register("ark", Prayer.builder().drain(1F).level(15)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/ark.png")).effect(TemptAnimalEffect::new)::build);
	public static final RegistryObject<Prayer> FLIGHT = Prayer.REGISTER.register("flight", Prayer.builder().drain(10F).level(99)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/flight.png")).onActivate(PrayerActivationEffects::onActivateFlight)
			.onDeactivate(PrayerActivationEffects::onDeactivateFlight).effect(FlightEffect::new).requiresTome(true)::build);

	private final float drain;
	private final int level;
	private final List<PrayerEffect> effects;
	@Getter(AccessLevel.PRIVATE)
	private final Consumer<LivingEntity> onActivate;
	@Getter(AccessLevel.PRIVATE)
	private final Consumer<LivingEntity> onDeactivate;
	private final List<String> exclusionTypes;
	private final boolean requiresTome;
	private final boolean overhead;
	private final ResourceLocation texture;
	private List<ITextComponent> tooltipDescription;
	private IFormattableTextComponent name;
	@Setter
	private boolean enabled = true;

	//We require a supplier for effects so that a new instance can be created every time the prayer is built.
	@Builder
	private Prayer(final float drain, final int level, @Singular final List<Supplier<PrayerEffect>> effects, @Singular final List<String> exclusionTypes,
			Consumer<LivingEntity> onActivate, Consumer<LivingEntity> onDeactivate, final boolean requiresTome, final boolean overhead, @NonNull final ResourceLocation texture) {
		this.drain = drain;
		this.level = level;
		this.texture = texture;
		this.exclusionTypes = exclusionTypes;
		this.requiresTome = requiresTome;
		this.overhead = overhead;
		if(onActivate == null)
			onActivate = entity -> {};
			if(onDeactivate == null)
				onDeactivate = entity -> {};
				this.onActivate = onActivate;
				this.onDeactivate = onDeactivate;

				this.effects = effects.stream().map(Supplier::get).collect(Collectors.toList());
				this.effects.forEach(effect -> {
					effect.setOwner(this);
					if(effect.hasListener())
						MinecraftForge.EVENT_BUS.register(effect);
				});
	}

	public IFormattableTextComponent getName() {
		if(this.name == null)
			this.name = new TranslationTextComponent(Util.makeDescriptionId("prayer", this.getRegistryName()));
		return this.name;
	}

	public List<ITextComponent> getTooltipDescription(){
		if(this.tooltipDescription == null) {
			this.tooltipDescription = Lists.newArrayList(this.getName());
			this.effects.forEach(effect -> this.tooltipDescription.add(effect.getDescription().setStyle(Style.EMPTY.withItalic(true))));
		}
		return this.tooltipDescription;
	}

	public boolean isActive(final LivingEntity entity) {
		return CapabilityHandler.getPrayerCapability(entity).isPrayerActive(this);
	}

	public boolean isObfusctated(final PlayerEntity player) {
		final PlayerPrayerUser user = CapabilityHandler.getPrayerCapability(player);
		return this.requiresTome && !user.isUnlocked(this) || user.getPrayerLevel() < this.getLevel();
	}

	public void onActivate(final LivingEntity e) {
		this.onActivate.accept(e);
	}

	public void onDeactivate(final LivingEntity e) {
		this.onDeactivate.accept(e);
	}

	public static List<RegistryObject<? extends Prayer>> defaultObjects(){
		return Lists.newArrayList(Prayer.POTENCY_1, Prayer.POTENCY_2,
				Prayer.ENHANCE_MELEE_1, Prayer.ENHANCE_MELEE_2, Prayer.ENHANCE_MELEE_3,
				Prayer.ENHANCE_MAGIC_1, Prayer.ENHANCE_MAGIC_2, Prayer.ENHANCE_MAGIC_3,
				Prayer.ENHANCE_RANGE_1, Prayer.ENHANCE_RANGE_2, Prayer.ENHANCE_RANGE_3,
				Prayer.ENHANCE_DEFENCE_1, Prayer.ENHANCE_DEFENCE_2, Prayer.ENHANCE_DEFENCE_3,
				Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE, Prayer.PROTECT_ITEM,
				Prayer.PROTECT_FIRE);
	}

	public static List<Prayer> defaults(){
		return Prayer.defaultObjects().stream().map(RegistryObject::get).collect(Collectors.toList());
	}

	public static List<ResourceLocation> defaultLocations(){
		return Prayer.defaultObjects().stream().map(RegistryObject::getId).collect(Collectors.toList());
	}

}