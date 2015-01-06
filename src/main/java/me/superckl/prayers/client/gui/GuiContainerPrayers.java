package me.superckl.prayers.client.gui;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayers.client.gui.button.ButtonPrayer;
import me.superckl.prayers.common.container.ContainerPrayers;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.RenderData;
import me.superckl.prayers.common.utility.PrayerHelper;
import me.superckl.prayers.network.MessageDisablePrayer;
import me.superckl.prayers.network.MessageEnablePrayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tconstruct.client.tabs.TabRegistry;

public class GuiContainerPrayers extends GuiContainer{

	private float xSize_lo_2;
	private float ySize_lo_2;

	public GuiContainerPrayers(final InventoryPlayer invPlayer) {
		super(new ContainerPrayers(invPlayer));
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;

		final int cornerX = this.guiLeft;
		final int cornerY = this.guiTop;

		TabRegistry.updateTabValues(cornerX, cornerY, InventoryTabPrayers.class);
		TabRegistry.addTabsToList(this.buttonList);

		final PrayerExtendedProperties prop = (PrayerExtendedProperties) this.mc.thePlayer.getExtendedProperties("prayer");
		final EnumPrayers[] prayers = EnumPrayers.values();
		final int width = 5;
		final int excess = 94-(16*width)-(2*(width-1));
		final int startX = 79+(excess/2);
		int y = 10;
		int x = startX;
		int j = 0;
		for(int i = 0; i < prayers.length; i++){
			this.buttonList.add(new ButtonPrayer(i, x+this.guiLeft, y+this.guiTop, prayers[i]));
			x += 18;
			j++;
			if(j == width){
				j = 0;
				y += 18;
				x = startX;
			}
		}
	}

	@Override
	public void drawScreen(final int par1, final int par2, final float par3)
	{
		super.drawScreen(par1, par2, par3);
		this.xSize_lo_2 = par1;
		this.ySize_lo_2 = par2;
	}



	@Override
	protected void actionPerformed(final GuiButton button) {
		if(button instanceof ButtonPrayer){
			final ButtonPrayer pButton = (ButtonPrayer) button;
			final EnumPrayers prayer = pButton.getPrayer();
			final EntityPlayer player = ((ContainerPrayers)this.inventorySlots).getInvPlayer().player;
			final List<EnumPrayers> list = PrayerHelper.getActivePrayers(player);
			if(list.contains(prayer)){
				list.remove(prayer);
				final MessageDisablePrayer message = new MessageDisablePrayer();
				message.setPrayer(prayer);
				ModData.PRAYER_UPDATE_CHANNEL.sendToServer(message);
			}else{
				final PrayerExtendedProperties prop = (PrayerExtendedProperties) this.mc.thePlayer.getExtendedProperties("prayer");
				if(prop.getPrayerPoints() <= 0F)
					return;
				list.add(prayer);
				if(PrayerHelper.hasConflictions(list)){
					list.remove(prayer);
					return;
				}
				final MessageEnablePrayer message = new MessageEnablePrayer();
				message.setPrayer(prayer);
				ModData.PRAYER_UPDATE_CHANNEL.sendToServer(message);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int p_146979_1_,
			final int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		for(final Object obj:this.buttonList){
			if((obj instanceof ButtonPrayer) == false)
				continue;
			final ButtonPrayer button = (ButtonPrayer) obj;
			final int i = (Mouse.getEventX() * this.width) / this.mc.displayWidth;
			final int j = this.height - ((Mouse.getEventY() * this.height) / this.mc.displayHeight) - 1;
			if(button.mousePressed(this.mc, i, j)){
				final List<String> list = new ArrayList<String>(button.getPrayer().getDescription());
				list.add(0, button.getPrayer().getDisplayName());
				this.drawHoveringText(list, i-this.guiLeft, j-this.guiTop, this.fontRendererObj);
				break;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float p_146976_1_,
			final int p_146976_2_, final int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(RenderData.PRAYERS_GUI);
		final int k = this.guiLeft;
		final int l = this.guiTop;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		//drawPlayerOnGui(this.mc, k + 51, l + 75, 30, k + 51 - this.xSize_lo_2, l + 75 - 50 - this.ySize_lo_2);
		GuiContainerPrayers.func_147046_a(k + 51, l + 75, 30, (k + 51) - this.xSize_lo_2, (l + 75) - 50 - this.ySize_lo_2, this.mc.thePlayer);
	}

	/*public static void drawPlayerOnGui(Minecraft par0Minecraft, int par1, int par2, int par3, float par4, float par5)
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(par1, par2, 50.0F);
        GL11.glScalef(-par3, par3, par3);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = par0Minecraft.thePlayer.renderYawOffset;
        float f3 = par0Minecraft.thePlayer.rotationYaw;
        float f4 = par0Minecraft.thePlayer.rotationPitch;
        float f5 = par0Minecraft.thePlayer.rotationYawHead;
        par4 -= 19;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        // GL11.glRotatef(-((float) Math.atan(par5 / 40.0F)) * 20.0F, 1.0F,
        // 0.0F, 0.0F);
        //par0Minecraft.thePlayer.renderYawOffset = rotation;
        par0Minecraft.thePlayer.rotationYaw = (float) Math.atan(par4 / 40.0F) * 40.0F;
        //par0Minecraft.thePlayer.rotationYaw = rotation;
        par0Minecraft.thePlayer.rotationYawHead = par0Minecraft.thePlayer.rotationYaw;
        GL11.glTranslatef(0.0F, par0Minecraft.thePlayer.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(par0Minecraft.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        par0Minecraft.thePlayer.renderYawOffset = f2;
        par0Minecraft.thePlayer.rotationYaw = f3;
        par0Minecraft.thePlayer.rotationPitch = f4;
        par0Minecraft.thePlayer.rotationYawHead = f5;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }*/

	public static void func_147046_a(final int p_147046_0_, final int p_147046_1_, final int p_147046_2_, final float p_147046_3_, final float p_147046_4_, final EntityLivingBase p_147046_5_)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(p_147046_0_, p_147046_1_, 50.0F);
		GL11.glScalef((-p_147046_2_), p_147046_2_, p_147046_2_);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		final float f2 = p_147046_5_.renderYawOffset;
		final float f3 = p_147046_5_.rotationYaw;
		final float f4 = p_147046_5_.rotationPitch;
		final float f5 = p_147046_5_.prevRotationYawHead;
		final float f6 = p_147046_5_.rotationYawHead;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan(p_147046_4_ / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		p_147046_5_.renderYawOffset = (float)Math.atan(p_147046_3_ / 40.0F) * 20.0F;
		p_147046_5_.rotationYaw = (float)Math.atan(p_147046_3_ / 40.0F) * 40.0F;
		p_147046_5_.rotationPitch = -((float)Math.atan(p_147046_4_ / 40.0F)) * 20.0F;
		p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
		p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
		GL11.glTranslatef(0.0F, p_147046_5_.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		p_147046_5_.renderYawOffset = f2;
		p_147046_5_.rotationYaw = f3;
		p_147046_5_.rotationPitch = f4;
		p_147046_5_.prevRotationYawHead = f5;
		p_147046_5_.rotationYawHead = f6;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

}
