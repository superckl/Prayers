package me.superckl.prayers.integration.jei;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.block.entity.CraftingStandTileEntity;
import me.superckl.prayers.client.CraftingStandRenderer;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;

@RequiredArgsConstructor
public class CraftingStandItemRenderer implements IIngredientRenderer<ItemStack>{

	private final IGuiHelper guiHelper;
	private final ITickTimer timer;
	private final Direction dir;
	private final int xPos, yPos;
	private final CraftingStandRenderer tileRenderer = (CraftingStandRenderer) TileEntityRendererDispatcher.instance.getRenderer(new CraftingStandTileEntity());
	
	@Override
	public void render(MatrixStack matrixStack, int xPosition, int yPosition, ItemStack ingredient) {
		matrixStack.pushPose();
		guiHelper.createDrawableIngredient(ingredient).draw(matrixStack, xPosition, yPosition);
		matrixStack.translate(xPosition-xPos, yPosition-yPos, 0);
		AltarCraftingBackground.setupBlockRendering(matrixStack, 0, 0, AltarCraftingBackground.altarState);
		matrixStack.translate(0, 1, 0);
		RenderHelper.setupForFlatItems();
		tileRenderer.renderStandItem(ingredient, matrixStack, dir, ((float) timer.getValue())/timer.getMaxValue(), ClientHelper.getPartialTicks(),
				ClientHelper.getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
		ClientHelper.getBufferSource().endBatch();
		RenderHelper.setupFor3DItems();
		matrixStack.popPose();
	}

	@Override
	public List<ITextComponent> getTooltip(ItemStack ingredient, ITooltipFlag tooltipFlag) {
		return ingredient.getTooltipLines(ClientHelper.getPlayer(), tooltipFlag);
	}

}
