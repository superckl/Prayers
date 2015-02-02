package me.superckl.prayers.common.reference;

import net.minecraft.util.ResourceLocation;

public final class RenderData {

	public static final ResourceLocation BASIC_ALTAR_MODEL = new ResourceLocation(ModData.MOD_ID+":textures/models/basicaltar.obj");
	public static final ResourceLocation PRAYERS_GUI = new ResourceLocation(ModData.MOD_ID+":textures/gui/prayerinventory.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation(ModData.MOD_ID+":textures/gui/widgets.png");

	public static final String NEI_GUI = ModData.MOD_ID+":textures/gui/neiofferingtable.png";

	public static final class BlockIDs{
		public static int OFFERING_TABLE;
	}

	public static final class GUIIDs{
		public static final int PRAYERS = 0;
		public static final int ANCIENT_TOME = 1;
	}

}
