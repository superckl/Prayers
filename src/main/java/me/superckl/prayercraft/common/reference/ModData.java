package me.superckl.prayercraft.common.reference;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class ModData {

	public static final String MOD_ID = "PrayerCraft";
	public static final String MOD_NAME= "PrayerCraft";
	public static final String VERSION = "0.1-Beta";
	public static final String GUI_FACTORY = "me.superckl.prayercraft.common.gui.GuiFactory";
	public static final String CLIENT_PROXY = "me.superckl.prayercraft.proxy.ClientProxy";
	public static final String SERVER_PROXY = "me.superckl.prayercraft.proxy.ServerProxy";

	public static SimpleNetworkWrapper PRAYER_UPDATE_CHANNEL;

}
