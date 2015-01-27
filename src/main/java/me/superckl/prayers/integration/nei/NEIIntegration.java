package me.superckl.prayers.integration.nei;

import me.superckl.prayers.integration.IIntegrationModule;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class NEIIntegration implements IIntegrationModule{

	@Override
	public void preInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

	public static class OfferingTableRecipeHandler extends TemplateRecipeHandler{

		@Override
		public String getRecipeName() {
			return "Offering Table";
		}

		@Override
		public String getGuiTexture() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Not Enough Items Integration";
	}

}
