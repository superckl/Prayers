package me.superckl.prayers.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelOfferingTable extends ModelBase
{
	ModelRenderer Altar;
	ModelRenderer Altar1;
	ModelRenderer Altar2;
	ModelRenderer Altar3;
	ModelRenderer Altar4;
	ModelRenderer Altar9;
	ModelRenderer Altar10;
	ModelRenderer Altar11;
	ModelRenderer Altar12;
	ModelRenderer Pedestol1;
	ModelRenderer Pedestol1m1;
	ModelRenderer Pedestol2;
	ModelRenderer Pedestol2m1;
	ModelRenderer Pedestol3;
	ModelRenderer Pedestol3m1;
	ModelRenderer Pedestol4;
	ModelRenderer Pedestol4m1;

	public ModelOfferingTable()
	{
		this( 0.0f );
	}

	public ModelOfferingTable( final float par1 )
	{
		this.Altar = new ModelRenderer( this, 8, 2 );
		this.Altar.setTextureSize( 128, 64 );
		this.Altar.addBox( -6F, -8F, -7F, 12, 16, 14);
		this.Altar.setRotationPoint( 0F, 15F, 0F );
		this.Altar1 = new ModelRenderer( this, 4, 4 );
		this.Altar1.setTextureSize( 128, 64 );
		this.Altar1.addBox( -7F, -8F, -6F, 14, 16, 12);
		this.Altar1.setRotationPoint( 0F, 15F, 0F );
		this.Altar2 = new ModelRenderer( this, 3, 3 );
		this.Altar2.setTextureSize( 128, 64 );
		this.Altar2.addBox( -6.5F, -8F, -6.5F, 13, 16, 13);
		this.Altar2.setRotationPoint( 0F, 15F, 0F );
		this.Altar3 = new ModelRenderer( this, 2, 36 );
		this.Altar3.setTextureSize( 128, 64 );
		this.Altar3.addBox( -9F, -0.5F, -9F, 18, 1, 18);
		this.Altar3.setRotationPoint( 0F, 23F, 0F );
		this.Altar4 = new ModelRenderer( this, 2, 36 );
		this.Altar4.setTextureSize( 128, 64 );
		this.Altar4.addBox( -9F, -0.5F, -9F, 18, 1, 18);
		this.Altar4.setRotationPoint( 0F, 7F, 0F );
		this.Altar9 = new ModelRenderer( this, 83, 17 );
		this.Altar9.setTextureSize( 128, 64 );
		this.Altar9.addBox( -1F, -6F, -7.5F, 2, 12, 15);
		this.Altar9.setRotationPoint( -3F, 17F, 0F );
		this.Altar10 = new ModelRenderer( this, 82, 17 );
		this.Altar10.setTextureSize( 128, 64 );
		this.Altar10.addBox( -1F, -6F, -7.5F, 2, 12, 15);
		this.Altar10.setRotationPoint( 3F, 17F, 0F );
		this.Altar11 = new ModelRenderer( this, 93, 30 );
		this.Altar11.setTextureSize( 128, 64 );
		this.Altar11.addBox( -7.5F, -6F, -1F, 15, 12, 2);
		this.Altar11.setRotationPoint( 0F, 17F, 3F );
		this.Altar12 = new ModelRenderer( this, 93, 30 );
		this.Altar12.setTextureSize( 128, 64 );
		this.Altar12.addBox( -7.5F, -6F, -1F, 15, 12, 2);
		this.Altar12.setRotationPoint( 0F, 17F, -3F );
		this.Pedestol1 = new ModelRenderer( this, 9, 34 );
		this.Pedestol1.setTextureSize( 128, 64 );
		this.Pedestol1.addBox( -0.5F, -7.5F, -1F, 1, 15, 2);
		this.Pedestol1.setRotationPoint( 8F, 15F, -8F );
		this.Pedestol1m1 = new ModelRenderer( this, 9, 35 );
		this.Pedestol1m1.setTextureSize( 128, 64 );
		this.Pedestol1m1.addBox( -1F, -7.5F, -0.5F, 2, 15, 1);
		this.Pedestol1m1.setRotationPoint( 8F, 15F, -8F );
		this.Pedestol2 = new ModelRenderer( this, 9, 34 );
		this.Pedestol2.setTextureSize( 128, 64 );
		this.Pedestol2.addBox( -0.5F, -7.5F, -1F, 1, 15, 2);
		this.Pedestol2.setRotationPoint( -8F, 15F, -8F );
		this.Pedestol2m1 = new ModelRenderer( this, 9, 35 );
		this.Pedestol2m1.setTextureSize( 128, 64 );
		this.Pedestol2m1.addBox( -1F, -7.5F, -0.5F, 2, 15, 1);
		this.Pedestol2m1.setRotationPoint( -8F, 15F, -8F );
		this.Pedestol3 = new ModelRenderer( this, 9, 34 );
		this.Pedestol3.setTextureSize( 128, 64 );
		this.Pedestol3.addBox( -0.5F, -7.5F, -1F, 1, 15, 2);
		this.Pedestol3.setRotationPoint( -8F, 15F, 8F );
		this.Pedestol3m1 = new ModelRenderer( this, 9, 35 );
		this.Pedestol3m1.setTextureSize( 128, 64 );
		this.Pedestol3m1.addBox( -1F, -7.5F, -0.5F, 2, 15, 1);
		this.Pedestol3m1.setRotationPoint( -8F, 15F, 8F );
		this.Pedestol4 = new ModelRenderer( this, 9, 34 );
		this.Pedestol4.setTextureSize( 128, 64 );
		this.Pedestol4.addBox( -0.5F, -7.5F, -1F, 1, 15, 2);
		this.Pedestol4.setRotationPoint( 8F, 15F, 8F );
		this.Pedestol4m1 = new ModelRenderer( this, 9, 35 );
		this.Pedestol4m1.setTextureSize( 128, 64 );
		this.Pedestol4m1.addBox( -1F, -7.5F, -0.5F, 2, 15, 1);
		this.Pedestol4m1.setRotationPoint( 8F, 15F, 8F );
	}

	public void render(final float par7)
	{
		this.Altar.rotateAngleX = 0F;
		this.Altar.rotateAngleY = 0F;
		this.Altar.rotateAngleZ = 0F;
		this.Altar.renderWithRotation(par7);

		this.Altar1.rotateAngleX = 0F;
		this.Altar1.rotateAngleY = 0F;
		this.Altar1.rotateAngleZ = 0F;
		this.Altar1.renderWithRotation(par7);

		this.Altar2.rotateAngleX = 0F;
		this.Altar2.rotateAngleY = 0F;
		this.Altar2.rotateAngleZ = 0F;
		this.Altar2.renderWithRotation(par7);

		this.Altar3.rotateAngleX = 0F;
		this.Altar3.rotateAngleY = 0F;
		this.Altar3.rotateAngleZ = 0F;
		this.Altar3.renderWithRotation(par7);

		this.Altar4.rotateAngleX = 0F;
		this.Altar4.rotateAngleY = 0F;
		this.Altar4.rotateAngleZ = 0F;
		this.Altar4.renderWithRotation(par7);

		this.Altar9.rotateAngleX = 0F;
		this.Altar9.rotateAngleY = 0F;
		this.Altar9.rotateAngleZ = 0F;
		this.Altar9.renderWithRotation(par7);

		this.Altar10.rotateAngleX = 0F;
		this.Altar10.rotateAngleY = 0F;
		this.Altar10.rotateAngleZ = 0F;
		this.Altar10.renderWithRotation(par7);

		this.Altar11.rotateAngleX = 0F;
		this.Altar11.rotateAngleY = 0F;
		this.Altar11.rotateAngleZ = 0F;
		this.Altar11.renderWithRotation(par7);

		this.Altar12.rotateAngleX = 0F;
		this.Altar12.rotateAngleY = 0F;
		this.Altar12.rotateAngleZ = 0F;
		this.Altar12.renderWithRotation(par7);

		this.Pedestol1.rotateAngleX = 0F;
		this.Pedestol1.rotateAngleY = 0F;
		this.Pedestol1.rotateAngleZ = 0F;
		this.Pedestol1.renderWithRotation(par7);

		this.Pedestol1m1.rotateAngleX = 0F;
		this.Pedestol1m1.rotateAngleY = 0F;
		this.Pedestol1m1.rotateAngleZ = 0F;
		this.Pedestol1m1.renderWithRotation(par7);

		this.Pedestol2.rotateAngleX = 0F;
		this.Pedestol2.rotateAngleY = 0F;
		this.Pedestol2.rotateAngleZ = 0F;
		this.Pedestol2.renderWithRotation(par7);

		this.Pedestol2m1.rotateAngleX = 0F;
		this.Pedestol2m1.rotateAngleY = 0F;
		this.Pedestol2m1.rotateAngleZ = 0F;
		this.Pedestol2m1.renderWithRotation(par7);

		this.Pedestol3.rotateAngleX = 0F;
		this.Pedestol3.rotateAngleY = 0F;
		this.Pedestol3.rotateAngleZ = 0F;
		this.Pedestol3.renderWithRotation(par7);

		this.Pedestol3m1.rotateAngleX = 0F;
		this.Pedestol3m1.rotateAngleY = 0F;
		this.Pedestol3m1.rotateAngleZ = 0F;
		this.Pedestol3m1.renderWithRotation(par7);

		this.Pedestol4.rotateAngleX = 0F;
		this.Pedestol4.rotateAngleY = 0F;
		this.Pedestol4.rotateAngleZ = 0F;
		this.Pedestol4.renderWithRotation(par7);

		this.Pedestol4m1.rotateAngleX = 0F;
		this.Pedestol4m1.rotateAngleY = 0F;
		this.Pedestol4m1.rotateAngleZ = 0F;
		this.Pedestol4m1.renderWithRotation(par7);

	}

}
