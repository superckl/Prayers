package me.superckl.prayers;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Getter;
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

	//These are the default prayers, and are not explicitly registered anywhere. They are written to JSON via a data generator and registered from files.

	public static final Prayer POTENCY_1 = new Prayer.PrayerBuilder().name("Effective")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/effective.png"))
			.effect(new DamageEffect(DamageType.ALL, false, true, 0.3F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "effective"));
	public static final Prayer POTENCY_2 = new Prayer.PrayerBuilder().name("Potent")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/potency.png"))
			.effect(new DamageEffect(DamageType.ALL, false, true, 0.5F))
			.exclusionTypes(Lists.newArrayList("enhance_melee", "enhance_range", "enhance_magic"))
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "potent"));

	public static final Prayer PROTECT_MELEE = new Prayer.PrayerBuilder().name("Protect Melee")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmelee.png"))
			.effect(new DamageEffect(DamageType.MELEE, true, true, -0.5F)).exclusionType("protect")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "protect_melee"));
	public static final Prayer PROTECT_MAGIC = new Prayer.PrayerBuilder().name("Protect Magic")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectmagic.png"))
			.effect(new DamageEffect(DamageType.MAGIC, true, true, -0.5F)).exclusionType("protect")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "protect_magic"));
	public static final Prayer PROTECT_RANGE = new Prayer.PrayerBuilder().name("Protect Range")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectrange.png"))
			.effect(new DamageEffect(DamageType.RANGE, true, true, -0.5F)).exclusionType("protect")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "protect_range"));
	public static final Prayer PROTECT_ITEM = new Prayer.PrayerBuilder().name("Protect Item")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/protectitem.png"))
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "protect_item"));

	public static final Prayer ENHANCE_MELEE_1 = new Prayer.PrayerBuilder().name("Might")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee1.png"))
			.effect(new DamageEffect(DamageType.MELEE, false, true, 0.05F)).exclusionType("enhance_melee")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_melee_1"));
	public static final Prayer ENHANCE_MELEE_2 = new Prayer.PrayerBuilder().name("Strength")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee2.png"))
			.effect(new DamageEffect(DamageType.MELEE, false, true, 0.15F)).exclusionType("enhance_melee")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_melee_2"));
	public static final Prayer ENHANCE_MELEE_3 = new Prayer.PrayerBuilder().name("Vigor")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemelee3.png"))
			.effect(new DamageEffect(DamageType.MELEE, false, true, 0.25F)).exclusionType("enhance_melee")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_melee_3"));

	public static final Prayer ENHANCE_MAGIC_1 = new Prayer.PrayerBuilder().name("Mystic Charge")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic1.png"))
			.effect(new DamageEffect(DamageType.MAGIC, false, true, 0.05F)).exclusionType("enhance_magic")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_magic_1"));
	public static final Prayer ENHANCE_MAGIC_2 = new Prayer.PrayerBuilder().name("Mystic Will")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic2.png"))
			.effect(new DamageEffect(DamageType.MAGIC, false, true, 0.15F)).exclusionType("enhance_magic")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_magic_2"));
	public static final Prayer ENHANCE_MAGIC_3 = new Prayer.PrayerBuilder().name("Mystic Might")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancemagic3.png"))
			.effect(new DamageEffect(DamageType.MAGIC, false, true, 0.25F)).exclusionType("enhance_magic")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_magic_3"));

	public static final Prayer ENHANCE_RANGE_1 = new Prayer.PrayerBuilder().name("Watchful")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange1.png"))
			.effect(new DamageEffect(DamageType.RANGE, false, true, 0.05F)).exclusionType("enhance_range")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_range_1"));
	public static final Prayer ENHANCE_RANGE_2 = new Prayer.PrayerBuilder().name("Sharp Eye")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange2.png"))
			.effect(new DamageEffect(DamageType.RANGE, false, true, 0.15F)).exclusionType("enhance_range")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_range_2"));
	public static final Prayer ENHANCE_RANGE_3 = new Prayer.PrayerBuilder().name("Hawk Eye")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancerange3.png"))
			.effect(new DamageEffect(DamageType.RANGE, false, true, 0.25F)).exclusionType("enhance_range")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_range_3"));

	public static final Prayer ENHANCE_DEFENCE_1 = new Prayer.PrayerBuilder().name("Tough")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence1.png"))
			.effect(new DamageEffect(DamageType.ALL, true, true, -0.05F)).exclusionType("enhance_defence")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_defence_1"));
	public static final Prayer ENHANCE_DEFENCE_2 = new Prayer.PrayerBuilder().name("Durable")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence2.png"))
			.effect(new DamageEffect(DamageType.ALL, true, true, -0.15F)).exclusionType("enhance_defence")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_defence_2"));
	public static final Prayer ENHANCE_DEFENCE_3 = new Prayer.PrayerBuilder().name("Robust")
			.texture(new ResourceLocation(Prayers.MOD_ID, "textures/prayer/enhancedefence3.png"))
			.effect(new DamageEffect(DamageType.ALL, true, true, -0.25F)).exclusionType("enhance_defence")
			.build().setRegistryName(new ResourceLocation(Prayers.MOD_ID, "enhance_defence_3"));

	private final String name;
	private final float drain;
	private final List<PrayerEffect> effects;
	private final List<String> exclusionTypes;
	private final ResourceLocation texture;
	private final List<ITextComponent> tooltipDescription;

	@Builder
	private Prayer(final String name, final float drain, @Singular final List<PrayerEffect> effects, @Singular final List<String> exclusionTypes, final ResourceLocation texture) {
		this.name = name;
		this.drain = drain;
		this.texture = texture;
		this.exclusionTypes = exclusionTypes;

		effects.forEach(effect -> {
			effect.setOwner(this);
			if(effect.hasListener())
				MinecraftForge.EVENT_BUS.register(effect);
		});
		this.effects = effects;
		this.tooltipDescription = Lists.newArrayList(new StringTextComponent(this.name));
		this.effects.forEach(effect -> this.tooltipDescription.add(new StringTextComponent(effect.getDescription()).setStyle(Style.EMPTY.setItalic(true))));
	}

	public boolean isEnabled(final Entity entity) {
		final LazyOptional<IPrayerUser> opt = entity.getCapability(Prayers.PRAYER_USER_CAPABILITY);
		return opt.isPresent() && opt.orElse(null).isPrayerActive(this);
	}

}
