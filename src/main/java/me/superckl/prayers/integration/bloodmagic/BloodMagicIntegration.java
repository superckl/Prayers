package me.superckl.prayers.integration.bloodmagic;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.api.AltarRegistry;
import me.superckl.prayers.integration.IIntegrationModule;

public class BloodMagicIntegration implements IIntegrationModule{

	@Override
	public void preInit() {}

	@Override
	public void init() {
		if(Prayers.getInstance().getConfig().isOrbRecipe())
			AltarRegistry.registerOfferingTableRecipe(new OrbChargeTableRecipe());
	}

	@Override
	public void postInit() {}

	@Override
	public String getName() {
		return "Blood Magic Integration";
	}

}
