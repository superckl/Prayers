package me.superckl.prayers.client;

import java.awt.Color;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.SoulOrbItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class SoulOrbItemColor implements IItemColor{

	@Override
	public int getColor(final ItemStack stack, final int layer) {
		if(layer != 0)
			return -1;
		final int kills = ModItems.SOUL_ORB.get().getStoredKills(stack).size();
		final float percentage = (float) kills/SoulOrbItem.REQ_MOBS.size();

		final int r0 = Color.LIGHT_GRAY.getRed();
		final int b0 = Color.LIGHT_GRAY.getBlue();
		final int g0 = Color.LIGHT_GRAY.getGreen();

		final int r1 = 0x32;
		final int b1 = 0x68;
		final int g1 = 0xd3;

		final int r = MathHelper.floor(r0+(r1-r0)*percentage);
		final int b = MathHelper.floor(b0+(b1-b0)*percentage);
		final int g = MathHelper.floor(g0+(g1-g0)*percentage);

		return r << 16 | g << 8 | b;
	}

}
