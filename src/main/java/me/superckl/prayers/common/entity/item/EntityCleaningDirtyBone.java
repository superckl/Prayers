package me.superckl.prayers.common.entity.item;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCleaningDirtyBone extends EntityItem{

	@Getter
	@Setter
	private int progress = 0;

	public EntityCleaningDirtyBone(final World world, final double x, final double y, final double z, final ItemStack stack) {
		super(world, x, y, z, stack);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if((this.getEntityItem().getItem() != ModItems.basicBone) || (this.getEntityItem().getItemDamage() != 2)){
			this.progress = 0;
			return;
		}
		final int x = MathHelper.floor_double( this.posX );
		final int y = MathHelper.floor_double( this.posY );
		final int z = MathHelper.floor_double( this.posZ );

		if(this.worldObj.getBlock(x, y, z).getMaterial().isLiquid()){
			this.progress++;
			if((this.progress % 50) == 0)
				this.getEntityItem().getTagCompound().setInteger("progress", this.progress);
		}
		LogHelper.info(this.progress);
		if(this.progress == 1200){
			this.setEntityItemStack(new ItemStack(ModItems.basicBone, this.getEntityItem().stackSize, 3));
			LogHelper.info("Setting item");
		}
	}



}
