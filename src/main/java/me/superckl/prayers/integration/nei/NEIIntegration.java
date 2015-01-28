package me.superckl.prayers.integration.nei;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayers.common.altar.AltarRegistry;
import me.superckl.prayers.common.altar.crafting.BasicTableOreCraftingHandler;
import me.superckl.prayers.common.altar.crafting.OfferingTableCraftingHandler;
import me.superckl.prayers.common.reference.RenderData;
import me.superckl.prayers.integration.IIntegrationModule;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class NEIIntegration implements IIntegrationModule{

	@Override
	public void preInit() {}

	@Override
	public void init() {}

	@Override
	public void postInit() {
		final OfferingTableRecipeHandler handler = new OfferingTableRecipeHandler();
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
	}

	public static class OfferingTableRecipeHandler extends TemplateRecipeHandler{

		private final Minecraft mc = Minecraft.getMinecraft();

		@Override
		public String getRecipeName() {
			return "Offering Table";
		}

		@Override
		public String getGuiTexture() {
			return RenderData.NEI_GUI;
		}

		@Override
		public void loadCraftingRecipes(final ItemStack result) {
			for(final OfferingTableCraftingHandler handler:AltarRegistry.getRegisteredRecipes())
				if(handler.getResult().isItemEqual(result))
					this.arecipes.add(new OfferingTableCachedRecipe(handler));
		}

		@Override
		public void loadUsageRecipes(final ItemStack ingredient) {
			for(final OfferingTableCraftingHandler handler:AltarRegistry.getRegisteredRecipes())
				if(handler instanceof BasicTableOreCraftingHandler){
					final List<Object> objs = ((BasicTableOreCraftingHandler)handler).getInput();
					for(final Object obj:objs)
						if((obj instanceof ItemStack) && ((ItemStack)obj).isItemEqual(ingredient)){
							this.arecipes.add(new OfferingTableCachedRecipe(handler));
							break;
						}else if(obj instanceof List){
							boolean shouldBreak = false;
							for(final Object obj2:((List)obj))
								if((obj2 instanceof ItemStack) && ((ItemStack)obj2).isItemEqual(ingredient)){
									this.arecipes.add(new OfferingTableCachedRecipe(handler));
									shouldBreak = true;
									break;
								}
							if(shouldBreak)
								break;
						}
				}else{
					if(handler.getBaseIngredient().isItemEqual(ingredient)){
						this.arecipes.add(new OfferingTableCachedRecipe(handler));
						continue;
					}
					for(final ItemStack stack:handler.getTertiaryIngredients())
						if(stack.isItemEqual(ingredient)){
							this.arecipes.add(new OfferingTableCachedRecipe(handler));
							break;
						}
				}
		}

		@Override
		public void drawExtras(final int recipe) {
			final OfferingTableCachedRecipe cache = (OfferingTableCachedRecipe) this.arecipes.get(recipe);
			this.mc.fontRenderer.drawString("Points: "+cache.points, 130-(this.mc.fontRenderer.getStringWidth("Points: "+cache.points)/2), 45, 0x404040);
			this.mc.fontRenderer.drawString("Time: "+cache.time, 130-(this.mc.fontRenderer.getStringWidth("Time: "+cache.time)/2), 55, 0x404040);
			GuiDraw.changeTexture(RenderData.WIDGETS);
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			this.drawProgressBar(77, 29, 0, 0, 22, 15, ((float) this.cycleticks%cache.time)/(cache.time), 0);
			GL11.glPopMatrix();
		}

		private class OfferingTableCachedRecipe extends CachedRecipe{

			private final PositionedStack result;
			private final List<PositionedStack> ingredients = new ArrayList<PositionedStack>();
			private final int points;
			private final int time;
			private final boolean isOre;

			public OfferingTableCachedRecipe(final OfferingTableCraftingHandler handler){
				this.isOre = handler instanceof BasicTableOreCraftingHandler;
				this.result = new PositionedStack(handler.getResult(), 122, 26, this.isOre);
				this.points = (int) handler.getOverallDrain();
				this.time = handler.getOverallTime();
				List<Object> objs;
				if(this.isOre)
					objs = new ArrayList<Object>(((BasicTableOreCraftingHandler)handler).getInput());
				else{
					objs = new ArrayList<Object>(handler.getTertiaryIngredients());
					objs.add(0, handler.getBaseIngredient());
				}
				this.ingredients.add(new PositionedStack(objs.remove(0), 33, 28, false));
				final float increment = (float) ((2D*Math.PI)/objs.size());
				float current = (float) (Math.PI/2D);

				for(final Object stack:objs){
					this.ingredients.add(new PositionedStack(stack, (int) (33+(Math.cos(current)*24)), (int) (30-(Math.sin(current)*24)), false));
					current += increment;
				}
			}

			@Override
			public PositionedStack getResult() {
				return this.result;
			}

			@Override
			public List<PositionedStack> getIngredients() {
				return this.getCycledIngredients(OfferingTableRecipeHandler.this.cycleticks/20, this.ingredients);
			}

		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Not Enough Items Integration";
	}

}
