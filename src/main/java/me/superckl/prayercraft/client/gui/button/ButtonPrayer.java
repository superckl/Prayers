package me.superckl.prayercraft.client.gui.button;

import lombok.Getter;
import me.superckl.prayercraft.common.prayer.Prayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ButtonPrayer extends GuiButton{

	@Getter
	private final Prayers prayer;

	public ButtonPrayer(final int id, final int x, final int y, final Prayers prayer) {
		super(id, x, y, 16, 16, null);
		this.prayer = prayer;
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
		// TODO Auto-generated method stub
	}



}
