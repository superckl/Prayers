package me.superckl.prayers.client.render;

import me.superckl.prayers.client.model.ModelUndeadPriest;
import me.superckl.prayers.common.reference.RenderData;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderUndeadWizardPriest extends RenderLiving{

	public RenderUndeadWizardPriest() {
		super(new ModelUndeadPriest(), .7f);

	}

	@Override
	protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
		return RenderData.UNDEAD_PRIEST_MODEL;
	}

}
