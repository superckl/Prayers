package me.superckl.prayercraft.common.prayer;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor
public enum Prayers {

	ENHANCE_POTION("ePotion", EnumChatFormatting.YELLOW+"Enhance Potions", Arrays.asList("Boosts potion duration by 20%"), 5, 1F, false, new ResourceLocation("textures/items/fire.png")),
	PROTECT_MELEE("pMelee", EnumChatFormatting.AQUA+"Protect from Melee", Arrays.asList("Reduces melee damage by 50%"), 45, 2.5F, true, new ResourceLocation("textures/items/diamond_sword.png")),
	PROTECT_RANGE("pRange", EnumChatFormatting.AQUA+"Protect from Range", Arrays.asList("Reduces ranged damage by 50%"), 45, 2.5F, true, new ResourceLocation("textures/items/arrow.png")),
	PROTECT_MAGIC("pMagic", EnumChatFormatting.AQUA+"Protect from Magic", Arrays.asList("Reduces magic damage by 50%"), 45, 2.5F, true, new ResourceLocation("textures/items/fire.png")); //TODO

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
