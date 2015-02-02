package me.superckl.prayers.common.gui;

import me.superckl.prayers.client.gui.GuiAncientTome;
import me.superckl.prayers.client.gui.GuiContainerPrayers;
import me.superckl.prayers.common.container.ContainerPrayers;
import me.superckl.prayers.common.reference.RenderData.GUIIDs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch(ID){
		case GUIIDs.PRAYERS:
		{
			return new ContainerPrayers(player.inventory);
		}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch(ID){
		case GUIIDs.PRAYERS:
		{
			return new GuiContainerPrayers(player.inventory);
		}
		case GUIIDs.ANCIENT_TOME:
		{
			return new GuiAncientTome(player);
		}
		}
		return null;
	}

}
