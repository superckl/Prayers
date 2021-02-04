package me.superckl.prayers.network.packet;

import me.superckl.prayers.Prayers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PrayersPacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Prayers.MOD_ID, "main"),
			() -> PrayersPacketHandler.PROTOCOL_VERSION, PrayersPacketHandler.PROTOCOL_VERSION::equals, PrayersPacketHandler.PROTOCOL_VERSION::equals);

}
