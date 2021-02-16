package me.superckl.prayers.block;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public abstract class FourWayShapedBlock extends ShapedBlock{

	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter(facingProperty -> facingProperty.getKey().getAxis().isHorizontal()).collect(Util.toMapCollector());

	private final Object2IntMap<BlockState> statePaletteMap = new Object2IntOpenHashMap<>();

	public FourWayShapedBlock(final Properties properties, final boolean waterLoggable) {
		super(properties, waterLoggable);
		this.setDefaultState(this.getDefaultState().with(FourWayShapedBlock.NORTH, false)
				.with(FourWayShapedBlock.EAST, false).with(FourWayShapedBlock.SOUTH, false).with(FourWayShapedBlock.WEST, false));
	}

	@Override
	public BlockState updatePostPlacement(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
		final BlockState superState = super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ?
				superState.with(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(facing),
						this.canConnect(facingState))
				: state;
	}

	@Override
	public BlockState rotate(final BlockState state, final Rotation rot) {
		switch(rot) {
		case CLOCKWISE_180:
			return state.with(FourWayShapedBlock.NORTH, state.get(FourWayShapedBlock.SOUTH)).with(FourWayShapedBlock.EAST, state.get(FourWayShapedBlock.WEST)).with(FourWayShapedBlock.SOUTH, state.get(FourWayShapedBlock.NORTH)).with(FourWayShapedBlock.WEST, state.get(FourWayShapedBlock.EAST));
		case COUNTERCLOCKWISE_90:
			return state.with(FourWayShapedBlock.NORTH, state.get(FourWayShapedBlock.EAST)).with(FourWayShapedBlock.EAST, state.get(FourWayShapedBlock.SOUTH)).with(FourWayShapedBlock.SOUTH, state.get(FourWayShapedBlock.WEST)).with(FourWayShapedBlock.WEST, state.get(FourWayShapedBlock.NORTH));
		case CLOCKWISE_90:
			return state.with(FourWayShapedBlock.NORTH, state.get(FourWayShapedBlock.WEST)).with(FourWayShapedBlock.EAST, state.get(FourWayShapedBlock.NORTH)).with(FourWayShapedBlock.SOUTH, state.get(FourWayShapedBlock.EAST)).with(FourWayShapedBlock.WEST, state.get(FourWayShapedBlock.SOUTH));
		default:
			return state;
		}
	}

	@Override
	public BlockState mirror(final BlockState state, final Mirror mirror) {
		switch(mirror) {
		case LEFT_RIGHT:
			return state.with(FourWayShapedBlock.NORTH, state.get(FourWayShapedBlock.SOUTH)).with(FourWayShapedBlock.SOUTH, state.get(FourWayShapedBlock.NORTH));
		case FRONT_BACK:
			return state.with(FourWayShapedBlock.EAST, state.get(FourWayShapedBlock.WEST)).with(FourWayShapedBlock.WEST, state.get(FourWayShapedBlock.EAST));
		default:
			return state.mirror(mirror);
		}
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		final IBlockReader blockReader = context.getWorld();
		final BlockPos blockPos = context.getPos();
		final BlockPos blockNorth = blockPos.north();
		final BlockPos blockEast = blockPos.east();
		final BlockPos blockSouth = blockPos.south();
		final BlockPos blockWest = blockPos.west();
		final BlockState blockStateNorth = blockReader.getBlockState(blockNorth);
		final BlockState blockStateEast = blockReader.getBlockState(blockEast);
		final BlockState blockStateSouth = blockReader.getBlockState(blockSouth);
		final BlockState blockStateWest = blockReader.getBlockState(blockWest);
		return super.getStateForPlacement(context)
				.with(AltarBlock.NORTH, this.canConnect(blockStateNorth))
				.with(AltarBlock.EAST, this.canConnect(blockStateEast))
				.with(AltarBlock.SOUTH, this.canConnect(blockStateSouth))
				.with(AltarBlock.WEST, this.canConnect(blockStateWest));
	}

	@Override
	protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FourWayShapedBlock.NORTH, FourWayShapedBlock.EAST, FourWayShapedBlock.WEST, FourWayShapedBlock.SOUTH);
		super.fillStateContainer(builder);
	}

	public static int getMask(final Direction facing) {
		return 1 << facing.getHorizontalIndex();
	}

	@Override
	protected int getIndex(final BlockState state) {
		return this.statePaletteMap.computeIntIfAbsent(state, stateIn -> {
			int i = 0;
			if (stateIn.get(FourWayShapedBlock.NORTH))
				i |= FourWayShapedBlock.getMask(Direction.NORTH);

			if (stateIn.get(FourWayShapedBlock.EAST))
				i |= FourWayShapedBlock.getMask(Direction.EAST);

			if (stateIn.get(FourWayShapedBlock.SOUTH))
				i |= FourWayShapedBlock.getMask(Direction.SOUTH);

			if (stateIn.get(FourWayShapedBlock.WEST))
				i |= FourWayShapedBlock.getMask(Direction.WEST);

			return i;
		});
	}

	protected abstract boolean canConnect(BlockState state);

}
