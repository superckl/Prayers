package me.superckl.prayercraft.client.gui;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayercraft.Config.Category;
import me.superckl.prayercraft.PrayerCraft;
import me.superckl.prayercraft.common.reference.ModData;
import me.superckl.prayercraft.common.utility.StringHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class GuiConfigPrayers extends GuiConfig{

	public GuiConfigPrayers(final GuiScreen parentScreen) {
		super(parentScreen, GuiConfigPrayers.getConfigElements(), ModData.MOD_ID, false, false,
				LanguageRegistry.instance().getStringLocalization(StringHelper.formatGUIUnlocalizedName("config")));
	}

	private static List<IConfigElement> getConfigElements(){
		final List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyConfigElement.DummyCategoryElement("General", "betteroceans.configgui.ctgy.general", GeneralCategory.class));
		return list;
	}

	public static class GeneralCategory extends CategoryEntry{

		public GeneralCategory(final GuiConfig owningScreen,
				final GuiConfigEntries owningEntryList, final IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(this.owningScreen, new ConfigElement(PrayerCraft.getInstance().getConfig().getConfigFile().getCategory(Category.GENERAL)).getChildElements()
					, ModData.MOD_ID, false, false, LanguageRegistry.instance().getStringLocalization(StringHelper.formatGUIUnlocalizedName("config_general")));
		}

	}

}
