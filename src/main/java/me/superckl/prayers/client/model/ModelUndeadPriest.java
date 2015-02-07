package me.superckl.prayers.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelUndeadPriest extends ModelBase
{
	ModelRenderer rib1;
	ModelRenderer rib32;
	ModelRenderer rib15;
	ModelRenderer rib22;
	ModelRenderer rib3;
	ModelRenderer rib2;
	ModelRenderer rib23;
	ModelRenderer rib24;
	ModelRenderer rib19;
	ModelRenderer rib20;
	ModelRenderer rib21;
	ModelRenderer rib16;
	ModelRenderer rib18;
	ModelRenderer rib17;
	ModelRenderer rib13;
	ModelRenderer rib14;
	ModelRenderer rib6;
	ModelRenderer rib35;
	ModelRenderer rib34;
	ModelRenderer rib31;
	ModelRenderer rib33;
	ModelRenderer rib36;
	ModelRenderer rib9;
	ModelRenderer rib5;
	ModelRenderer rib4;
	ModelRenderer rib8;
	ModelRenderer rib7;
	ModelRenderer rib12;
	ModelRenderer rib11;
	ModelRenderer rib10;
	ModelRenderer rib27;
	ModelRenderer rib25;
	ModelRenderer rib26;
	ModelRenderer rib30;
	ModelRenderer rib29;
	ModelRenderer rib28;
	ModelRenderer sternum;
	ModelRenderer crystal1;
	ModelRenderer crystal2;
	ModelRenderer crystal3;
	ModelRenderer head1;
	ModelRenderer head2;
	ModelRenderer head3;
	ModelRenderer head4;
	ModelRenderer head5;
	ModelRenderer head6;
	ModelRenderer head7;
	ModelRenderer head8;
	ModelRenderer head9;
	ModelRenderer spine1;
	ModelRenderer spine2;
	ModelRenderer spine3;
	ModelRenderer spine5;
	ModelRenderer spine8;
	ModelRenderer spine4;
	ModelRenderer spine7;
	ModelRenderer spine6;
	ModelRenderer spine9;
	ModelRenderer neck;
	ModelRenderer lshoulder;
	ModelRenderer larm1;
	ModelRenderer larm2;
	ModelRenderer elbow;
	ModelRenderer larm3;
	ModelRenderer larm4;
	ModelRenderer larm5;
	ModelRenderer larm6;
	ModelRenderer larm7;
	ModelRenderer larm8;
	ModelRenderer larm9;
	ModelRenderer larm10;
	ModelRenderer larm11;
	ModelRenderer rarm1;
	ModelRenderer rhand;
	ModelRenderer staff1;
	ModelRenderer staff2;
	ModelRenderer staff3;
	ModelRenderer staff4;
	ModelRenderer staff7;
	ModelRenderer staff;
	ModelRenderer staff11;
	ModelRenderer staff12;
	ModelRenderer staff10;
	ModelRenderer staff9;
	ModelRenderer staff8;
	ModelRenderer staff15;
	ModelRenderer staff6;
	ModelRenderer staff14;
	ModelRenderer staff16;
	ModelRenderer staff13;
	ModelRenderer staff5;
	ModelRenderer staffCrystal1;
	ModelRenderer staffCrystal3;
	ModelRenderer staffCrystal2;
	ModelRenderer rshoulder;
	ModelRenderer rarm3;
	ModelRenderer rarm2;
	ModelRenderer relbow;
	ModelRenderer robe1;
	ModelRenderer robe2;
	ModelRenderer robe4;
	ModelRenderer robe5;
	ModelRenderer robe6;
	ModelRenderer robe3;
	ModelRenderer robe7;
	ModelRenderer robe8;
	ModelRenderer robe9;

	public ModelUndeadPriest()
	{
		this( 0.0f );
	}

	public ModelUndeadPriest( final float par1 )
	{
		this.rib1 = new ModelRenderer( this, 40, 23 );
		this.rib1.setTextureSize( 128, 64 );
		this.rib1.addBox( -4F, 0F, 4F, 3, 1, 1);
		this.rib1.setRotationPoint( 0F, -14F, 4F );
		this.rib32 = new ModelRenderer( this, 40, 13 );
		this.rib32.setTextureSize( 128, 64 );
		this.rib32.addBox( -5.2F, 10F, -2.5F, 1, 1, 7);
		this.rib32.setRotationPoint( 0F, -14F, 4F );
		this.rib15 = new ModelRenderer( this, 40, 23 );
		this.rib15.setTextureSize( 128, 64 );
		this.rib15.addBox( -4F, 4F, -3F, 3, 1, 1);
		this.rib15.setRotationPoint( 0F, -14F, 4F );
		this.rib22 = new ModelRenderer( this, 40, 21 );
		this.rib22.setTextureSize( 128, 64 );
		this.rib22.addBox( 1F, 6F, 4F, 4, 1, 1);
		this.rib22.setRotationPoint( 0F, -14F, 4F );
		this.rib3 = new ModelRenderer( this, 40, 23 );
		this.rib3.setTextureSize( 128, 64 );
		this.rib3.addBox( -4F, 0F, -3F, 3, 1, 1);
		this.rib3.setRotationPoint( 0F, -14F, 4F );
		this.rib2 = new ModelRenderer( this, 40, 13 );
		this.rib2.setTextureSize( 128, 64 );
		this.rib2.addBox( -4.5F, 0F, -2.5F, 1, 1, 7);
		this.rib2.setRotationPoint( 0F, -14F, 4F );
		this.rib23 = new ModelRenderer( this, 40, 13 );
		this.rib23.setTextureSize( 128, 64 );
		this.rib23.addBox( 4.3F, 6F, -2.5F, 1, 1, 7);
		this.rib23.setRotationPoint( 0F, -14F, 4F );
		this.rib24 = new ModelRenderer( this, 40, 21 );
		this.rib24.setTextureSize( 128, 64 );
		this.rib24.addBox( 1F, 6F, -3F, 4, 1, 1);
		this.rib24.setRotationPoint( 0F, -14F, 4F );
		this.rib19 = new ModelRenderer( this, 40, 21 );
		this.rib19.setTextureSize( 128, 64 );
		this.rib19.addBox( -5F, 6F, 4F, 4, 1, 1);
		this.rib19.setRotationPoint( 0F, -14F, 4F );
		this.rib20 = new ModelRenderer( this, 40, 13 );
		this.rib20.setTextureSize( 128, 64 );
		this.rib20.addBox( -5.3F, 6F, -2.5F, 1, 1, 7);
		this.rib20.setRotationPoint( 0F, -14F, 4F );
		this.rib21 = new ModelRenderer( this, 40, 21 );
		this.rib21.setTextureSize( 128, 64 );
		this.rib21.addBox( -5F, 6F, -3F, 4, 1, 1);
		this.rib21.setRotationPoint( 0F, -14F, 4F );
		this.rib16 = new ModelRenderer( this, 40, 23 );
		this.rib16.setTextureSize( 128, 64 );
		this.rib16.addBox( 1F, 4F, 4F, 3, 1, 1);
		this.rib16.setRotationPoint( 0F, -14F, 4F );
		this.rib18 = new ModelRenderer( this, 40, 23 );
		this.rib18.setTextureSize( 128, 64 );
		this.rib18.addBox( 1F, 4F, -3F, 3, 1, 1);
		this.rib18.setRotationPoint( 0F, -14F, 4F );
		this.rib17 = new ModelRenderer( this, 40, 13 );
		this.rib17.setTextureSize( 128, 64 );
		this.rib17.addBox( 4F, 4F, -2.5F, 1, 1, 7);
		this.rib17.setRotationPoint( 0F, -14F, 4F );
		this.rib13 = new ModelRenderer( this, 40, 23 );
		this.rib13.setTextureSize( 128, 64 );
		this.rib13.addBox( -4F, 4F, 4F, 3, 1, 1);
		this.rib13.setRotationPoint( 0F, -14F, 4F );
		this.rib14 = new ModelRenderer( this, 40, 13 );
		this.rib14.setTextureSize( 128, 64 );
		this.rib14.addBox( -5F, 4F, -2.5F, 1, 1, 7);
		this.rib14.setRotationPoint( 0F, -14F, 4F );
		this.rib6 = new ModelRenderer( this, 40, 23 );
		this.rib6.setTextureSize( 128, 64 );
		this.rib6.addBox( 1F, 0F, -3F, 3, 1, 1);
		this.rib6.setRotationPoint( 0F, -14F, 4F );
		this.rib35 = new ModelRenderer( this, 40, 13 );
		this.rib35.setTextureSize( 128, 64 );
		this.rib35.addBox( 4.2F, 10F, -2.5F, 1, 1, 7);
		this.rib35.setRotationPoint( 0F, -14F, 4F );
		this.rib34 = new ModelRenderer( this, 40, 21 );
		this.rib34.setTextureSize( 128, 64 );
		this.rib34.addBox( 1F, 10F, 4F, 4, 1, 1);
		this.rib34.setRotationPoint( 0F, -14F, 4F );
		this.rib31 = new ModelRenderer( this, 40, 21 );
		this.rib31.setTextureSize( 128, 64 );
		this.rib31.addBox( -5F, 10F, 4F, 4, 1, 1);
		this.rib31.setRotationPoint( 0F, -14F, 4F );
		this.rib33 = new ModelRenderer( this, 40, 25 );
		this.rib33.setTextureSize( 128, 64 );
		this.rib33.addBox( -5F, 10F, -3F, 2, 1, 1);
		this.rib33.setRotationPoint( 0F, -14F, 4F );
		this.rib36 = new ModelRenderer( this, 40, 25 );
		this.rib36.setTextureSize( 128, 64 );
		this.rib36.addBox( 3F, 10F, -3F, 2, 1, 1);
		this.rib36.setRotationPoint( 0F, -14F, 4F );
		this.rib9 = new ModelRenderer( this, 40, 23 );
		this.rib9.setTextureSize( 128, 64 );
		this.rib9.addBox( -4F, 2F, -3F, 3, 1, 1);
		this.rib9.setRotationPoint( 0F, -14F, 4F );
		this.rib5 = new ModelRenderer( this, 40, 13 );
		this.rib5.setTextureSize( 128, 64 );
		this.rib5.addBox( 3.5F, 0F, -2.5F, 1, 1, 7);
		this.rib5.setRotationPoint( 0F, -14F, 4F );
		this.rib4 = new ModelRenderer( this, 40, 23 );
		this.rib4.setTextureSize( 128, 64 );
		this.rib4.addBox( 1F, 0F, 4F, 3, 1, 1);
		this.rib4.setRotationPoint( 0F, -14F, 4F );
		this.rib8 = new ModelRenderer( this, 40, 13 );
		this.rib8.setTextureSize( 128, 64 );
		this.rib8.addBox( -4.8F, 2F, -2.5F, 1, 1, 7);
		this.rib8.setRotationPoint( 0F, -14F, 4F );
		this.rib7 = new ModelRenderer( this, 40, 23 );
		this.rib7.setTextureSize( 128, 64 );
		this.rib7.addBox( -4F, 2F, 4F, 3, 1, 1);
		this.rib7.setRotationPoint( 0F, -14F, 4F );
		this.rib12 = new ModelRenderer( this, 40, 23 );
		this.rib12.setTextureSize( 128, 64 );
		this.rib12.addBox( 1F, 2F, -3F, 3, 1, 1);
		this.rib12.setRotationPoint( 0F, -14F, 4F );
		this.rib11 = new ModelRenderer( this, 40, 13 );
		this.rib11.setTextureSize( 128, 64 );
		this.rib11.addBox( 3.8F, 2F, -2.5F, 1, 1, 7);
		this.rib11.setRotationPoint( 0F, -14F, 4F );
		this.rib10 = new ModelRenderer( this, 40, 23 );
		this.rib10.setTextureSize( 128, 64 );
		this.rib10.addBox( 1F, 2F, 4F, 3, 1, 1);
		this.rib10.setRotationPoint( 0F, -14F, 4F );
		this.rib27 = new ModelRenderer( this, 40, 23 );
		this.rib27.setTextureSize( 128, 64 );
		this.rib27.addBox( -5F, 8F, -3F, 3, 1, 1);
		this.rib27.setRotationPoint( 0F, -14F, 4F );
		this.rib25 = new ModelRenderer( this, 40, 21 );
		this.rib25.setTextureSize( 128, 64 );
		this.rib25.addBox( -5F, 8F, 4F, 4, 1, 1);
		this.rib25.setRotationPoint( 0F, -14F, 4F );
		this.rib26 = new ModelRenderer( this, 40, 13 );
		this.rib26.setTextureSize( 128, 64 );
		this.rib26.addBox( -5.5F, 8F, -2.5F, 1, 1, 7);
		this.rib26.setRotationPoint( 0F, -14F, 4F );
		this.rib30 = new ModelRenderer( this, 40, 23 );
		this.rib30.setTextureSize( 128, 64 );
		this.rib30.addBox( 2F, 8F, -3F, 3, 1, 1);
		this.rib30.setRotationPoint( 0F, -14F, 4F );
		this.rib29 = new ModelRenderer( this, 40, 13 );
		this.rib29.setTextureSize( 128, 64 );
		this.rib29.addBox( 4.5F, 8F, -2.5F, 1, 1, 7);
		this.rib29.setRotationPoint( 0F, -14F, 4F );
		this.rib28 = new ModelRenderer( this, 40, 21 );
		this.rib28.setTextureSize( 128, 64 );
		this.rib28.addBox( 1F, 8F, 4F, 4, 1, 1);
		this.rib28.setRotationPoint( 0F, -14F, 4F );
		this.sternum = new ModelRenderer( this, 52, 7 );
		this.sternum.setTextureSize( 128, 64 );
		this.sternum.addBox( -1F, 0F, -3.3F, 2, 6, 1);
		this.sternum.setRotationPoint( 0F, -14F, 4F );
		this.crystal1 = new ModelRenderer( this, 116, 40 );
		this.crystal1.setTextureSize( 128, 64 );
		this.crystal1.addBox( -1.5F, -1.5F, -1.5F, 3, 3, 3);
		this.crystal1.setRotationPoint( 0F, -5F, 5F );
		this.crystal2 = new ModelRenderer( this, 116, 40 );
		this.crystal2.setTextureSize( 128, 64 );
		this.crystal2.addBox( -1.5F, -1.5F, -1.5F, 3, 3, 3);
		this.crystal2.setRotationPoint( 1.485731E-07F, -5F, 5F );
		this.crystal3 = new ModelRenderer( this, 116, 40 );
		this.crystal3.setTextureSize( 128, 64 );
		this.crystal3.addBox( -1.5F, -1.5F, -1.5F, 3, 3, 3);
		this.crystal3.setRotationPoint( 1.485731E-07F, -5F, 5F );
		this.head1 = new ModelRenderer( this, 0, 0 );
		this.head1.setTextureSize( 128, 64 );
		this.head1.addBox( -3F, -8F, -4F, 6, 4, 8);
		this.head1.setRotationPoint( 0F, -15F, 3F );
		this.head2 = new ModelRenderer( this, 28, 17 );
		this.head2.setTextureSize( 128, 64 );
		this.head2.addBox( -2F, -4F, -3.7F, 4, 1, 1);
		this.head2.setRotationPoint( 0F, -15F, 3F );
		this.head3 = new ModelRenderer( this, 3, 18 );
		this.head3.setTextureSize( 128, 64 );
		this.head3.addBox( -2.5F, -6.5F, -3.5F, 5, 1, 7);
		this.head3.setRotationPoint( 0F, -17.5F, 3F );
		this.head4 = new ModelRenderer( this, 29, 1 );
		this.head4.setTextureSize( 128, 64 );
		this.head4.addBox( 1.4F, -8F, -3.5F, 1, 2, 4);
		this.head4.setRotationPoint( 0F, -14.4F, 10.6F );
		this.head5 = new ModelRenderer( this, 29, 1 );
		this.head5.setTextureSize( 128, 64 );
		this.head5.addBox( -2.4F, -8F, -3.5F, 1, 2, 4);
		this.head5.setRotationPoint( -2.384186E-07F, -14.39999F, 10.6F );
		this.head6 = new ModelRenderer( this, 28, 7 );
		this.head6.setTextureSize( 128, 64 );
		this.head6.addBox( -2.5F, -3F, -11F, 1, 2, 5);
		this.head6.setRotationPoint( -2.384186E-07F, -14.39999F, 10.6F );
		this.head7 = new ModelRenderer( this, 28, 7 );
		this.head7.setTextureSize( 128, 64 );
		this.head7.addBox( 1.5F, -3F, -11F, 1, 2, 5);
		this.head7.setRotationPoint( -2.384186E-07F, -14.39999F, 10.6F );
		this.head8 = new ModelRenderer( this, 28, 19 );
		this.head8.setTextureSize( 128, 64 );
		this.head8.addBox( -1.5F, -3.5F, -10.8F, 3, 1, 1);
		this.head8.setRotationPoint( -2.384186E-07F, -14.39999F, 10.6F );
		this.head9 = new ModelRenderer( this, 28, 14 );
		this.head9.setTextureSize( 128, 64 );
		this.head9.addBox( -1.5F, -3F, -11F, 3, 2, 1);
		this.head9.setRotationPoint( -2.384186E-07F, -14.39999F, 10.6F );
		this.spine1 = new ModelRenderer( this, 46, 0 );
		this.spine1.setTextureSize( 128, 64 );
		this.spine1.addBox( -1F, 0F, 4F, 2, 12, 1);
		this.spine1.setRotationPoint( 0F, -14F, 4F );
		this.spine2 = new ModelRenderer( this, 52, 0 );
		this.spine2.setTextureSize( 128, 64 );
		this.spine2.addBox( -4F, 0F, -2F, 2, 6, 1);
		this.spine2.setRotationPoint( 3F, -2F, 10F );
		this.spine3 = new ModelRenderer( this, 58, 0 );
		this.spine3.setTextureSize( 128, 64 );
		this.spine3.addBox( -4F, 0F, -2F, 1, 3, 1);
		this.spine3.setRotationPoint( 3.5F, 4F, 8F );
		this.spine5 = new ModelRenderer( this, 40, 27 );
		this.spine5.setTextureSize( 128, 64 );
		this.spine5.addBox( -0.5F, 2F, 4.9F, 1, 1, 1);
		this.spine5.setRotationPoint( 0F, -14F, 4F );
		this.spine8 = new ModelRenderer( this, 40, 27 );
		this.spine8.setTextureSize( 128, 64 );
		this.spine8.addBox( -0.5F, 8F, 4.6F, 1, 1, 1);
		this.spine8.setRotationPoint( 0F, -14F, 4F );
		this.spine4 = new ModelRenderer( this, 40, 27 );
		this.spine4.setTextureSize( 128, 64 );
		this.spine4.addBox( -0.5F, 0F, 5F, 1, 1, 1);
		this.spine4.setRotationPoint( 0F, -14F, 4F );
		this.spine7 = new ModelRenderer( this, 40, 27 );
		this.spine7.setTextureSize( 128, 64 );
		this.spine7.addBox( -0.5F, 6F, 4.7F, 1, 1, 1);
		this.spine7.setRotationPoint( 0F, -14F, 4F );
		this.spine6 = new ModelRenderer( this, 40, 27 );
		this.spine6.setTextureSize( 128, 64 );
		this.spine6.addBox( -0.5F, 4F, 4.8F, 1, 1, 1);
		this.spine6.setRotationPoint( 0F, -14F, 4F );
		this.spine9 = new ModelRenderer( this, 40, 27 );
		this.spine9.setTextureSize( 128, 64 );
		this.spine9.addBox( -0.5F, 10F, 4.5F, 1, 1, 1);
		this.spine9.setRotationPoint( 0F, -14F, 4F );
		this.neck = new ModelRenderer( this, 40, 0 );
		this.neck.setTextureSize( 128, 64 );
		this.neck.addBox( -1F, 0F, -1F, 2, 7, 1);
		this.neck.setRotationPoint( 0F, -13F, 8F );
		this.lshoulder = new ModelRenderer( this, 50, 22 );
		this.lshoulder.setTextureSize( 128, 64 );
		this.lshoulder.addBox( 0F, -1F, -2F, 4, 2, 4);
		this.lshoulder.setRotationPoint( 3F, -15F, 4.5F );
		this.larm1 = new ModelRenderer( this, 106, 0 );
		this.larm1.setTextureSize( 128, 64 );
		this.larm1.addBox( 1F, 1F, -1.5F, 1, 12, 1);
		this.larm1.setRotationPoint( 3.996775F, -14.91975F, 4.5F );
		this.larm2 = new ModelRenderer( this, 106, 0 );
		this.larm2.setTextureSize( 128, 64 );
		this.larm2.addBox( 1F, 1F, 0.5F, 1, 12, 1);
		this.larm2.setRotationPoint( 3.996774F, -14.91975F, 4.5F );
		this.elbow = new ModelRenderer( this, 13, 13 );
		this.elbow.setTextureSize( 128, 64 );
		this.elbow.addBox( -1F, -1F, -1.5F, 2, 2, 3);
		this.elbow.setRotationPoint( 11F, -3.040003F, 4.5F );
		this.larm3 = new ModelRenderer( this, 100, 0 );
		this.larm3.setTextureSize( 128, 64 );
		this.larm3.addBox( 0F, 0F, -1F, 1, 10, 2);
		this.larm3.setRotationPoint( 11.61399F, -3.008533F, 4.5F );
		this.larm4 = new ModelRenderer( this, 88, 7 );
		this.larm4.setTextureSize( 128, 64 );
		this.larm4.addBox( 0F, 10F, -1.5F, 1, 4, 3);
		this.larm4.setRotationPoint( 11.61399F, -3.008535F, 4.5F );
		this.larm5 = new ModelRenderer( this, 88, 14 );
		this.larm5.setTextureSize( 128, 64 );
		this.larm5.addBox( 0F, 0F, -1F, 1, 2, 1);
		this.larm5.setRotationPoint( 11.99788F, 7.984766F, 2.499998F );
		this.larm6 = new ModelRenderer( this, 88, 14 );
		this.larm6.setTextureSize( 128, 64 );
		this.larm6.addBox( 0F, 0F, 0F, 1, 2, 1);
		this.larm6.setRotationPoint( 12.03569F, 10.4547F, 6F );
		this.larm7 = new ModelRenderer( this, 88, 14 );
		this.larm7.setTextureSize( 128, 64 );
		this.larm7.addBox( 0.5F, 1.5F, 0F, 1, 2, 1);
		this.larm7.setRotationPoint( 12.90172F, 10.9547F, 6F );
		this.larm8 = new ModelRenderer( this, 92, 14 );
		this.larm8.setTextureSize( 128, 64 );
		this.larm8.addBox( 0F, 0F, 0F, 1, 3, 1);
		this.larm8.setRotationPoint( 12.08513F, 10.48324F, 4.999999F );
		this.larm9 = new ModelRenderer( this, 88, 14 );
		this.larm9.setTextureSize( 128, 64 );
		this.larm9.addBox( 0.7F, 2.5F, 0F, 1, 2, 1);
		this.larm9.setRotationPoint( 13.33246F, 11.20862F, 4.999999F );
		this.larm10 = new ModelRenderer( this, 88, 14 );
		this.larm10.setTextureSize( 128, 64 );
		this.larm10.addBox( 0.5F, 1.5F, 0F, 1, 2, 1);
		this.larm10.setRotationPoint( 12.90172F, 10.9547F, 3.999999F );
		this.larm11 = new ModelRenderer( this, 88, 14 );
		this.larm11.setTextureSize( 128, 64 );
		this.larm11.addBox( 0F, 0F, 0F, 1, 2, 1);
		this.larm11.setRotationPoint( 12.03569F, 10.45469F, 3.999999F );
		this.rarm1 = new ModelRenderer( this, 100, 0 );
		this.rarm1.setTextureSize( 128, 64 );
		this.rarm1.addBox( -1F, 0F, 0F, 1, 10, 2);
		this.rarm1.setRotationPoint( -11F, -3.000002F, 4F );
		this.rhand = new ModelRenderer( this, 88, 0 );
		this.rhand.setTextureSize( 128, 64 );
		this.rhand.addBox( -2F, 10F, -0.5F, 3, 4, 3);
		this.rhand.setRotationPoint( -11F, -3F, 4.000001F );
		this.staff1 = new ModelRenderer( this, 124, 0 );
		this.staff1.setTextureSize( 128, 64 );
		this.staff1.addBox( -0.5F, 3F, -0.5F, 1, 1, 1);
		this.staff1.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff2 = new ModelRenderer( this, 124, 2 );
		this.staff2.setTextureSize( 128, 64 );
		this.staff2.addBox( -0.5F, 3F, -0.5F, 1, 2, 1);
		this.staff2.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff3 = new ModelRenderer( this, 124, 0 );
		this.staff3.setTextureSize( 128, 64 );
		this.staff3.addBox( -0.5F, 3F, -0.5F, 1, 1, 1);
		this.staff3.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff4 = new ModelRenderer( this, 124, 2 );
		this.staff4.setTextureSize( 128, 64 );
		this.staff4.addBox( -0.5F, 3F, -0.5F, 1, 2, 1);
		this.staff4.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff7 = new ModelRenderer( this, 122, 5 );
		this.staff7.setTextureSize( 128, 64 );
		this.staff7.addBox( -0.5F, 3F, -1F, 1, 2, 2);
		this.staff7.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff = new ModelRenderer( this, 80, 0 );
		this.staff.setTextureSize( 128, 64 );
		this.staff.addBox( -1F, -19F, -13F, 1, 11, 1);
		this.staff.setRotationPoint( -11F, -2.999998F, 3.999997F );
		this.staff11 = new ModelRenderer( this, 124, 0 );
		this.staff11.setTextureSize( 128, 64 );
		this.staff11.addBox( -0.5F, 3F, -0.5F, 1, 1, 1);
		this.staff11.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff12 = new ModelRenderer( this, 124, 14 );
		this.staff12.setTextureSize( 128, 64 );
		this.staff12.addBox( -1F, -19F, -13F, 1, 13, 1);
		this.staff12.setRotationPoint( -11F, -2.999998F, 3.999997F );
		this.staff10 = new ModelRenderer( this, 124, 2 );
		this.staff10.setTextureSize( 128, 64 );
		this.staff10.addBox( -0.5F, 3F, -0.5F, 1, 2, 1);
		this.staff10.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff9 = new ModelRenderer( this, 124, 0 );
		this.staff9.setTextureSize( 128, 64 );
		this.staff9.addBox( -0.5F, 3F, -0.5F, 1, 1, 1);
		this.staff9.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff8 = new ModelRenderer( this, 124, 2 );
		this.staff8.setTextureSize( 128, 64 );
		this.staff8.addBox( -0.5F, 3F, -0.5F, 1, 2, 1);
		this.staff8.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff15 = new ModelRenderer( this, 118, 14 );
		this.staff15.setTextureSize( 128, 64 );
		this.staff15.addBox( -1F, 5F, -13.5F, 1, 6, 2);
		this.staff15.setRotationPoint( -11F, -2.999998F, 3.999997F );
		this.staff6 = new ModelRenderer( this, 120, 9 );
		this.staff6.setTextureSize( 128, 64 );
		this.staff6.addBox( -0.5F, 3F, -1.5F, 1, 2, 3);
		this.staff6.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staff14 = new ModelRenderer( this, 110, 14 );
		this.staff14.setTextureSize( 128, 64 );
		this.staff14.addBox( -1.5F, -2F, -13.5F, 2, 7, 2);
		this.staff14.setRotationPoint( -11F, -2.999998F, 3.999997F );
		this.staff16 = new ModelRenderer( this, 124, 14 );
		this.staff16.setTextureSize( 128, 64 );
		this.staff16.addBox( -1F, 11F, -13F, 1, 13, 1);
		this.staff16.setRotationPoint( -11F, -2.999998F, 3.999997F );
		this.staff13 = new ModelRenderer( this, 118, 14 );
		this.staff13.setTextureSize( 128, 64 );
		this.staff13.addBox( -1F, -8F, -13.5F, 1, 6, 2);
		this.staff13.setRotationPoint( -11F, -2.999998F, 3.999997F );
		this.staff5 = new ModelRenderer( this, 122, 5 );
		this.staff5.setTextureSize( 128, 64 );
		this.staff5.addBox( -0.5F, 3F, -1F, 1, 2, 2);
		this.staff5.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staffCrystal1 = new ModelRenderer( this, 112, 44 );
		this.staffCrystal1.setTextureSize( 128, 64 );
		this.staffCrystal1.addBox( -0.5F, -0.5F, -0.5F, 1, 1, 1);
		this.staffCrystal1.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staffCrystal3 = new ModelRenderer( this, 112, 44 );
		this.staffCrystal3.setTextureSize( 128, 64 );
		this.staffCrystal3.addBox( -0.5F, -0.5F, -0.5F, 1, 1, 1);
		this.staffCrystal3.setRotationPoint( -11.5F, -23F, -14.3F );
		this.staffCrystal2 = new ModelRenderer( this, 112, 44 );
		this.staffCrystal2.setTextureSize( 128, 64 );
		this.staffCrystal2.addBox( -0.5F, -0.5F, -0.5F, 1, 1, 1);
		this.staffCrystal2.setRotationPoint( -11.5F, -23F, -14.3F );
		this.rshoulder = new ModelRenderer( this, 50, 22 );
		this.rshoulder.setTextureSize( 128, 64 );
		this.rshoulder.addBox( 0F, -1F, -2F, 4, 2, 4);
		this.rshoulder.setRotationPoint( -3F, -14.53105F, 4.53373F );
		this.rarm3 = new ModelRenderer( this, 106, 0 );
		this.rarm3.setTextureSize( 128, 64 );
		this.rarm3.addBox( 1F, 1F, -1.5F, 1, 12, 1);
		this.rarm3.setRotationPoint( -3.996779F, -14.45085F, 4.53373F );
		this.rarm2 = new ModelRenderer( this, 106, 0 );
		this.rarm2.setTextureSize( 128, 64 );
		this.rarm2.addBox( 1F, 1F, 0.5F, 1, 12, 1);
		this.rarm2.setRotationPoint( -3.996779F, -14.45085F, 4.53373F );
		this.relbow = new ModelRenderer( this, 13, 13 );
		this.relbow.setTextureSize( 128, 64 );
		this.relbow.addBox( -1F, -1F, -1.5F, 2, 2, 3);
		this.relbow.setRotationPoint( -11.00435F, -2.617571F, 3.51349F );
		this.robe1 = new ModelRenderer( this, 4, 39 );
		this.robe1.setTextureSize( 128, 64 );
		this.robe1.addBox( -2.5F, -12F, 0F, 5, 24, 0);
		this.robe1.setRotationPoint( -5F, -3F, 0.5F );
		this.robe2 = new ModelRenderer( this, 16, 39 );
		this.robe2.setTextureSize( 128, 64 );
		this.robe2.addBox( -2.5F, -12F, 0F, 5, 24, 0);
		this.robe2.setRotationPoint( 5F, -3F, 0.5F );
		this.robe4 = new ModelRenderer( this, 28, 56 );
		this.robe4.setTextureSize( 128, 64 );
		this.robe4.addBox( -4F, -0.5F, -2.5F, 8, 1, 5);
		this.robe4.setRotationPoint( 7F, -16.5F, 4.5F );
		this.robe5 = new ModelRenderer( this, 27, 50 );
		this.robe5.setTextureSize( 128, 64 );
		this.robe5.addBox( -3F, -0.5F, -2F, 6, 1, 4);
		this.robe5.setRotationPoint( 6F, -16F, 1.5F );
		this.robe6 = new ModelRenderer( this, 26, 39 );
		this.robe6.setTextureSize( 128, 64 );
		this.robe6.addBox( -3F, -0.5F, -2.5F, 6, 1, 5);
		this.robe6.setRotationPoint( 6F, -16F, 8.5F );
		this.robe3 = new ModelRenderer( this, 74, 27 );
		this.robe3.setTextureSize( 128, 64 );
		this.robe3.addBox( -6.5F, -17.5F, 0F, 13, 35, 0);
		this.robe3.setRotationPoint( 0F, 2F, 9.2F );
		this.robe7 = new ModelRenderer( this, 1, 31 );
		this.robe7.setTextureSize( 128, 64 );
		this.robe7.addBox( -4F, -0.5F, -2.5F, 8, 1, 5);
		this.robe7.setRotationPoint( -7F, -16.5F, 4.5F );
		this.robe8 = new ModelRenderer( this, 27, 45 );
		this.robe8.setTextureSize( 128, 64 );
		this.robe8.addBox( -3F, -0.5F, -2F, 6, 1, 4);
		this.robe8.setRotationPoint( -6F, -16F, 1.5F );
		this.robe9 = new ModelRenderer( this, 26, 33 );
		this.robe9.setTextureSize( 128, 64 );
		this.robe9.addBox( -3F, -0.5F, -2.5F, 6, 1, 5);
		this.robe9.setRotationPoint( -6F, -16F, 8.5F );
	}

	@Override
	public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
	{
		this.rib1.rotateAngleX = 0F;
		this.rib1.rotateAngleY = 0F;
		this.rib1.rotateAngleZ = 0F;
		this.rib1.renderWithRotation(par7);

		this.rib32.rotateAngleX = 0F;
		this.rib32.rotateAngleY = 0F;
		this.rib32.rotateAngleZ = 0F;
		this.rib32.renderWithRotation(par7);

		this.rib15.rotateAngleX = 0F;
		this.rib15.rotateAngleY = 0F;
		this.rib15.rotateAngleZ = 0F;
		this.rib15.renderWithRotation(par7);

		this.rib22.rotateAngleX = 0F;
		this.rib22.rotateAngleY = 0F;
		this.rib22.rotateAngleZ = 0F;
		this.rib22.renderWithRotation(par7);

		this.rib3.rotateAngleX = 0F;
		this.rib3.rotateAngleY = 0F;
		this.rib3.rotateAngleZ = 0F;
		this.rib3.renderWithRotation(par7);

		this.rib2.rotateAngleX = 0F;
		this.rib2.rotateAngleY = 0F;
		this.rib2.rotateAngleZ = 0F;
		this.rib2.renderWithRotation(par7);

		this.rib23.rotateAngleX = 0F;
		this.rib23.rotateAngleY = 0F;
		this.rib23.rotateAngleZ = 0F;
		this.rib23.renderWithRotation(par7);

		this.rib24.rotateAngleX = 0F;
		this.rib24.rotateAngleY = 0F;
		this.rib24.rotateAngleZ = 0F;
		this.rib24.renderWithRotation(par7);

		this.rib19.rotateAngleX = 0F;
		this.rib19.rotateAngleY = 0F;
		this.rib19.rotateAngleZ = 0F;
		this.rib19.renderWithRotation(par7);

		this.rib20.rotateAngleX = 0F;
		this.rib20.rotateAngleY = 0F;
		this.rib20.rotateAngleZ = 0F;
		this.rib20.renderWithRotation(par7);

		this.rib21.rotateAngleX = 0F;
		this.rib21.rotateAngleY = 0F;
		this.rib21.rotateAngleZ = 0F;
		this.rib21.renderWithRotation(par7);

		this.rib16.rotateAngleX = 0F;
		this.rib16.rotateAngleY = 0F;
		this.rib16.rotateAngleZ = 0F;
		this.rib16.renderWithRotation(par7);

		this.rib18.rotateAngleX = 0F;
		this.rib18.rotateAngleY = 0F;
		this.rib18.rotateAngleZ = 0F;
		this.rib18.renderWithRotation(par7);

		this.rib17.rotateAngleX = 0F;
		this.rib17.rotateAngleY = 0F;
		this.rib17.rotateAngleZ = 0F;
		this.rib17.renderWithRotation(par7);

		this.rib13.rotateAngleX = 0F;
		this.rib13.rotateAngleY = 0F;
		this.rib13.rotateAngleZ = 0F;
		this.rib13.renderWithRotation(par7);

		this.rib14.rotateAngleX = 0F;
		this.rib14.rotateAngleY = 0F;
		this.rib14.rotateAngleZ = 0F;
		this.rib14.renderWithRotation(par7);

		this.rib6.rotateAngleX = 0F;
		this.rib6.rotateAngleY = 0F;
		this.rib6.rotateAngleZ = 0F;
		this.rib6.renderWithRotation(par7);

		this.rib35.rotateAngleX = 0F;
		this.rib35.rotateAngleY = 0F;
		this.rib35.rotateAngleZ = 0F;
		this.rib35.renderWithRotation(par7);

		this.rib34.rotateAngleX = 0F;
		this.rib34.rotateAngleY = 0F;
		this.rib34.rotateAngleZ = 0F;
		this.rib34.renderWithRotation(par7);

		this.rib31.rotateAngleX = 0F;
		this.rib31.rotateAngleY = 0F;
		this.rib31.rotateAngleZ = 0F;
		this.rib31.renderWithRotation(par7);

		this.rib33.rotateAngleX = 0F;
		this.rib33.rotateAngleY = 0F;
		this.rib33.rotateAngleZ = 0F;
		this.rib33.renderWithRotation(par7);

		this.rib36.rotateAngleX = 0F;
		this.rib36.rotateAngleY = 0F;
		this.rib36.rotateAngleZ = 0F;
		this.rib36.renderWithRotation(par7);

		this.rib9.rotateAngleX = 0F;
		this.rib9.rotateAngleY = 0F;
		this.rib9.rotateAngleZ = 0F;
		this.rib9.renderWithRotation(par7);

		this.rib5.rotateAngleX = 0F;
		this.rib5.rotateAngleY = 0F;
		this.rib5.rotateAngleZ = 0F;
		this.rib5.renderWithRotation(par7);

		this.rib4.rotateAngleX = 0F;
		this.rib4.rotateAngleY = 0F;
		this.rib4.rotateAngleZ = 0F;
		this.rib4.renderWithRotation(par7);

		this.rib8.rotateAngleX = 0F;
		this.rib8.rotateAngleY = 0F;
		this.rib8.rotateAngleZ = 0F;
		this.rib8.renderWithRotation(par7);

		this.rib7.rotateAngleX = 0F;
		this.rib7.rotateAngleY = 0F;
		this.rib7.rotateAngleZ = 0F;
		this.rib7.renderWithRotation(par7);

		this.rib12.rotateAngleX = 0F;
		this.rib12.rotateAngleY = 0F;
		this.rib12.rotateAngleZ = 0F;
		this.rib12.renderWithRotation(par7);

		this.rib11.rotateAngleX = 0F;
		this.rib11.rotateAngleY = 0F;
		this.rib11.rotateAngleZ = 0F;
		this.rib11.renderWithRotation(par7);

		this.rib10.rotateAngleX = 0F;
		this.rib10.rotateAngleY = 0F;
		this.rib10.rotateAngleZ = 0F;
		this.rib10.renderWithRotation(par7);

		this.rib27.rotateAngleX = 0F;
		this.rib27.rotateAngleY = 0F;
		this.rib27.rotateAngleZ = 0F;
		this.rib27.renderWithRotation(par7);

		this.rib25.rotateAngleX = 0F;
		this.rib25.rotateAngleY = 0F;
		this.rib25.rotateAngleZ = 0F;
		this.rib25.renderWithRotation(par7);

		this.rib26.rotateAngleX = 0F;
		this.rib26.rotateAngleY = 0F;
		this.rib26.rotateAngleZ = 0F;
		this.rib26.renderWithRotation(par7);

		this.rib30.rotateAngleX = 0F;
		this.rib30.rotateAngleY = 0F;
		this.rib30.rotateAngleZ = 0F;
		this.rib30.renderWithRotation(par7);

		this.rib29.rotateAngleX = 0F;
		this.rib29.rotateAngleY = 0F;
		this.rib29.rotateAngleZ = 0F;
		this.rib29.renderWithRotation(par7);

		this.rib28.rotateAngleX = 0F;
		this.rib28.rotateAngleY = 0F;
		this.rib28.rotateAngleZ = 0F;
		this.rib28.renderWithRotation(par7);

		this.sternum.rotateAngleX = 0F;
		this.sternum.rotateAngleY = 0F;
		this.sternum.rotateAngleZ = 0F;
		this.sternum.renderWithRotation(par7);

		this.crystal1.rotateAngleX = -8.323605E-09F;
		this.crystal1.rotateAngleY = -0.7853982F;
		this.crystal1.rotateAngleZ = -0.7853982F;
		this.crystal1.renderWithRotation(par7);

		this.crystal2.rotateAngleX = 0F;
		this.crystal2.rotateAngleY = 0F;
		this.crystal2.rotateAngleZ = 0F;
		this.crystal2.renderWithRotation(par7);

		this.crystal3.rotateAngleX = 0.7853982F;
		this.crystal3.rotateAngleY = -0.7853982F;
		this.crystal3.rotateAngleZ = -1.177136E-08F;
		this.crystal3.renderWithRotation(par7);

		this.head1.rotateAngleX = 0F;
		this.head1.rotateAngleY = 0F;
		this.head1.rotateAngleZ = 0F;
		this.head1.renderWithRotation(par7);

		this.head2.rotateAngleX = 0F;
		this.head2.rotateAngleY = 0F;
		this.head2.rotateAngleZ = 0F;
		this.head2.renderWithRotation(par7);

		this.head3.rotateAngleX = 0F;
		this.head3.rotateAngleY = 0F;
		this.head3.rotateAngleZ = 0F;
		this.head3.renderWithRotation(par7);

		this.head4.rotateAngleX = 0.7853982F;
		this.head4.rotateAngleY = 0F;
		this.head4.rotateAngleZ = 0F;
		this.head4.renderWithRotation(par7);

		this.head5.rotateAngleX = 0.7853984F;
		this.head5.rotateAngleY = 0F;
		this.head5.rotateAngleZ = 0F;
		this.head5.renderWithRotation(par7);

		this.head6.rotateAngleX = -1.889617E-08F;
		this.head6.rotateAngleY = 0F;
		this.head6.rotateAngleZ = 0F;
		this.head6.renderWithRotation(par7);

		this.head7.rotateAngleX = -1.889617E-08F;
		this.head7.rotateAngleY = 0F;
		this.head7.rotateAngleZ = 0F;
		this.head7.renderWithRotation(par7);

		this.head8.rotateAngleX = -1.889617E-08F;
		this.head8.rotateAngleY = 0F;
		this.head8.rotateAngleZ = 0F;
		this.head8.renderWithRotation(par7);

		this.head9.rotateAngleX = -1.889617E-08F;
		this.head9.rotateAngleY = 0F;
		this.head9.rotateAngleZ = 0F;
		this.head9.renderWithRotation(par7);

		this.spine1.rotateAngleX = 0F;
		this.spine1.rotateAngleY = 0F;
		this.spine1.rotateAngleZ = 0F;
		this.spine1.renderWithRotation(par7);

		this.spine2.rotateAngleX = -0.296706F;
		this.spine2.rotateAngleY = 0F;
		this.spine2.rotateAngleZ = 0F;
		this.spine2.renderWithRotation(par7);

		this.spine3.rotateAngleX = -0.7679448F;
		this.spine3.rotateAngleY = 0F;
		this.spine3.rotateAngleZ = 0F;
		this.spine3.renderWithRotation(par7);

		this.spine5.rotateAngleX = 0F;
		this.spine5.rotateAngleY = 0F;
		this.spine5.rotateAngleZ = 0F;
		this.spine5.renderWithRotation(par7);

		this.spine8.rotateAngleX = 0F;
		this.spine8.rotateAngleY = 0F;
		this.spine8.rotateAngleZ = 0F;
		this.spine8.renderWithRotation(par7);

		this.spine4.rotateAngleX = 0F;
		this.spine4.rotateAngleY = 0F;
		this.spine4.rotateAngleZ = 0F;
		this.spine4.renderWithRotation(par7);

		this.spine7.rotateAngleX = 0F;
		this.spine7.rotateAngleY = 0F;
		this.spine7.rotateAngleZ = 0F;
		this.spine7.renderWithRotation(par7);

		this.spine6.rotateAngleX = 0F;
		this.spine6.rotateAngleY = 0F;
		this.spine6.rotateAngleZ = 0F;
		this.spine6.renderWithRotation(par7);

		this.spine9.rotateAngleX = 0F;
		this.spine9.rotateAngleY = 0F;
		this.spine9.rotateAngleZ = 0F;
		this.spine9.renderWithRotation(par7);

		this.neck.rotateAngleX = -0.3316128F;
		this.neck.rotateAngleY = -3.141593F;
		this.neck.rotateAngleZ = -3.141593F;
		this.neck.renderWithRotation(par7);

		this.lshoulder.rotateAngleX = 0F;
		this.lshoulder.rotateAngleY = 0F;
		this.lshoulder.rotateAngleZ = 0.08033861F;
		this.lshoulder.renderWithRotation(par7);

		this.larm1.rotateAngleX = 0F;
		this.larm1.rotateAngleY = 0F;
		this.larm1.rotateAngleZ = -0.4083535F;
		this.larm1.renderWithRotation(par7);

		this.larm2.rotateAngleX = 0F;
		this.larm2.rotateAngleY = 0F;
		this.larm2.rotateAngleZ = -0.4083536F;
		this.larm2.renderWithRotation(par7);

		this.elbow.rotateAngleX = 0F;
		this.elbow.rotateAngleY = 0F;
		this.elbow.rotateAngleZ = -0.3490659F;
		this.elbow.renderWithRotation(par7);

		this.larm3.rotateAngleX = 7.413874E-08F;
		this.larm3.rotateAngleY = -3.141592F;
		this.larm3.rotateAngleZ = 0.03490664F;
		this.larm3.renderWithRotation(par7);

		this.larm4.rotateAngleX = 7.413874E-08F;
		this.larm4.rotateAngleY = -3.141592F;
		this.larm4.rotateAngleZ = 0.03490666F;
		this.larm4.renderWithRotation(par7);

		this.larm5.rotateAngleX = 7.413874E-08F;
		this.larm5.rotateAngleY = -3.141592F;
		this.larm5.rotateAngleZ = -0.8203049F;
		this.larm5.renderWithRotation(par7);

		this.larm6.rotateAngleX = 7.413874E-08F;
		this.larm6.rotateAngleY = -3.141592F;
		this.larm6.rotateAngleZ = -0.366519F;
		this.larm6.renderWithRotation(par7);

		this.larm7.rotateAngleX = 7.413873E-08F;
		this.larm7.rotateAngleY = -3.141592F;
		this.larm7.rotateAngleZ = -0.6457719F;
		this.larm7.renderWithRotation(par7);

		this.larm8.rotateAngleX = 7.413874E-08F;
		this.larm8.rotateAngleY = -3.141592F;
		this.larm8.rotateAngleZ = -0.366519F;
		this.larm8.renderWithRotation(par7);

		this.larm9.rotateAngleX = 7.413873E-08F;
		this.larm9.rotateAngleY = -3.141592F;
		this.larm9.rotateAngleZ = -0.6457719F;
		this.larm9.renderWithRotation(par7);

		this.larm10.rotateAngleX = 7.413873E-08F;
		this.larm10.rotateAngleY = -3.141592F;
		this.larm10.rotateAngleZ = -0.6457719F;
		this.larm10.renderWithRotation(par7);

		this.larm11.rotateAngleX = 7.413873E-08F;
		this.larm11.rotateAngleY = -3.141592F;
		this.larm11.rotateAngleZ = -0.3665189F;
		this.larm11.renderWithRotation(par7);

		this.rarm1.rotateAngleX = -1.308997F;
		this.rarm1.rotateAngleY = -8.22972E-09F;
		this.rarm1.rotateAngleZ = -4.377172E-08F;
		this.rarm1.renderWithRotation(par7);

		this.rhand.rotateAngleX = -1.308997F;
		this.rhand.rotateAngleY = -8.22972E-09F;
		this.rhand.rotateAngleZ = -4.377172E-08F;
		this.rhand.renderWithRotation(par7);

		this.staff1.rotateAngleX = -1.256637F;
		this.staff1.rotateAngleY = -3.42539E-16F;
		this.staff1.rotateAngleZ = -2.049728E-15F;
		this.staff1.renderWithRotation(par7);

		this.staff2.rotateAngleX = -1.012291F;
		this.staff2.rotateAngleY = -1.278293E-15F;
		this.staff2.rotateAngleZ = -1.195278E-15F;
		this.staff2.renderWithRotation(par7);

		this.staff3.rotateAngleX = -0.7679449F;
		this.staff3.rotateAngleY = -1.680278E-15F;
		this.staff3.rotateAngleZ = -8.805304E-16F;
		this.staff3.renderWithRotation(par7);

		this.staff4.rotateAngleX = -0.5235988F;
		this.staff4.rotateAngleY = -1.926252E-15F;
		this.staff4.rotateAngleZ = -7.31388E-16F;
		this.staff4.renderWithRotation(par7);

		this.staff7.rotateAngleX = 0.7504914F;
		this.staff7.rotateAngleY = -2.882602E-15F;
		this.staff7.rotateAngleZ = -8.660658E-16F;
		this.staff7.renderWithRotation(par7);

		this.staff.rotateAngleX = 0.2617992F;
		this.staff.rotateAngleY = -2.461665E-15F;
		this.staff.rotateAngleZ = -6.557445E-16F;
		this.staff.renderWithRotation(par7);

		this.staff11.rotateAngleX = 1.361356F;
		this.staff11.rotateAngleY = -3.141593F;
		this.staff11.rotateAngleZ = -3.141593F;
		this.staff11.renderWithRotation(par7);

		this.staff12.rotateAngleX = 0.2617992F;
		this.staff12.rotateAngleY = -2.461665E-15F;
		this.staff12.rotateAngleZ = -6.557445E-16F;
		this.staff12.renderWithRotation(par7);

		this.staff10.rotateAngleX = 1.53589F;
		this.staff10.rotateAngleY = -2.043021E-14F;
		this.staff10.rotateAngleZ = -1.814932E-14F;
		this.staff10.renderWithRotation(par7);

		this.staff9.rotateAngleX = 1.291543F;
		this.staff9.rotateAngleY = -4.500876E-15F;
		this.staff9.rotateAngleZ = -2.297949E-15F;
		this.staff9.renderWithRotation(par7);

		this.staff8.rotateAngleX = 1.047198F;
		this.staff8.rotateAngleY = -3.389028E-15F;
		this.staff8.rotateAngleZ = -1.266802E-15F;
		this.staff8.renderWithRotation(par7);

		this.staff15.rotateAngleX = 0.2617992F;
		this.staff15.rotateAngleY = -2.461665E-15F;
		this.staff15.rotateAngleZ = -6.557445E-16F;
		this.staff15.renderWithRotation(par7);

		this.staff6.rotateAngleX = 0.2617992F;
		this.staff6.rotateAngleY = -2.461665E-15F;
		this.staff6.rotateAngleZ = -6.557445E-16F;
		this.staff6.renderWithRotation(par7);

		this.staff14.rotateAngleX = 0.2617992F;
		this.staff14.rotateAngleY = -2.461665E-15F;
		this.staff14.rotateAngleZ = -6.557445E-16F;
		this.staff14.renderWithRotation(par7);

		this.staff16.rotateAngleX = 0.2617992F;
		this.staff16.rotateAngleY = -2.461665E-15F;
		this.staff16.rotateAngleZ = -6.557445E-16F;
		this.staff16.renderWithRotation(par7);

		this.staff13.rotateAngleX = 0.2617992F;
		this.staff13.rotateAngleY = -2.461665E-15F;
		this.staff13.rotateAngleZ = -6.557445E-16F;
		this.staff13.renderWithRotation(par7);

		this.staff5.rotateAngleX = -0.2268929F;
		this.staff5.rotateAngleY = -2.145714E-15F;
		this.staff5.rotateAngleZ = -6.500616E-16F;
		this.staff5.renderWithRotation(par7);

		this.staffCrystal1.rotateAngleX = -0.7853979F;
		this.staffCrystal1.rotateAngleY = -1.658545E-15F;
		this.staffCrystal1.rotateAngleZ = -8.957633E-16F;
		this.staffCrystal1.renderWithRotation(par7);

		this.staffCrystal3.rotateAngleX = -5.925837E-08F;
		this.staffCrystal3.rotateAngleY = -9.883016E-09F;
		this.staffCrystal3.rotateAngleZ = 0.7853982F;
		this.staffCrystal3.renderWithRotation(par7);

		this.staffCrystal2.rotateAngleX = -4.889034E-08F;
		this.staffCrystal2.rotateAngleY = 0.7853982F;
		this.staffCrystal2.rotateAngleZ = -3.491365E-08F;
		this.staffCrystal2.renderWithRotation(par7);

		this.rshoulder.rotateAngleX = 0.09028283F;
		this.rshoulder.rotateAngleY = -3.134309F;
		this.rshoulder.rotateAngleZ = 0.08061417F;
		this.rshoulder.renderWithRotation(par7);

		this.rarm3.rotateAngleX = 0.09028283F;
		this.rarm3.rotateAngleY = -3.134309F;
		this.rarm3.rotateAngleZ = -0.4080779F;
		this.rarm3.renderWithRotation(par7);

		this.rarm2.rotateAngleX = 0.09028291F;
		this.rarm2.rotateAngleY = -3.134309F;
		this.rarm2.rotateAngleZ = -0.408078F;
		this.rarm2.renderWithRotation(par7);

		this.relbow.rotateAngleX = 0.09028283F;
		this.relbow.rotateAngleY = -3.134309F;
		this.relbow.rotateAngleZ = -0.3487903F;
		this.relbow.renderWithRotation(par7);

		this.robe1.rotateAngleX = 0F;
		this.robe1.rotateAngleY = 0F;
		this.robe1.rotateAngleZ = 0F;
		this.robe1.renderWithRotation(par7);

		this.robe2.rotateAngleX = 0F;
		this.robe2.rotateAngleY = 0F;
		this.robe2.rotateAngleZ = 0F;
		this.robe2.renderWithRotation(par7);

		this.robe4.rotateAngleX = 0F;
		this.robe4.rotateAngleY = 0F;
		this.robe4.rotateAngleZ = -0.1745329F;
		this.robe4.renderWithRotation(par7);

		this.robe5.rotateAngleX = 0.2577309F;
		this.robe5.rotateAngleY = -0.04649536F;
		this.robe5.rotateAngleZ = -0.180559F;
		this.robe5.renderWithRotation(par7);

		this.robe6.rotateAngleX = -0.255709F;
		this.robe6.rotateAngleY = -0.04267286F;
		this.robe6.rotateAngleZ = -0.1797679F;
		this.robe6.renderWithRotation(par7);

		this.robe3.rotateAngleX = 0F;
		this.robe3.rotateAngleY = 0F;
		this.robe3.rotateAngleZ = 0F;
		this.robe3.renderWithRotation(par7);

		this.robe7.rotateAngleX = 0F;
		this.robe7.rotateAngleY = 0F;
		this.robe7.rotateAngleZ = 0.1745329F;
		this.robe7.renderWithRotation(par7);

		this.robe8.rotateAngleX = 0.2617994F;
		this.robe8.rotateAngleY = 1.703252E-09F;
		this.robe8.rotateAngleZ = 0.1745329F;
		this.robe8.renderWithRotation(par7);

		this.robe9.rotateAngleX = -0.2443461F;
		this.robe9.rotateAngleY = -3.968324E-11F;
		this.robe9.rotateAngleZ = 0.08726646F;
		this.robe9.renderWithRotation(par7);

	}

	private void setRotation(final ModelRenderer model, final float x, final float y, final float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5, final Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		//TODO
	}

}
