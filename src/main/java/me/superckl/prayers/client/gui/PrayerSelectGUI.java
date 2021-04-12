package me.superckl.prayers.client.gui;

import java.util.Collection;
import java.util.Iterator;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.ints.IntComparators;
import me.superckl.prayers.ClientConfig;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.client.input.KeyBindings;
import me.superckl.prayers.prayer.Prayer;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PrayerSelectGUI extends Screen{

	public static ResourceLocation PRAYER_GUI_TEXTURE = new ResourceLocation(Prayers.MOD_ID, "textures/gui/select_gui.png");

	protected int xSize = 147;
	protected int ySize = 116;
	protected int guiLeft;
	protected int guiTop;

	protected PrayerBar prayerBar = new PrayerBar(true, true);

	private boolean moving = false;
	private double barX = -1;
	private double barY = -1;
	private double relX;
	private double relY;

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
		PrayerBar.MAIN_BAR.setMovable(true);
	}

	@Override
	public void onClose() {
		PrayerBar.MAIN_BAR.setMovable(false);
		super.onClose();
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
		if(this.barX != -1 && this.barY != -1)
			PrayerBar.renderMainPrayerBarAt(matrixStack, (int) this.barX, (int) this.barY);
		else
			PrayerBar.renderMainPrayerBar(matrixStack, ClientHelper.getWindow());
		if(this.isInBar(mouseX, mouseY) && !this.moving)
			this.renderTooltip(matrixStack, new TranslationTextComponent(LangUtil.buildTextLoc("prayer_bar.move")), mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
		if(KeyBindings.OPEN_PRAYER_GUI.getKey().getValue() == keyCode) {
			this.onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
		if(this.isInBar(mouseX, mouseY) && mouseButton == GLFW.GLFW_MOUSE_BUTTON_1) {
			this.barX = (int) (this.width*ClientConfig.getInstance().getWidgetX().get());
			this.barY = (int) (this.height*ClientConfig.getInstance().getWidgetY().get());
			this.relX = this.barX - mouseX;
			this.relY = this.barY - mouseY;
			this.moving = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseDragged(final double newX, final double newY, final int button, final double deltaX, final double deltaY) {
		if(this.moving && button == GLFW.GLFW_MOUSE_BUTTON_1) {
			this.barX = newX+this.relX;
			this.barY = newY+this.relY;
			return true;
		}
		return super.mouseDragged(newX, newY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
		if(this.moving && button == GLFW.GLFW_MOUSE_BUTTON_1) {
			ClientConfig.getInstance().getWidgetX().set(this.barX/this.width);
			ClientConfig.getInstance().getWidgetY().set(this.barY/this.height);
			ClientConfig.getInstance().getWidgetX().save();
			ClientConfig.getInstance().getWidgetY().save();
			this.barX = this.barY = -1;
			this.moving = false;
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private boolean needsScrollBars() {
		return this.buttons.size() > 6*4;
	}

	public boolean isInBar(final double mouseX, final double mouseY) {
		final int barX = (int) (this.width*ClientConfig.getInstance().getWidgetX().get());
		final int barY = (int) (this.height*ClientConfig.getInstance().getWidgetY().get());
		final double relX = barX - mouseX;
		final double relY = barY - mouseY;
		return -relX >= 0 && -relX < PrayerBar.MAIN_BAR.width() && -relY >=0 && -relY < PrayerBar.HEIGHT;
	}

}
