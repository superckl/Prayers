package me.superckl.prayers.client.render;

import me.superckl.prayers.client.model.ModelOfferingTable;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class RenderOfferingTable extends TileEntitySpecialRenderer{

	private final ModelOfferingTable model = new ModelOfferingTable();
	private final RenderItem customRenderItem;

	public RenderOfferingTable() {
		this.customRenderItem = new RenderItem()
		{
			@Override
			public boolean shouldBob()
			{
				return false;
			}
		};

		this.customRenderItem.setRenderManager(RenderManager.instance);
	}

	@Override
	public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float scale) {
		if((te instanceof TileEntityOfferingTable) == false)
			return;
		final TileEntityOfferingTable offer = (TileEntityOfferingTable) te;

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);

		//this.bindTexture(Textures.Model.ALUDEL);

		this.model.render();

		GL11.glPopMatrix();

		if(offer.getCurrentItem() != null){
			GL11.glPushMatrix();

			final float scaleFactor = .85F;
			final float rotationAngle = (float) ((720.0 * (System.currentTimeMillis() & 0x3FFFL)) / 0x3FFFL)*.5F;

			final EntityItem entity = new EntityItem(offer.getWorldObj());
			entity.hoverStart = 0F;
			entity.setEntityItemStack(offer.getCurrentItem());

			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.25F, (float) z + 0.5F);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
			GL11.glRotatef(rotationAngle, 0.0F, 1.0F, 0.0F);

			this.customRenderItem.doRender(entity, 0, 0, 0, 0, 0);

			GL11.glPopMatrix();
		}

		if(!offer.getTertiaryIngredients().isEmpty()){

			GL11.glPushMatrix();

			final float scaleFactor = .37F;
			final float rotationAngle = (float) ((720.0 * (System.currentTimeMillis() & 0x3FFFL)) / 0x3FFFL)*.5F;

			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.15F, (float) z + 0.5F);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);

			final float increment = (float) ((2D*Math.PI)/offer.getTertiaryIngredients().size());
			float current = (float) Math.toRadians(rotationAngle);

			for(final ItemStack stack:offer.getTertiaryIngredients()){
				final EntityItem entity = new EntityItem(offer.getWorldObj());
				entity.hoverStart = 0F;
				entity.setEntityItemStack(stack);

				final double cos = Math.cos(current), sin = Math.sin(current);
				this.customRenderItem.doRender(entity, cos, 0, -sin, 0, (float) (current+Math.PI)*20F);
				current += increment;
			}

			GL11.glPopMatrix();

		}

	}

}
