package me.superckl.prayers.client.handler;

import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.reference.ModPotions;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
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
			final boolean isBoosted = this.mc.thePlayer.isPotionActive(ModPotions.prayerBoost);
			final String s = StringHelper.build(Math.round(prayerProp.getPrayerPoints()),"/", Math.round(prayerProp.getMaxPrayerPoints()),
					isBoosted ? StringHelper.build(" (", EnumChatFormatting.AQUA, "+", Math.round(ModPotions.prayerBoost.getBoostFor(this.mc.thePlayer)), EnumChatFormatting.RESET, ")"):"");
			e.left.add(s);
		}
	}

}
