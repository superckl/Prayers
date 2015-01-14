package me.superckl.prayers.common.reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModTabs {

	public static final CreativeTabs TAB_PRAYER_ITEMS = new CreativeTabs(ModData.MOD_ID+":items") {

		@Override
		public Item getTabIconItem() {
			return ModItems.basicBone;
		}

		@Override
		public String getTranslatedTabLabel() {
			return "Prayers Items";
		}


	};
	public static final CreativeTabs TAB_PRAYER_BLOCKS = new CreativeTabs(ModData.MOD_ID+":blocks") {

		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(ModBlocks.offeringTable);
		}

		@Override
		public String getTranslatedTabLabel() {
			return "Prayers Blocks";
		}
	};

	public static final CreativeTabs TAB_PRAYER_TOMES = new CreativeTabs(ModData.MOD_ID+":tomes") {

		@Override
		public Item getTabIconItem() {
			return ModItems.tome;
		}

		@Override
		public String getTranslatedTabLabel() {
			return "Prayer Tomes";
		}
	};

}
