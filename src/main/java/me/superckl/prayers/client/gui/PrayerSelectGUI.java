package me.superckl.prayers.client.gui;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.ints.IntComparators;
import me.superckl.prayers.Prayer;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.client.input.KeyBindings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PrayerSelectGUI extends Screen{

	public static ResourceLocation PRAYER_GUI_TEXTURE = new ResourceLocation(Prayers.MOD_ID, "textures/gui/select_gui.png");

	protected int xSize = 147;
	protected int ySize = 116;
	protected int guiLeft;
	protected int guiTop;

	protected PrayerBar prayerBar = new PrayerBar(true, true);

	public PrayerSelectGUI() {
		super(new StringTextComponent("Prayers"));
	}

	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		final int spacing = 4;
		final int numCols = 6;
		final int startX = this.guiLeft+9;

		int y = this.guiTop+29;
		int x = startX;
		int j = 0;

		final Collection<Prayer> prayers = GameRegistry.findRegistry(Prayer.class).getValues();
		final Iterator<Prayer> it = prayers.stream().sorted((p1, p2) -> IntComparators.NATURAL_COMPARATOR.compare(p1.getLevel(), p2.getLevel())).iterator();
		while(it.hasNext()) {
			final Prayer prayer  = it.next();
			if(!prayer.isEnabled())
				continue;
			final Button prayerButton = new PrayerButton(prayer, x, y, 16, 16);
			this.addButton(prayerButton);
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
		this.minecraft.getTextureManager().bind(PrayerSelectGUI.PRAYER_GUI_TEXTURE);
		RenderSystem.enableDepthTest();
		this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		final int scrollBarX = this.guiLeft+134;
		final int scrollBarY = this.guiTop+25;
		final int textureU = this.needsScrollBars() ? 154:147;
		this.blit(matrixStack, scrollBarX, scrollBarY, textureU, 0, 7, 9);
		this.prayerBar.renderAt(matrixStack, this.guiLeft+5, this.guiTop+4);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
		if(KeyBindings.OPEN_PRAYER_GUI.getKey().getValue() == keyCode) {
			this.onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private boolean needsScrollBars() {
		return this.buttons.size() > 6*4;
	}

}
