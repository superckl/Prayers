package me.superckl.prayers.client.handler;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.RenderData;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TextureStitchHandler {

	@SubscribeEvent
	public void onTextureStitch(final TextureStitchEvent.Pre e){
		if(e.map.getTextureType() != 1)
			return;
		RenderData.MAGIC_BURST = e.map.registerIcon(ModData.MOD_ID+":icons/magicburst");
	}

}
