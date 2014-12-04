package me.superckl.prayercraft.common.block;

import me.superckl.prayercraft.common.reference.ModData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockPrayerCraft extends Block{

	public BlockPrayerCraft(final Material material) {
		super(material);
	}

	@Override
	public String getUnlocalizedName(){
		return String.format("tile.%s%s%s", ModData.MOD_ID.toLowerCase(), ":", this.getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	private String getUnwrappedUnlocalizedName(final String unlocalizedName){
		return unlocalizedName.substring(unlocalizedName.indexOf(".")+1);
	}

	public String getUnlocalizedName(final int meta){
		return String.format("tile.%s%s%s", ModData.MOD_ID.toLowerCase(), ":", this.getUnwrappedUnlocalizedName(super.getUnlocalizedName(), meta));
	}

	private String getUnwrappedUnlocalizedName(final String unlocalizedName, final int meta){
		return unlocalizedName.substring(unlocalizedName.indexOf(".")+1)+":"+meta;
	}

}
