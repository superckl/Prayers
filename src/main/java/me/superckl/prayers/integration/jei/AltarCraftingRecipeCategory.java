package me.superckl.prayers.integration.jei;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.recipe.AbstractAltarCraftingRecipe;
import me.superckl.prayers.util.LangUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class AltarCraftingRecipeCategory implements IRecipeCategory<AbstractAltarCraftingRecipe>{

	public static final ResourceLocation UID = new ResourceLocation(Prayers.MOD_ID, "altar_crafting");
	public static final ResourceLocation ARROWS = new ResourceLocation(Prayers.MOD_ID, "textures/gui/arrow_widgets.png");
	private final IGuiHelper guiHelper;
	private final ITickTimer inputTimer;
	private final ITickTimer outputTimer;
	private final IDrawable arrowBackground;
	private final IDrawable animArrow;
	
	public AltarCraftingRecipeCategory(IGuiHelper helper) {
		this.guiHelper = helper;
		inputTimer = guiHelper.createTickTimer(200, 16, true);
		outputTimer = guiHelper.createTickTimer(200, 16, false);
		this.arrowBackground = helper.drawableBuilder(ARROWS, 0, 0, 7, 16).setTextureSize(14, 16).build();
		this.animArrow = helper.drawableBuilder(ARROWS, 7, 0, 7, 16).setTextureSize(14, 16).buildAnimated(inputTimer, StartDirection.TOP);
	}
	
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends AbstractAltarCraftingRecipe> getRecipeClass() {
		return AbstractAltarCraftingRecipe.class;
	}

	@Override
	public String getTitle() {
		return new TranslationTextComponent(LangUtil.buildTextLoc("jei.altar_crafting")).getString();
	}

	@Override
	public IDrawable getBackground() {
		return new AltarCraftingBackground();
	}
	
	@Override
	public IDrawable getIcon() {
		return this.guiHelper.createDrawableIngredient(new ItemStack(ModItems.ALTARS.get(AltarTypes.GILDED_SANDSTONE)::get));
	}

	@Override
	public void setIngredients(AbstractAltarCraftingRecipe recipe, IIngredients ingredients) {
		List<List<ItemStack>> inputs = recipe.getIngredients().stream().map(Ingredient::getItems).map(Lists::newArrayList).collect(Collectors.toList());
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AbstractAltarCraftingRecipe recipe, IIngredients ingredients) {
		int startX = 40;
		int startY = 6;
		int num = (int) ingredients.getInputs(VanillaTypes.ITEM).stream().filter(list -> !list.isEmpty() && (list.size() > 1 || !list.get(0).isEmpty())).count();
		int[] offsets = this.getSlotOffsets(num);
		Iterator<Direction> dirs = Direction.Plane.HORIZONTAL.iterator();
		for(int i = 0; i < num; i++) {
			recipeLayout.getItemStacks().init(i, true, new CraftingStandItemRenderer(guiHelper, inputTimer, dirs.next(), startX+offsets[i]+1, startY+1),
					startX+offsets[i], startY, 18, 18, 1, 1);
			recipeLayout.getItemStacks().setBackground(i, guiHelper.getSlotDrawable());
		}
		recipeLayout.getItemStacks().init(5, false, new CraftingStandItemRenderer(guiHelper, outputTimer, Direction.UP, startX-9+1, startY+40+1),
				startX-9, startY+40, 18, 18, 1, 1);
		recipeLayout.getItemStacks().setBackground(5, guiHelper.getSlotDrawable());
		
		recipeLayout.getItemStacks().set(ingredients);
	}

	public int[] getSlotOffsets(int numSlots) {
		int[] offsets = new int[numSlots];
		for(int i = 0; i < numSlots; i++) {
			offsets[i] = 9*(2*i-numSlots);
		}
		return offsets;
	}
	
	@Override
	public List<ITextComponent> getTooltipStrings(AbstractAltarCraftingRecipe recipe, double mouseX, double mouseY) {
		int startX = 40;
		int startY = 6;
		if(mouseX >= startX-3 && mouseX < startX+7 && mouseY >= startY+21 && mouseY < startY+37)
			return Lists.newArrayList(new TranslationTextComponent(LangUtil.buildTextLoc("points"), (int) recipe.getPoints()));
		return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
	}
	
	@Override
	public void draw(AbstractAltarCraftingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int startX = 40;
		int startY = 6;
		this.arrowBackground.draw(matrixStack, startX-3, startY+21);
		this.animArrow.draw(matrixStack, startX-3, startY+21);
	}
	
}