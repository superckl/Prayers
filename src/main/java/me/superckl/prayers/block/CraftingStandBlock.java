package me.superckl.prayers.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class CraftingStandBlock extends FourWayShapedBlock{

	public CraftingStandBlock() {
		super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL), true);
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		return super.getStateForPlacement(context);
	}
	
	protected VoxelShape[] getShapes() {
		final VoxelShape base = Block.makeCuboidShape(7, 0, 7, 9, 0.5, 9);
		final VoxelShape connector_1 = Block.makeCuboidShape(7, 0.5, 7, 7.5, 1, 7.5);
		final VoxelShape connector_2 = Block.makeCuboidShape(7, 0.5, 8.5, 7.5, 1, 9);
		final VoxelShape connector_3 = Block.makeCuboidShape(8.5, 0.5, 8.5, 9, 1, 9);
		final VoxelShape connector_4 = Block.makeCuboidShape(8.5, 0.5, 7, 9, 1, 7.5);
		final VoxelShape side_top_n = Block.makeCuboidShape(7.5, 1, 9, 8.5, 1.5, 9.5);
		final VoxelShape side_top_s = Block.makeCuboidShape(7.5, 1, 6.5, 8.5, 1.5, 7);
		final VoxelShape side_top_e = Block.makeCuboidShape(9, 1, 7.5, 9.5, 1.5, 8.5);
		final VoxelShape side_top_w = Block.makeCuboidShape(6.5, 1, 7.5, 7, 1.5, 8.5);
		final VoxelShape side_n = Block.makeCuboidShape(7, 0.5, 9, 9, 1, 9.5);
		final VoxelShape side_s = Block.makeCuboidShape(7, 0.5, 6.5, 9, 1, 7);
		final VoxelShape side_e = Block.makeCuboidShape(9, 0.5, 7, 9.5, 1, 9);
		final VoxelShape side_w = Block.makeCuboidShape(6.5, 0.5, 7, 7, 1, 9);

		final VoxelShape stand = VoxelShapes.or(base, connector_1, connector_2, connector_3, connector_4, side_n,
				side_s, side_e, side_w, side_top_n, side_top_s, side_top_e, side_top_w);

		final VoxelShape stand_n = stand.withOffset(0, 0, -5.5/16);
		final VoxelShape stand_s = stand.withOffset(0, 0, 5.5/16);
		final VoxelShape stand_e = stand.withOffset(5.5/16, 0, 0);
		final VoxelShape stand_w = stand.withOffset(-5.5/16, 0, 0);

		final int north = getMask(Direction.NORTH);
		final int east = getMask(Direction.EAST);
		final int south = getMask(Direction.SOUTH);
		final int west = getMask(Direction.WEST);

		final VoxelShape[] shapes = new VoxelShape[16];
		shapes[0] = stand;//only center

		//All 8 including north (9)
		shapes[north] = VoxelShapes.or(stand, stand_n);

		shapes[north | east] = VoxelShapes.or(shapes[north], stand_e);
		shapes[north | south] = VoxelShapes.or(shapes[north], stand_s);
		shapes[north | west] = VoxelShapes.or(shapes[north], stand_w);

		shapes[north | east | south] = VoxelShapes.or(shapes[north | east], stand_s);
		shapes[north | east | west] = VoxelShapes.or(shapes[north], stand_w);
		shapes[north | south | west] = VoxelShapes.or(shapes[north | south], stand_w);

		shapes[north | east | south | west] = VoxelShapes.or(shapes[north | east | south], stand_w);

		//Remaining 4 including east (13)
		shapes[east] = VoxelShapes.or(stand, stand_e);

		shapes[east | south] = VoxelShapes.or(shapes[east], stand_s);
		shapes[east | west] = VoxelShapes.or(shapes[east], stand_w);

		shapes[east | west | south] = VoxelShapes.or(shapes[east | west], stand_s);

		//Remaining 2 including south (15)
		shapes[south] = VoxelShapes.or(stand, stand_s);

		shapes[south | west] = VoxelShapes.or(shapes[south], stand_w);

		//Remaining 1 including west (16)
		shapes[west] = VoxelShapes.or(stand, stand_w);

		return shapes;
	}

	@Override
	protected boolean canConnect(BlockState state) {
		return true;
	}

}
