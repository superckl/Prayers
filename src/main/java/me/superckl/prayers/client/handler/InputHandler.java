package me.superckl.prayers.client.handler;

import me.superckl.prayers.common.reference.KeyBindings;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.network.MessageOpenPrayerGui;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class InputHandler {

	@SubscribeEvent
	public void onKeyInput(final KeyInputEvent e){
		if(KeyBindings.OPEN_PRAYERS.isPressed())
			ModData.PRAYER_UPDATE_CHANNEL.sendToServer(new MessageOpenPrayerGui());
	}

}
