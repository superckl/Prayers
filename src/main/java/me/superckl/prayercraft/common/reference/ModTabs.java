package me.superckl.prayercraft.common.reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModTabs {

	public static final CreativeTabs TAB_PRAYER_ITEMS = new CreativeTabs(ModData.MOD_ID) {

		@Override
		public Item getTabIconItem() {
			return ModItems.basicBone;
		}

		@Override
		public String getTranslatedTabLabel() {
			return "PrayerCraft Items";
		}


	};

}
