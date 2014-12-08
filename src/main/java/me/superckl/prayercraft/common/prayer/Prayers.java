package me.superckl.prayercraft.common.prayer;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor
public enum Prayers {

	ENCHANCE_DEFENCE_1("eDefence1", EnumChatFormatting.YELLOW+"Tough", Arrays.asList("Increases defence by 5%"), 1, 0.1F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MELEE_1("eMelee1", EnumChatFormatting.YELLOW+"Might", Arrays.asList("Increases melee strength by 5%"), 3, 0.15F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_RANGE_1("eRange1", EnumChatFormatting.YELLOW+"Watchful", Arrays.asList("Increases ranged strength by 5%"), 5, 0.15F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_POTION("ePotion", EnumChatFormatting.YELLOW+"Enhance Potions", Arrays.asList("Boosts potion duration by 20%"), 5, 1F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MAGIC_1("eMagic1", EnumChatFormatting.YELLOW+"Mystic Charge", Arrays.asList("Increases magic strength by 5%"), 6, 0.15F, false, new ResourceLocation("textures/items/fire.png")),
	ENCHANCE_DEFENCE_2("eDefence2", EnumChatFormatting.YELLOW+"Durable", Arrays.asList("Increases defence by 10%"), 7, 0.2F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MELEE_2("eMelee2", EnumChatFormatting.YELLOW+"Strength", Arrays.asList("Increases melee strength by 10%"), 10, 0.25F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_RANGE_2("eRange2", EnumChatFormatting.YELLOW+"Sharp Eye", Arrays.asList("Increases ranged strength by 10%"), 12, 0.25F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MAGIC_2("eMagic2", EnumChatFormatting.YELLOW+"Mystic Will", Arrays.asList("Increases magic strength by 10%"), 14, 0.25F, false, new ResourceLocation("textures/items/fire.png")),
	ENCHANCE_DEFENCE_3("eDefence3", EnumChatFormatting.YELLOW+"Resilient", Arrays.asList("Increases defence by 15%"), 15, 0.3F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MELEE_3("eMelee3", EnumChatFormatting.YELLOW+"Power", Arrays.asList("Increases melee strength by 15%"), 17, 0.35F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_RANGE_3("eRange3", EnumChatFormatting.YELLOW+"Keen Eye", Arrays.asList("Increases ranged strength by 15%"), 20, 0.35F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MAGIC_3("eMagic3", EnumChatFormatting.YELLOW+"Mystic Might", Arrays.asList("Increases magic strength by 15%"), 22, 0.35F, false, new ResourceLocation("textures/items/fire.png")),
	ENCHANCE_DEFENCE_4("eDefence4", EnumChatFormatting.YELLOW+"Robust", Arrays.asList("Increases defence by 25%"), 25, 0.5F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MELEE_4("eMelee4", EnumChatFormatting.YELLOW+"Vigor", Arrays.asList("Increases melee strength by 25%"), 27, 0.6F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_RANGE_4("eRange4", EnumChatFormatting.YELLOW+"Hawk Eye", Arrays.asList("Increases ranged strength by 25%"), 30, 0.6F, false, new ResourceLocation("textures/items/fire.png")),
	ENHANCE_MAGIC_4("eMagic4", EnumChatFormatting.YELLOW+"Mystic Attunement", Arrays.asList("Increases magic strength by 25%"), 32, 0.6F, false, new ResourceLocation("textures/items/fire.png")),
	PROTECT_MELEE("pMelee", EnumChatFormatting.AQUA+"Protect from Melee", Arrays.asList("Reduces melee damage by 50%"), 45, 2.5F, true, new ResourceLocation("textures/items/diamond_sword.png")),
	PROTECT_RANGE("pRange", EnumChatFormatting.AQUA+"Protect from Range", Arrays.asList("Reduces ranged damage by 50%"), 45, 2.5F, true, new ResourceLocation("textures/items/arrow.png")),
	PROTECT_MAGIC("pMagic", EnumChatFormatting.AQUA+"Protect from Magic", Arrays.asList("Reduces magic damage by 50%"), 45, 2.5F, true, new ResourceLocation("textures/items/fire.png")),
	POTENCY_1("eAll1", EnumChatFormatting.AQUA+"Effective", Arrays.asList("Increases damage output by 30%"), 50, 3F, false, new ResourceLocation("textures/items/fire.png")),
	POTENCY_2("eAll2", EnumChatFormatting.AQUA+"Potent", Arrays.asList("Increases damage output by 50%"), 70, 5F, false, new ResourceLocation("textures/items/fire.png"));//TODO

	@Getter
	private final String id;
	@Getter
	private final String displayName;
	@Getter
	private final List<String> description;
	/**
	 * Calculated every second (20 ticks)
	 */
	@Getter
	private final int level;
	@Getter
	private final float drain;
	@Getter
	private final boolean overhead;
	@Getter
	private final ResourceLocation texture;

	public static Prayers getById(final String id){
		for(final Prayers prayer:Prayers.values())
			if(prayer.id.equals(id))
				return prayer;
		return null;
	}

}
