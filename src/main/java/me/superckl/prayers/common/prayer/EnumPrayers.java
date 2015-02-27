package me.superckl.prayers.common.prayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.collect.ImmutableList;

@Getter
@RequiredArgsConstructor
public enum EnumPrayers {

	//I would be using EnumSets for the requiredAfter, but it causes an initialization exception
	DESTRUCTIVISM("destructivism", EnumChatFormatting.RED+"Destructivism", Arrays.asList("Allows you to break altars."), 0.1F, true, false, Collections.EMPTY_LIST, new ResourceLocation("textures/items/fire.png")),
	ENCHANCE_DEFENCE_1("eDefence1", EnumChatFormatting.YELLOW+"Tough", Arrays.asList("Increases defence by 5%"), 0.1F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancedefence1.png")),
	ENHANCE_MELEE_1("eMelee1", EnumChatFormatting.YELLOW+"Might", Arrays.asList("Increases melee strength by 5%"), 0.15F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancemelee1.png")),
	ENHANCE_RANGE_1("eRange1", EnumChatFormatting.YELLOW+"Watchful", Arrays.asList("Increases ranged strength by 5%"), 0.15F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancerange1.png")),
	ENHANCE_POTION("ePotion", EnumChatFormatting.YELLOW+"Enhance Potions", Arrays.asList("Boosts potion duration by 20%"), 1F, false, true, Collections.EMPTY_LIST, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MAGIC_1("eMagic1", EnumChatFormatting.YELLOW+"Mystic Charge", Arrays.asList("Increases magic strength by 5%"), 0.15F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancemagic1.png")),
	ENCHANCE_DEFENCE_2("eDefence2", EnumChatFormatting.YELLOW+"Durable", Arrays.asList("Increases defence by 15%"), 0.2F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancedefence2.png")),
	ENHANCE_MELEE_2("eMelee2", EnumChatFormatting.YELLOW+"Strength", Arrays.asList("Increases melee strength by 15%"), 0.25F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancemelee2.png")),
	ENHANCE_RANGE_2("eRange2", EnumChatFormatting.YELLOW+"Sharp Eye", Arrays.asList("Increases ranged strength by 15%"), 0.25F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancerange2.png")),
	ENHANCE_MAGIC_2("eMagic2", EnumChatFormatting.YELLOW+"Mystic Will", Arrays.asList("Increases magic strength by 15%"), 0.25F, false, false, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancemagic2.png")),
	ENCHANCE_DEFENCE_3("eDefence3", EnumChatFormatting.YELLOW+"Robust", Arrays.asList("Increases defence by 25%"), 0.5F, false, true, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancedefence3.png")),
	ENHANCE_MELEE_3("eMelee3", EnumChatFormatting.YELLOW+"Vigor", Arrays.asList("Increases melee strength by 25%"), 0.6F, false, true, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancemelee3.png")),
	ENHANCE_RANGE_3("eRange3", EnumChatFormatting.YELLOW+"Hawk Eye", Arrays.asList("Increases ranged strength by 25%"), 0.6F, false, true, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancerange3.png")),
	ENHANCE_MAGIC_3("eMagic3", EnumChatFormatting.YELLOW+"Mystic Might", Arrays.asList("Increases magic strength by 25%"), 0.6F, true, true, Collections.EMPTY_LIST, new ResourceLocation(ModData.MOD_ID+":textures/prayer/enhancemagic3.png")),
	PROTECT_MELEE("pMelee", EnumChatFormatting.AQUA+"Protect from Melee", Arrays.asList("Reduces melee damage by 50%"), 2.5F, true, true, Collections.EMPTY_LIST, new ResourceLocation("textures/items/diamond_sword.png")),
	PROTECT_RANGE("pRange", EnumChatFormatting.AQUA+"Protect from Range", Arrays.asList("Reduces ranged damage by 50%"), 2.5F, true, true, Collections.EMPTY_LIST, new ResourceLocation("textures/items/arrow.png")),
	PROTECT_MAGIC("pMagic", EnumChatFormatting.AQUA+"Protect from Magic", Arrays.asList("Reduces magic damage by 50%"), 2.5F, true, true, Collections.EMPTY_LIST, new ResourceLocation("textures/items/fire.png")),
	POTENCY_1("eAll1", EnumChatFormatting.AQUA+"Effective", Arrays.asList("Increases damage output by 30%"), 5F, false, true, ImmutableList.of(ENHANCE_MAGIC_3, ENHANCE_RANGE_3, ENHANCE_MELEE_3), new ResourceLocation(ModData.MOD_ID+":textures/prayer/effective.png")),
	POTENCY_2("eAll2", EnumChatFormatting.AQUA+"Potent", Arrays.asList("Increases damage output by 50%"), 7F, false, true, ImmutableList.of(POTENCY_1), new ResourceLocation(ModData.MOD_ID+":textures/prayer/potency.png"));//TODO

	public static final float MAX_DRAIN = 7F;

	private final String id;
	private final String displayName;
	private final List<String> description;
	/**
	 * Calculated every second (20 ticks)
	 */
	private final float drain;
	private final boolean overhead;
	private final boolean requiresTome;
	private final List<EnumPrayers> requiredAfter;
	private final ResourceLocation texture;

	public static EnumPrayers getById(final String id){
		for(final EnumPrayers prayer:EnumPrayers.values())
			if(prayer.id.equals(id))
				return prayer;
		return null;
	}

	private static final Class<?>[] types = (Class<?>[]) Arrays.asList(String.class, String.class, List.class, Float.TYPE, Boolean.TYPE, Boolean.TYPE, List.class, ResourceLocation.class).toArray();

	/**
	 * Helper method to allow other mods to easily add prayers. This should be done in your register method.
	 * @return The new prayer.
	 */
	public static EnumPrayers addPrayer(final String enumName, final String id, final String displayName, final List<String> description, final float drain, final boolean overhead, final boolean requiresTome, final List<EnumPrayers> requiredAfter, final ResourceLocation texture){
		return EnumPrayers.addPrayersVar(enumName, id, displayName, description, drain, overhead, requiresTome, requiredAfter, texture);
	}

	private static EnumPrayers addPrayersVar(final String enumName, final Object ... objects){
		try {
			return EnumHelper.addEnum(EnumPrayers.class, enumName, EnumPrayers.types, objects);
		} catch (final Exception e) {
			LogHelper.error(StringHelper.build("Failed to register a new Prayer: ", enumName, ". The registration was requested in class: "+PSReflectionHelper.retrieveCallingStackTraceElement().getClassName()));
			e.printStackTrace();
		}
		return null;
	}

}
