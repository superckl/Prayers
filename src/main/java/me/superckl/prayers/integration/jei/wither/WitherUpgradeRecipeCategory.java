package me.superckl.prayers.integration.jei.wither;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.client.RenderHelper;
import me.superckl.prayers.integration.jei.altar.AltarCraftingRecipeCategory;
import me.superckl.prayers.util.LangUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class WitherUpgradeRecipeCategory implements IRecipeCategory<WitherUpgradeRecipe>{

	public static final ResourceLocation UID = new ResourceLocation(Prayers.MOD_ID, "wither_upgrade");

	private final IGuiHelper guiHelper;
	private final WitherEntity wither;
	private final FakeClientPlayer player;
	private final IDrawable arrow;
	private final ITickTimer timer;
	private int lastValue = -1;

	public WitherUpgradeRecipeCategory(final IGuiHelper helper) {
		this.guiHelper = helper;
		this.wither = new FakeWitherEntity(ClientHelper.getLevel());
		this.player = new FakeClientPlayer((ClientWorld) ClientHelper.getLevel(), ClientHelper.getPlayer().getGameProfile());
		this.arrow = helper.drawableBuilder(AltarCraftingRecipeCategory.ARROWS, 14, 0, 22, 15).setTextureSize(36, 16).build();
		this.timer = helper.createTickTimer(50, 50, false);

	}

	@Override
	public ResourceLocation getUid() {
		return WitherUpgradeRecipeCategory.UID;
	}

	@Override
	public Class<? extends WitherUpgradeRecipe> getRecipeClass() {
		return WitherUpgradeRecipe.class;
	}

	@Override
	public String getTitle() {
		return new TranslationTextComponent(LangUtil.buildTextLoc("jei.wither_upgrade")).getString();
	}

	@Override
	public IDrawable getBackground() {
		return this.guiHelper.createBlankDrawable(150, 50);
	}

	@Override
	public IDrawable getIcon() {
		return new WitherDrawable(4, false);
	}

	@Override
	public void setIngredients(final WitherUpgradeRecipe recipe, final IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}

	@Override
	public void setRecipe(final IRecipeLayout recipeLayout, final WitherUpgradeRecipe recipe, final IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 25-8, 33);
		recipeLayout.getItemStacks().init(1, false, 122-8, 33);
		recipeLayout.getItemStacks().set(ingredients);
	}

	@Override
	public void draw(final WitherUpgradeRecipe recipe, final MatrixStack matrixStack, final double mouseX, final double mouseY) {
		this.arrow.draw(matrixStack, 75-11, 25-7);
		final int tick = this.timer.getValue();
		if(tick != this.lastValue) {
			if(tick == 0)
				this.player.swing(Hand.MAIN_HAND);
			this.player.updateSwingTime();
			this.lastValue = tick;
		}
		this.wither.hurtTime = 0;
		this.wither.setInvulnerableTicks(1);
		RenderHelper.renderEntityInInventory(40, 40, 10, 1, matrixStack, 60, this.wither);
		this.player.setItemInHand(Hand.MAIN_HAND, recipe.getInput());
		RenderHelper.renderEntityInInventory(10, 32, 12, 1, matrixStack, -60, this.player);

		this.wither.hurtTime = Math.max(10-tick, 0);
		this.wither.setInvulnerableTicks(0);
		RenderHelper.renderEntityInInventory(110, 40, 10, 1, matrixStack, -60, this.wither);
		this.player.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
		RenderHelper.renderEntityInInventory(140, 32, 12, 1, matrixStack, 60, this.player);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(final WitherUpgradeRecipe recipe, final double mouseX, final double mouseY) {
		final List<ITextComponent> tooltip = Lists.newArrayList();
		final int[] arrowX = new int[] {75-11, 75-11+this.arrow.getWidth()};
		final int[] arrowY = new int[] {25-7, 25-7+this.arrow.getHeight()};
		if(mouseX >= arrowX[0] && mouseX < arrowX[1] && mouseY >= arrowY[0] && mouseY < arrowY[1])
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("jei.wither_upgrade.arrow_hover")));
		if(mouseX >= 5 && mouseX < 50 && mouseY >= 5 && mouseY < 34) {
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("jei.wither_upgrade.use_item")));
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("jei.wither_upgrade.consume_item")).withStyle(TextFormatting.RED));
		}

		if(mouseX >= 100 && mouseX < 145 && mouseY >= 5 && mouseY < 34)
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("jei.wither_upgrade.wither_drop")));
		return tooltip;
	}
}
