package me.superckl.prayers.common.altar.multi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

@Getter
@Setter
@ToString
public class BlockRequirement{

	private Material material;
	private Block block;
	private Class<? extends Block> clazz;
	private boolean assignBlock;

	public BlockRequirement(final Material material){
		this.material = material;
	}

	public BlockRequirement(final Material material, final boolean assignBlock){
		this.material = material;
		this.assignBlock = assignBlock;
	}

	public BlockRequirement(final Material material, final Class<? extends Block> clazz, final boolean assignBlock){
		this.material = material;
		this.clazz = clazz;
		this.assignBlock = assignBlock;
	}

	public BlockRequirement(final Material material, final Class<? extends Block> clazz){
		this.material = material;
		this.clazz = clazz;
	}

	public BlockRequirement(final Block block){
		this.block = block;
	}

	public boolean isSatisfied(final Block block){
		if(this.block != null)
			return this.block == block;
		final boolean cClear = this.clazz == null ? true:this.clazz.isAssignableFrom(block.getClass());
		final boolean mClear = this.material == null ? true:this.material == block.getMaterial();
		if (cClear && mClear){
			if(this.assignBlock)
				this.block = block;
			return true;
		}
		return false;
	}

}
