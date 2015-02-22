package me.superckl.prayers.client.gui;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayers.Config.Category;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.utility.StringHelper;
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
		list.add(new DummyConfigElement.DummyCategoryElement("General", "prayers.configgui.ctgy.general", GeneralCategory.class));
		list.add(new DummyConfigElement.DummyCategoryElement("Altar", "prayers.configgui.ctgy.altar", AltarCategory.class));
		list.add(new DummyConfigElement.DummyCategoryElement("Blood Magic", "prayers.configgui.ctgy.bloodmagic", BloodMagicCategory.class));
		list.add(new DummyConfigElement.DummyCategoryElement("Potions", "prayers.configgui.ctgy.potions", PotionsCategory.class));
		return list;
	}

	public static class GeneralCategory extends CategoryEntry{

		public GeneralCategory(final GuiConfig owningScreen,
				final GuiConfigEntries owningEntryList, final IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(this.owningScreen, new ConfigElement(Prayers.getInstance().getConfig().getConfigFile().getCategory(Category.GENERAL)).getChildElements()
					, ModData.MOD_ID, false, false, LanguageRegistry.instance().getStringLocalization(StringHelper.formatGUIUnlocalizedName("config_general")));
		}

	}

	public static class AltarCategory extends CategoryEntry{

		public AltarCategory(final GuiConfig owningScreen,
				final GuiConfigEntries owningEntryList, final IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(this.owningScreen, new ConfigElement(Prayers.getInstance().getConfig().getConfigFile().getCategory(Category.ALTAR)).getChildElements()
					, ModData.MOD_ID, false, true, LanguageRegistry.instance().getStringLocalization(StringHelper.formatGUIUnlocalizedName("config_altar")));
		}

	}

	public static class BloodMagicCategory extends CategoryEntry{

		public BloodMagicCategory(final GuiConfig owningScreen,
				final GuiConfigEntries owningEntryList, final IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(this.owningScreen, new ConfigElement(Prayers.getInstance().getConfig().getConfigFile().getCategory(Category.ALTAR)).getChildElements()
					, ModData.MOD_ID, false, true, LanguageRegistry.instance().getStringLocalization(StringHelper.formatGUIUnlocalizedName("config_bloodmagic")));
		}

	}

	public static class PotionsCategory extends CategoryEntry{

		public PotionsCategory(final GuiConfig owningScreen,
				final GuiConfigEntries owningEntryList, final IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(this.owningScreen, new ConfigElement(Prayers.getInstance().getConfig().getConfigFile().getCategory(Category.ALTAR)).getChildElements()
					, ModData.MOD_ID, false, true, LanguageRegistry.instance().getStringLocalization(StringHelper.formatGUIUnlocalizedName("config_potions")));
		}

	}

}
