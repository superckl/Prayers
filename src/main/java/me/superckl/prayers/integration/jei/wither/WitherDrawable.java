package me.superckl.prayers.integration.jei.wither;

import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.client.RenderHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.entity.boss.WitherEntity;

public class WitherDrawable implements IDrawable{

	public static final int WITHER_HEIGHT = 4;
	public static final int WITHER_WIDTH = 3;

	private final int scale;
	private final WitherEntity wither;

	public WitherDrawable(final int scale,  final boolean invul) {
		this.scale = scale;
		this.wither = new FakeWitherEntity(ClientHelper.getLevel());
		final int invulTicks = invul ? 1:0;
		this.wither.setInvulnerableTicks(invulTicks);
	}

	@Override
	public int getWidth() {
		return this.scale*WitherDrawable.WITHER_WIDTH;
	}

	@Override
	public int getHeight() {
		return this.scale*WitherDrawable.WITHER_HEIGHT;
	}

	@Override
	public void draw(final MatrixStack matrixStack, final int xOffset, final int yOffset) {
		final int renderX = xOffset+6;
		final int renderY = yOffset+16;
		RenderHelper.renderEntityInInventory(renderX, renderY, this.scale, matrixStack, 0, 0, this.wither);
	}

}
