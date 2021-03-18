package me.superckl.prayers.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public abstract class ShapedBlock extends Block implements IWaterLoggable{

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private final VoxelShape[] shapes;
	private final boolean waterLoggable;

	public ShapedBlock(final Properties properties, final boolean waterLoggable) {
		super(properties);
		this.shapes = this.getShapes();
		this.waterLoggable = waterLoggable;
		this.registerDefaultState(this.defaultBlockState().setValue(ShapedBlock.WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shapes[this.getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shapes[this.getIndex(state)];
	}

	@Override
	public VoxelShape getInteractionShape(final BlockState state, final IBlockReader reader, final BlockPos pos) {
		return this.shapes[this.getIndex(state)];
	}

	@Override
	public VoxelShape getVisualShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shapes[this.getIndex(state)];
	}

	@Override
	public FluidState getFluidState(final BlockState state) {
		return state.getValue(ShapedBlock.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public boolean canPlaceLiquid(final IBlockReader worldIn, final BlockPos pos, final BlockState state, final Fluid fluidIn) {
		if(this.waterLoggable)
			return IWaterLoggable.super.canPlaceLiquid(worldIn, pos, state, fluidIn);
		else
			return false;
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		final BlockState state =  super.getStateForPlacement(context);
		if(this.waterLoggable)
			return state.setValue(ShapedBlock.WATERLOGGED, fluidState.getType() == Fluids.WATER);
		else
			return state.setValue(ShapedBlock.WATERLOGGED, false);
	}



	@Override
	public BlockState updateShape(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
		if (state.getValue(ShapedBlock.WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		return state;
	}

	@Override
	protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ShapedBlock.WATERLOGGED);
	}

	protected abstract VoxelShape[] getShapes();

	protected abstract int getIndex(final BlockState state);

}
