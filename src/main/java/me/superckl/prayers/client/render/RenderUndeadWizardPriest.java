package me.superckl.prayers.client.render;

import me.superckl.prayers.client.model.ModelUndeadWizardPriest;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderUndeadWizardPriest extends RendererLivingEntity{

	public RenderUndeadWizardPriest() {
		super(new ModelUndeadWizardPriest(), 1F);

	}

	@Override
	protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}

}
