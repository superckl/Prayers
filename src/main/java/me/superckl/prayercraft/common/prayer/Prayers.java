package me.superckl.prayercraft.common.prayer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor
public enum Prayers {

	PROTECT_MELEE("pMelee", true, new ResourceLocation("textures/items/diamond_sword.png")),
	PROTECT_RANGE("pRange", true, new ResourceLocation("textures/items/arrow.png")),
	PROTECT_MAGIC("pMagic", true, null); //TODO

	@Getter
	private final String id;
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
