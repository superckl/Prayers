package me.superckl.prayers.client.gui;

import me.superckl.prayers.client.gui.button.ButtonPrayer;
import me.superckl.prayers.common.prayer.EnumPrayers;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface allowing an entry point to the Prayer GUI. These can be registered in {@link GuiContainerPrayers#registerPrayerClickHandler(EnumPrayers, int, PrayerGuiClickHandler) registerPrayerClickHandler}.
 * @author superckl
 *
 */
public interface PrayerGuiClickHandler {

	/**
	 * This method is called whenever a player clicks a prayer button in the prayers GUI. This can be used to override existing functionality or add new functionality for your custom prayers.
	 * @param player The player viewing the GUI.
	 * @param prayer The prayer whose button was clicked.
	 * @param gui The GUI instance.
	 * @param buttnon The button that was pressed.
	 * @return If the event should be considered consumed. If so, the method will exit, cauing lower priority handlers and other gui functionality to not be called.
	 */
	public boolean onClick(final EntityPlayer player, final EnumPrayers prayer, final GuiContainerPrayers gui, final ButtonPrayer buttnon);

}
