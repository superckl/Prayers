package me.superckl.prayers.client.gui.button;

import lombok.Getter;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ButtonPrayer extends GuiButton{

	@Getter
	private final EnumPrayers prayer;

	public ButtonPrayer(final int id, final int x, final int y, final EnumPrayers prayer) {
		super(id, x, y, 16, 16, null);
		this.prayer = prayer;
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
		RenderHelper.drawTexturedRect(this.prayer.getTexture(), this.xPosition, this.yPosition, 0, 0, 16, 16, 16, 16, 1D);
	}



}
