package me.superckl.prayers.integration.jei.altar;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.block.CraftingStandBlock;
import me.superckl.prayers.block.entity.CraftingStandTileEntity;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.client.render.CraftingStandRenderer;
import me.superckl.prayers.init.ModBlocks;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.EmptyModelData;

@RequiredArgsConstructor
public class CraftingStandItemRenderer implements IIngredientRenderer<ItemStack>{

	private final IGuiHelper guiHelper;
	private final ITickTimer timer;
	private final Direction dir;
	private final int xPos, yPos;
	private final BlockState craftingStand;
	private final CraftingStandRenderer tileRenderer = (CraftingStandRenderer) TileEntityRendererDispatcher.instance.getRenderer(new CraftingStandTileEntity());

	public CraftingStandItemRenderer(final IGuiHelper guiHelper, final ITickTimer timer, final Direction dir, final int xPos, final int yPos) {
		this.guiHelper = guiHelper;
		this.timer = timer;
		this.dir = dir;
		this.xPos = xPos;
		this.yPos = yPos;
		BlockState state  = ModBlocks.CRAFTING_STAND.get().defaultBlockState();
		if(dir != Direction.UP)
			state = state.setValue(CraftingStandBlock.CENTER, false).setValue(CraftingStandBlock.propertyFromDirection(dir), true);
		this.craftingStand = state;
	}

	@Override
	public void render(final MatrixStack matrixStack, final int xPosition, final int yPosition, final ItemStack ingredient) {
		matrixStack.pushPose();
		this.guiHelper.createDrawableIngredient(ingredient).draw(matrixStack, xPosition, yPosition);
		matrixStack.translate(xPosition-this.xPos, yPosition-this.yPos, 0);
		AltarCraftingBackground.setupBlockRendering(matrixStack, 0, 0, AltarCraftingBackground.altarState);
		matrixStack.translate(0, 1, 0);
		final IRenderTypeBuffer.Impl buffer = ClientHelper.getBufferSource();
		ClientHelper.getBlockRenderer().renderBlock(this.craftingStand, matrixStack, buffer,
				15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		buffer.endBatch();
		RenderHelper.setupForFlatItems();
		this.tileRenderer.renderStandItem(ingredient, matrixStack, this.dir, (float) this.timer.getValue()/this.timer.getMaxValue(), ClientHelper.getPartialTicks(),
				buffer, 15728880, OverlayTexture.NO_OVERLAY);
		buffer.endBatch();
		RenderHelper.setupFor3DItems();
		matrixStack.popPose();
	}

	@Override
	public List<ITextComponent> getTooltip(final ItemStack ingredient, final ITooltipFlag tooltipFlag) {
		return ingredient.getTooltipLines(ClientHelper.getPlayer(), tooltipFlag);
	}

}
