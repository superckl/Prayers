package me.superckl.prayers.client.gui;

import java.util.List;
import java.util.stream.Collectors;

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

public class PrayerSelectGUI extends Screen{

	public static ResourceLocation PRAYER_GUI_TEXTURE = new ResourceLocation(Prayers.MOD_ID, "textures/gui/select_gui.png");

	private static Group SELECTED_GROUP = Group.ALL;
	private static int NUM_ROWS = 4;
	private static int NUM_COLS = 6;

	protected int xSize = 147;
	protected int ySize = 143;
	protected int guiLeft;
	protected int guiTop;

	protected PrayerBar prayerBar = new PrayerBar(true, true);

	private boolean movingPrayerBar = false;
	private double prayerBarX = -1;
	private double prayerBarY = -1;
	private double prayerBarRelX;
	private double prayerBarRelY;

	private float scrollBar;
	private boolean scrolling;
	private double scrollBarRelY;

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
		this.children.clear();

		final int spacing = 4;
		final int startX = this.guiLeft+9;

		int y = this.guiTop+56;
		int x = startX;
		int j = 0;

		List<Prayer> prayers = Prayer.allForGroup(PrayerSelectGUI.SELECTED_GROUP)
				.sorted((p1, p2) -> IntComparators.NATURAL_COMPARATOR.compare(p1.getLevel(), p2.getLevel()))
				.collect(Collectors.toList());

		final int toSkip = Math.round((this.getTotalRows()-PrayerSelectGUI.NUM_ROWS)*this.scrollBar);
		final int startIndex = PrayerSelectGUI.NUM_COLS*toSkip;
		final int endIndex = Math.min(prayers.size(), startIndex+PrayerSelectGUI.NUM_COLS*PrayerSelectGUI.NUM_ROWS);
		prayers = prayers.subList(startIndex, endIndex);

		for(final Prayer prayer:prayers) {
			final Button prayerButton = new PrayerButton(prayer, x, y, 16, 16);
			this.addButton(prayerButton);
			x += 16+spacing;
			j++;
			if (j == PrayerSelectGUI.NUM_COLS) {
				j = 0;
				y += 20;
				x = startX;
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
		final int scrollBarY = this.guiTop+52+Math.round((84-9)*this.scrollBar);
		final int textureU = this.needsScrollBars() ? 154:147;
		this.blit(matrixStack, scrollBarX, scrollBarY, textureU, 0, 7, 9);
		this.prayerBar.renderAt(matrixStack, this.guiLeft+5, this.guiTop+4);
		this.renderTabs(matrixStack, mouseX, mouseY);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if(this.prayerBarX != -1 && this.prayerBarY != -1)
			PrayerBar.renderMainPrayerBarAt(matrixStack, (int) this.prayerBarX, (int) this.prayerBarY);
		else
			PrayerBar.renderMainPrayerBar(matrixStack, ClientHelper.getWindow());
		if(this.isInPrayerBar(mouseX, mouseY) && !this.movingPrayerBar)
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
			if(!this.movingPrayerBar && mouseX >= x && mouseX < x+width && mouseY >= y && mouseY < y+25)
				this.renderTooltip(matrixStack, group.getName(), mouseX, mouseY);
			x += width;
		}
	}

	@Override
	public boolean mouseScrolled(final double mouseX, final double mouseY, final double scroll) {
		if(!this.needsScrollBars())
			return false;
		final int numRows = this.getTotalRows();
		this.scrollBar = (float) ((this.scrollBar-scroll)/(numRows-4));
		this.scrollBar = MathHelper.clamp(this.scrollBar, 0, 1);
		this.setupButtons();
		return true;
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
		if(mouseButton != GLFW.GLFW_MOUSE_BUTTON_1)
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		if(this.isInPrayerBar(mouseX, mouseY)) {
			this.prayerBarX = (int) (this.width*ClientConfig.getInstance().getWidgetX().get());
			this.prayerBarY = (int) (this.height*ClientConfig.getInstance().getWidgetY().get());
			this.prayerBarRelX = this.prayerBarX - mouseX;
			this.prayerBarRelY = this.prayerBarY - mouseY;
			this.movingPrayerBar = true;
			return true;
		}
		if(this.isInScrollBar(mouseX, mouseY)) {
			final int scrollBarY = this.guiTop+52+Math.round((84-9)*this.scrollBar);
			this.scrollBarRelY = scrollBarY - mouseY;
			this.scrolling = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseDragged(final double newX, final double newY, final int button, final double deltaX, final double deltaY) {
		if(button != GLFW.GLFW_MOUSE_BUTTON_1)
			return super.mouseDragged(newX, newY, button, deltaX, deltaY);
		if(this.movingPrayerBar) {
			this.prayerBarX = newX+this.prayerBarRelX;
			this.prayerBarY = newY+this.prayerBarRelY;
			return true;
		}
		if(this.scrolling) {
			final int barMinY = this.guiTop+52;
			final int barMaxY = barMinY+84-9;
			final double newScrollY = MathHelper.clamp(newY+this.scrollBarRelY, barMinY, barMaxY);
			final float prevScroll = this.scrollBar;
			this.scrollBar = (float) ((newScrollY-barMinY)/(barMaxY-barMinY));

			final int totalRows =this.getTotalRows();
			final int toSkipBefore = Math.round((totalRows-PrayerSelectGUI.NUM_ROWS)*prevScroll);
			final int toSkipNow = Math.round((totalRows-PrayerSelectGUI.NUM_ROWS)*this.scrollBar);
			if(toSkipBefore != toSkipNow)
				this.setupButtons();
			return true;
		}
		return super.mouseDragged(newX, newY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
		if(button != GLFW.GLFW_MOUSE_BUTTON_1)
			return super.mouseReleased(mouseX, mouseY, button);
		if(this.movingPrayerBar) {
			ClientConfig.getInstance().getWidgetX().set(this.prayerBarX/this.width);
			ClientConfig.getInstance().getWidgetY().set(this.prayerBarY/this.height);
			ClientConfig.getInstance().getWidgetX().save();
			ClientConfig.getInstance().getWidgetY().save();
			this.prayerBarX = this.prayerBarY = -1;
			this.movingPrayerBar = false;
			return true;
		}
		if(this.scrolling) {
			this.scrolling = false;
			return true;
		}
		final int i = this.getTabIndex(mouseX, mouseY);
		if(i != -1) {
			PrayerSelectGUI.SELECTED_GROUP = Group.values()[i];
			this.scrollBar = 0;
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
		return this.getTotalRows() > PrayerSelectGUI.NUM_ROWS;
	}

	private int getTotalRows() {
		return (int) (Prayer.allForGroup(PrayerSelectGUI.SELECTED_GROUP).count()/PrayerSelectGUI.NUM_COLS)+1;
	}

	public boolean isInPrayerBar(final double mouseX, final double mouseY) {
		final int barX = (int) (this.width*ClientConfig.getInstance().getWidgetX().get());
		final int barY = (int) (this.height*ClientConfig.getInstance().getWidgetY().get());
		final double relX = barX - mouseX;
		final double relY = barY - mouseY;
		return -relX >= 0 && -relX < PrayerBar.MAIN_BAR.width() && -relY >=0 && -relY < PrayerBar.HEIGHT;
	}

	public boolean isInScrollBar(final double mouseX, final double mouseY) {
		final int scrollBarX = this.guiLeft+134;
		final int scrollBarY = this.guiTop+52+Math.round((84-9)*this.scrollBar);
		return mouseX >= scrollBarX && mouseX < scrollBarX + 7 && mouseY >= scrollBarY && mouseY < scrollBarY+9;
	}

}
