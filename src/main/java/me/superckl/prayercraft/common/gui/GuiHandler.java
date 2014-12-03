package me.superckl.prayercraft.common.gui;

import me.superckl.prayercraft.client.gui.GuiContainerPrayers;
import me.superckl.prayercraft.common.container.ContainerPrayers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch(ID){
		case 0:
		{
			return new ContainerPrayers(player.inventory);
		}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch(ID){
		case 0:
		{
			return new GuiContainerPrayers(player.inventory);
		}
		}
		return null;
	}

}
