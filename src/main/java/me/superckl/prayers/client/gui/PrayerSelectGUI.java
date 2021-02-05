package me.superckl.prayers.client.gui;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.Prayer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PrayerSelectGUI extends Screen{

	protected int xSize = 176;
	protected int ySize = 166;
	protected int guiLeft;
	protected int guiTop;

	public PrayerSelectGUI() {
		super(new StringTextComponent("Prayers GUI"));
	}

	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		final int numCols = 5;
		final int spacing = (this.xSize-16*(numCols+2))/(numCols+1);
		final int excess = this.xSize-16*numCols-spacing*(numCols-1);
		final int startX = excess/2;

		int y = 10;
		int x = startX;
		int j = 0;

		final Collection<Prayer> prayers = GameRegistry.findRegistry(Prayer.class).getValues();
		final Iterator<Prayer> it = prayers.iterator();
		while(it.hasNext()) {
			final Prayer prayer  = it.next();
			if(!prayer.isEnabled())
				continue;
			final Button prayerButton = new PrayerButton(prayer, x+this.guiLeft, y+this.guiTop, 16, 16);
			this.buttons.add(prayerButton);
			this.addListener(prayerButton);
			x += 16+spacing;
			j++;
			if (j == numCols) {
				j = 0;
				y += 20;
				x = startX;
			}
		}
	}

	@Override
	public void render(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

}
