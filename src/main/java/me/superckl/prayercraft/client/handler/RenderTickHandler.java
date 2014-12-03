package me.superckl.prayercraft.client.handler;

import me.superckl.prayercraft.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayercraft.common.utility.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.IExtendedEntityProperties;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderTickHandler {

	private final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent(receiveCanceled = false)
	public void onRenderTick(final RenderGameOverlayEvent.Text e){
		IExtendedEntityProperties prop;
		if((this.mc.thePlayer != null) && ((prop = this.mc.thePlayer.getExtendedProperties("prayer")) != null) && (prop instanceof PrayerExtendedProperties)){
			final PrayerExtendedProperties prayerProp = (PrayerExtendedProperties) prop;
			//final ScaledResolution r = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
			final String s = StringHelper.build(Math.round(prayerProp.getPrayerPoints()),"/",prayerProp.getPrayerLevel()*10);
			e.left.add(s);
		}
	}

}
