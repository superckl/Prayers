package me.superckl.prayercraft.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world,
			final int x, final int y, final int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world,
			final int x, final int y, final int z) {
		return null;
	}

}
