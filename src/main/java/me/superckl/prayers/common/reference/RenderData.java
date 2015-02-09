package me.superckl.prayers.common.reference;

import net.minecraft.util.ResourceLocation;

public final class RenderData {

	public static final ResourceLocation OFFERING_TABLE_MODEL = new ResourceLocation(ModData.MOD_ID+":textures/blocks/offeringtable.png");
	public static final ResourceLocation PRAYERS_GUI = new ResourceLocation(ModData.MOD_ID+":textures/gui/prayerinventory.png");
	public static final ResourceLocation WIDGETS = new ResourceLocation(ModData.MOD_ID+":textures/gui/widgets.png");
	public static final ResourceLocation UNDEAD_PRIEST_MODEL = new ResourceLocation(ModData.MOD_ID+":textures/entity/undeadpriest.png");
	public static final ResourceLocation PRIEST_VILLAGER_MODEL = new ResourceLocation(ModData.MOD_ID+":textures/entity/priestvillager.png");

	public static final String NEI_GUI = ModData.MOD_ID+":textures/gui/neiofferingtable.png";

	public static final float pixel = 0.0625F;

	public static final class BlockIDs{
		public static int OFFERING_TABLE;
	}

	public static final class GUIIDs{
		public static final int PRAYERS = 0;
		public static final int ANCIENT_TOME = 1;
	}

}
