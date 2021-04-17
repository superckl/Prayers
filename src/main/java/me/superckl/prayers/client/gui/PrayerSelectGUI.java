package me.superckl.prayers.client.gui;

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
import me.superckl.prayers.prayer.Prayer.Group;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PrayerSelectGUI extends Screen{

	public static ResourceLocation PRAYER_GUI_TEXTURE = new ResourceLocation(Prayers.MOD_ID, "textures/gui/select_gui.png");

	private static Group SELECTED_GROUP = Group.ALL;

	protected int xSize = 147;
	protected int ySize = 143;
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

		this.setupButtons();
		PrayerBar.MAIN_BAR.setMovable(true);
	}

	private void setupButtons() {
		this.buttons.clear();

		final int spacing = 4;
		final int numCols = 6;
		final int numRows = 4;
		final int startX = this.guiLeft+9;

		int y = this.guiTop+56;
		int x = startX;
		int j = 0;
		int i = 0;

		final Iterator<Prayer> prayers = GameRegistry.findRegistry(Prayer.class).getValues().stream()
				.filter(prayer -> prayer.isEnabled() && prayer.isIn(PrayerSelectGUI.SELECTED_GROUP))
				.sorted((p1, p2) -> IntComparators.NATURAL_COMPARATOR.compare(p1.getLevel(), p2.getLevel())).iterator();
		while(prayers.hasNext()) {
			final Prayer prayer  = prayers.next();
			final Button prayerButton = new PrayerButton(prayer, x, y, 16, 16);
			this.addButton(prayerButton);
			x += 16+spacing;
			j++;
			if (j == numCols) {
				j = 0;
				y += 20;
				x = startX;
				i++;
				if(i == numRows)
					break;
			}
		}
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
		final int scrollBarY = this.guiTop+52;
		final int textureU = this.needsScrollBars() ? 154:147;
		this.blit(matrixStack, scrollBarX, scrollBarY, textureU, 0, 7, 9);
		this.prayerBar.renderAt(matrixStack, this.guiLeft+5, this.guiTop+4);
		this.renderTabs(matrixStack, mouseX, mouseY);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if(this.barX != -1 && this.barY != -1)
			PrayerBar.renderMainPrayerBarAt(matrixStack, (int) this.barX, (int) this.barY);
		else
			PrayerBar.renderMainPrayerBar(matrixStack, ClientHelper.getWindow());
		if(this.isInBar(mouseX, mouseY) && !this.moving)
			this.renderTooltip(matrixStack, new TranslationTextComponent(LangUtil.buildTextLoc("prayer_bar.move")), mouseX, mouseY);
	}

	private void renderTabs(final MatrixStack matrixStack, final int mouseX, final int mouseY) {
		final Group[] groups = Group.values();
		int x = this.guiLeft+12;
		final int y = this.guiTop+25;
		final int v = 9;
		final int width = 28;
		for (final Group group:groups) {
			final boolean isActive = group == PrayerSelectGUI.SELECTED_GROUP;
			final int u = isActive ? 147:175;
			final int height = isActive ? 28:25;
			this.minecraft.getTextureManager().bind(PrayerSelectGUI.PRAYER_GUI_TEXTURE);
			this.blit(matrixStack, x, y, u, v, width, height);
			final ItemStack stack = group.getDisplayItem();
			if(!stack.isEmpty()) {
				ClientHelper.getItemRenderer().renderAndDecorateItem(stack, x+6, y+7);
				ClientHelper.getItemRenderer().renderGuiItemDecorations(ClientHelper.getFontRenderer(), stack, x+6, y+7);
			}else
				this.blit(matrixStack, x+6, y+7, 0, 168, 16, 16);
			if(!this.moving && mouseX >= x && mouseX < x+width && mouseY >= y && mouseY < y+25)
				this.renderTooltip(matrixStack, group.getName(), mouseX, mouseY);
			x += width;
		}
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
		final int i = this.getTabIndex(mouseX, mouseY);
		if(i != -1) {
			PrayerSelectGUI.SELECTED_GROUP = Group.values()[i];
			this.setupButtons();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private int getTabIndex(final double mouseX, final double mouseY) {
		final int x = this.guiLeft+12;
		final int y = this.guiTop+25;
		final int height = 25;
		final int width = 28;
		if(mouseY >= y && mouseY < y+height) {
			final int length = Group.values().length;
			if(mouseX >= x) {
				final int index = MathHelper.floor((mouseX-x)/width);
				return index < length ? index:-1;
			}
		}
		return -1;
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
