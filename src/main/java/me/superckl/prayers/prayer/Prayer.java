package me.superckl.prayers.prayer;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.capability.PlayerPrayerUser;
import me.superckl.prayers.effects.DamageEffect;
import me.superckl.prayers.effects.DamageEffect.DamageType;
import me.superckl.prayers.effects.DigSpeedEffect;
import me.superckl.prayers.effects.FireProtEffect;
import me.superckl.prayers.effects.FlightEffect;
import me.superckl.prayers.effects.MovementSpeedEffect;
import me.superckl.prayers.effects.PoisonProtEffect;
import me.superckl.prayers.effects.PrayerEffect;
import me.superckl.prayers.effects.TemptAnimalEffect;
import me.superckl.prayers.effects.entity.EntitySpecificEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Getter
public class Prayer extends ForgeRegistryEntry<Prayer>{

	public static final DeferredRegister<Prayer> REGISTER = DeferredRegister.create(Prayer.class, Prayers.MOD_ID);

	public static final RegistryObject<Prayer> POTENCY_1 = Prayer.REGISTER.register("effective", Prayer.builder().drain(2.5F).level(60)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/effective.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.3F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))::build);
	public static final RegistryObject<Prayer> POTENCY_2 = Prayer.REGISTER.register("potent", Prayer.builder().drain(5F).level(80)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/potency.png")).requiresTome(true).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.ALL, false, true, 0.5F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))::build);

	public static final RegistryObject<Prayer> PROTECT_MELEE = Prayer.REGISTER.register("protect_melee", Prayer.builder().drain(2.5F).level(43)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmelee.png")).overhead(true).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MELEE, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_MAGIC = Prayer.REGISTER.register("protect_magic", Prayer.builder().drain(2.5F).level(37)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmagic.png")).overhead(true).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MAGIC, true, true, -0.5F)).exclusionType("protect")::build);
	public static final RegistryObject<Prayer> PROTECT_RANGE = Prayer.REGISTER.register("protect_range", Prayer.builder().drain(2.5F).level(40)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectrange.png")).overhead(true).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.RANGE, true, true, -0.5F)).exclusionType("protect")::build);
	//	public static final RegistryObject<Prayer> PROTECT_ITEM = Prayer.REGISTER.register("protect_item", Prayer.builder().drain(0.2F).level(25)
	//			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectitem.png"))::build);
	public static final RegistryObject<Prayer> PROTECT_POISON = Prayer.REGISTER.register("protect_poison", Prayer.builder().drain(1F).level(20).group(Group.UTILITY)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectpoison.png")).effect(PoisonProtEffect::new)::build);
	public static final RegistryObject<Prayer> PROTECT_FIRE = Prayer.REGISTER.register("protect_fire", Prayer.builder().drain(2.5F).level(20).group(Group.UTILITY)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectfire.png")).effect(FireProtEffect::new)::build);

	public static final RegistryObject<Prayer> ENHANCE_MELEE_1 = Prayer.REGISTER.register("enhance_melee_1", Prayer.builder().drain(0.2F).level(4)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee1.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.05F)).exclusionType("enhance_melee")::build);
	public static final RegistryObject<Prayer> ENHANCE_MELEE_2 = Prayer.REGISTER.register("enhance_melee_2", Prayer.builder().drain(1F).level(13)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee2.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.15F)).exclusionType("enhance_melee")::build);
	public static final RegistryObject<Prayer> ENHANCE_MELEE_3 = Prayer.REGISTER.register("enhance_melee_3", Prayer.builder().drain(3F).level(31)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee3.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MELEE, false, true, 0.25F)).exclusionType("enhance_melee")::build);

	public static final RegistryObject<Prayer> ENHANCE_MAGIC_1 = Prayer.REGISTER.register("enhance_magic_1", Prayer.builder().drain(0.2F).level(9)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic1.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.05F)).exclusionType("enhance_magic")::build);
	public static final RegistryObject<Prayer> ENHANCE_MAGIC_2 = Prayer.REGISTER.register("enhance_magic_2", Prayer.builder().drain(1F).level(27)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic2.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.15F)).exclusionType("enhance_magic")::build);
	public static final RegistryObject<Prayer> ENHANCE_MAGIC_3 = Prayer.REGISTER.register("enhance_magic_3", Prayer.builder().drain(3F).level(45)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic3.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.MAGIC, false, true, 0.25F)).exclusionType("enhance_magic")::build);

	public static final RegistryObject<Prayer> ENHANCE_RANGE_1 = Prayer.REGISTER.register("enhance_range_1", Prayer.builder().drain(0.2F).level(8)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange1.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.05F)).exclusionType("enhance_range")::build);
	public static final RegistryObject<Prayer> ENHANCE_RANGE_2 = Prayer.REGISTER.register("enhance_range_2", Prayer.builder().drain(1F).level(26)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange2.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.15F)).exclusionType("enhance_range")::build);
	public static final RegistryObject<Prayer> ENHANCE_RANGE_3 = Prayer.REGISTER.register("enhance_range_3", Prayer.builder().drain(3F).level(44)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange3.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.RANGE, false, true, 0.25F)).exclusionType("enhance_range")::build);

	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_1 = Prayer.REGISTER.register("enhance_defence_1", Prayer.builder().drain(0.16F).level(1)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence1.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.05F)).exclusionType("enhance_defence")::build);
	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_2 = Prayer.REGISTER.register("enhance_defence_2", Prayer.builder().drain(0.7F).level(10)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence2.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.15F)).exclusionType("enhance_defence")::build);
	public static final RegistryObject<Prayer> ENHANCE_DEFENCE_3 = Prayer.REGISTER.register("enhance_defence_3", Prayer.builder().drain(2F).level(28)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence3.png")).group(Group.COMBAT)
			.effect(() -> new DamageEffect(DamageType.ALL, true, true, -0.25F)).exclusionType("enhance_defence")::build);

	public static final RegistryObject<Prayer> ARK = Prayer.REGISTER.register("ark", Prayer.builder().drain(1F).level(15).group(Group.UTILITY)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/ark.png")).effect(TemptAnimalEffect::new)::build);
	public static final RegistryObject<Prayer> FLIGHT = Prayer.REGISTER.register("flight", Prayer.builder().drain(10F).level(99).group(Group.UTILITY)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/flight.png")).effect(FlightEffect::new).requiresTome(true)::build);

	public static final RegistryObject<Prayer> DIG_SPEED_1 = Prayer.REGISTER.register("dig_speed_1", Prayer.builder().drain(0.12F).level(1)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/digspeed1.png")).group(Group.UTILITY)
			.effect(() -> new DigSpeedEffect(.2F)).exclusionType("dig_speed")::build);
	public static final RegistryObject<Prayer> DIG_SPEED_2 = Prayer.REGISTER.register("dig_speed_2", Prayer.builder().drain(0.5F).level(13)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/digspeed2.png")).group(Group.UTILITY)
			.effect(() -> new DigSpeedEffect(.75F)).exclusionType("dig_speed")::build);
	public static final RegistryObject<Prayer> DIG_SPEED_3 = Prayer.REGISTER.register("dig_speed_3", Prayer.builder().drain(2F).level(38)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/digspeed3.png")).group(Group.UTILITY)
			.effect(() -> new DigSpeedEffect(1.5F)).exclusionType("dig_speed")::build);

	public static final RegistryObject<Prayer> MOVEMENT_SPEED_1 = Prayer.REGISTER.register("movement_speed_1", Prayer.builder().drain(0.1F).level(1)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/movementspeed1.png")).group(Group.UTILITY)
			.effect(() -> new MovementSpeedEffect(0.2F)).exclusionType("movement_speed")::build);
	public static final RegistryObject<Prayer> MOVEMENT_SPEED_2 = Prayer.REGISTER.register("movement_speed_2", Prayer.builder().drain(0.4F).level(10)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/movementspeed2.png")).group(Group.UTILITY)
			.effect(() -> new MovementSpeedEffect(0.4F)).exclusionType("movement_speed")::build);
	public static final RegistryObject<Prayer> MOVEMENT_SPEED_3 = Prayer.REGISTER.register("movement_speed_3", Prayer.builder().drain(1.5F).level(30)
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/movementspeed3.png")).group(Group.UTILITY)
			.effect(() -> new MovementSpeedEffect(0.6F)).exclusionType("movement_speed")::build);

	private final float drain;
	private final int level;
	private final List<PrayerEffect> effects;
	private final List<String> exclusionTypes;
	private final boolean requiresTome;
	private final boolean overhead;
	private final Group group;
	private final ResourceLocation texture;
	private List<ITextComponent> tooltipDescription;
	private IFormattableTextComponent name;
	@Setter
	private boolean enabled = true;

	//We require a supplier for effects so that a new instance can be created every time the prayer is built.
	@Builder
	private Prayer(final float drain, final int level, @Singular final List<Supplier<PrayerEffect>> effects, @Singular final List<String> exclusionTypes,
			final boolean requiresTome, final boolean overhead, Group group,@NonNull final ResourceLocation texture) {
		this.drain = drain;
		this.level = level;
		this.texture = texture;
		this.exclusionTypes = exclusionTypes;
		this.requiresTome = requiresTome;
		this.overhead = overhead;
		if(group == null)
			group = Group.ALL;
		this.group = group;

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
		return !user.isUnlocked() || this.requiresTome && !user.isUnlocked(this) || user.getPrayerLevel() < this.getLevel();
	}

	public List<EntitySpecificEffect<?>> attachEffects(final LivingEntity entity){
		return this.effects.stream().filter(effect -> effect.canAttachTo(entity))
				.map(effect -> effect.attachTo(entity)).collect(Collectors.toList());
	}

	public boolean isIn(final Group group) {
		return group == Group.ALL || group == this.group;
	}

	public static List<RegistryObject<? extends Prayer>> defaultObjects(){
		return Lists.newArrayList(Prayer.POTENCY_1, Prayer.POTENCY_2,
				Prayer.ENHANCE_MELEE_1, Prayer.ENHANCE_MELEE_2, Prayer.ENHANCE_MELEE_3,
				Prayer.ENHANCE_MAGIC_1, Prayer.ENHANCE_MAGIC_2, Prayer.ENHANCE_MAGIC_3,
				Prayer.ENHANCE_RANGE_1, Prayer.ENHANCE_RANGE_2, Prayer.ENHANCE_RANGE_3,
				Prayer.ENHANCE_DEFENCE_1, Prayer.ENHANCE_DEFENCE_2, Prayer.ENHANCE_DEFENCE_3,
				Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE,
				Prayer.PROTECT_FIRE);
	}

	public static List<Prayer> defaults(){
		return Prayer.defaultObjects().stream().map(RegistryObject::get).collect(Collectors.toList());
	}

	public static List<ResourceLocation> defaultLocations(){
		return Prayer.defaultObjects().stream().map(RegistryObject::getId).collect(Collectors.toList());
	}

	public static Stream<Prayer> allForGroup(final Group group){
		Stream<Prayer> stream =  GameRegistry.findRegistry(Prayer.class).getValues().stream().filter(Prayer::isEnabled);
		if(group != Group.ALL)
			stream = stream.filter(prayer -> group == Group.ALL || prayer.group == group);
		return stream;
	}

	/*
	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}*/

	@RequiredArgsConstructor
	public enum Group {

		ALL(ItemStack.EMPTY),
		COMBAT(new ItemStack(Items.DIAMOND_SWORD)),
		UTILITY(new ItemStack(Items.DIAMOND_PICKAXE));

		private TranslationTextComponent name;
		private final ItemStack stack;

		public IFormattableTextComponent getName() {
			if(this.name == null)
				this.name = new TranslationTextComponent(Util.makeDescriptionId("prayer_group", new ResourceLocation(Prayers.MOD_ID, this.name().toLowerCase())));
			return this.name;
		}

		public ItemStack getDisplayItem() {
			return this.stack;
		}

	}

}
