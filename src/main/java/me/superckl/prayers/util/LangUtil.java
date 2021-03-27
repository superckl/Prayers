package me.superckl.prayers.util;

import me.superckl.prayers.Prayers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class LangUtil {

	public static String buildTextLoc(final String path) {
		return Util.makeDescriptionId("text", new ResourceLocation(Prayers.MOD_ID, path));
	}

}
