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
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(facingProperty -> facingProperty.getKey().getAxis().isHorizontal()).collect(Util.toMap());

	private final Object2IntMap<BlockState> statePaletteMap = new Object2IntOpenHashMap<>();

	public FourWayShapedBlock(final Properties properties, final boolean waterLoggable) {
		super(properties, waterLoggable);
		this.registerDefaultState(this.defaultBlockState().setValue(FourWayShapedBlock.NORTH, false)
				.setValue(FourWayShapedBlock.EAST, false).setValue(FourWayShapedBlock.SOUTH, false).setValue(FourWayShapedBlock.WEST, false));
	}

	@Override
	public BlockState updateShape(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
		final BlockState superState = super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ?
				superState.setValue(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(facing),
						this.canConnect(facingState))
				: state;
	}

	@Override
	public BlockState rotate(final BlockState state, final Rotation rot) {
		switch(rot) {
		case CLOCKWISE_180:
			return state.setValue(FourWayShapedBlock.NORTH, state.getValue(FourWayShapedBlock.SOUTH)).setValue(FourWayShapedBlock.EAST, state.getValue(FourWayShapedBlock.WEST)).setValue(FourWayShapedBlock.SOUTH, state.getValue(FourWayShapedBlock.NORTH)).setValue(FourWayShapedBlock.WEST, state.getValue(FourWayShapedBlock.EAST));
		case COUNTERCLOCKWISE_90:
			return state.setValue(FourWayShapedBlock.NORTH, state.getValue(FourWayShapedBlock.EAST)).setValue(FourWayShapedBlock.EAST, state.getValue(FourWayShapedBlock.SOUTH)).setValue(FourWayShapedBlock.SOUTH, state.getValue(FourWayShapedBlock.WEST)).setValue(FourWayShapedBlock.WEST, state.getValue(FourWayShapedBlock.NORTH));
		case CLOCKWISE_90:
			return state.setValue(FourWayShapedBlock.NORTH, state.getValue(FourWayShapedBlock.WEST)).setValue(FourWayShapedBlock.EAST, state.getValue(FourWayShapedBlock.NORTH)).setValue(FourWayShapedBlock.SOUTH, state.getValue(FourWayShapedBlock.EAST)).setValue(FourWayShapedBlock.WEST, state.getValue(FourWayShapedBlock.SOUTH));
		default:
			return state;
		}
	}

	@Override
	public BlockState mirror(final BlockState state, final Mirror mirror) {
		switch(mirror) {
		case LEFT_RIGHT:
			return state.setValue(FourWayShapedBlock.NORTH, state.getValue(FourWayShapedBlock.SOUTH)).setValue(FourWayShapedBlock.SOUTH, state.getValue(FourWayShapedBlock.NORTH));
		case FRONT_BACK:
			return state.setValue(FourWayShapedBlock.EAST, state.getValue(FourWayShapedBlock.WEST)).setValue(FourWayShapedBlock.WEST, state.getValue(FourWayShapedBlock.EAST));
		default:
			return state.mirror(mirror);
		}
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		final IBlockReader blockReader = context.getLevel();
		final BlockPos blockPos = context.getClickedPos();
		final BlockPos blockNorth = blockPos.north();
		final BlockPos blockEast = blockPos.east();
		final BlockPos blockSouth = blockPos.south();
		final BlockPos blockWest = blockPos.west();
		final BlockState blockStateNorth = blockReader.getBlockState(blockNorth);
		final BlockState blockStateEast = blockReader.getBlockState(blockEast);
		final BlockState blockStateSouth = blockReader.getBlockState(blockSouth);
		final BlockState blockStateWest = blockReader.getBlockState(blockWest);
		return super.getStateForPlacement(context)
				.setValue(AltarBlock.NORTH, this.canConnect(blockStateNorth))
				.setValue(AltarBlock.EAST, this.canConnect(blockStateEast))
				.setValue(AltarBlock.SOUTH, this.canConnect(blockStateSouth))
				.setValue(AltarBlock.WEST, this.canConnect(blockStateWest));
	}

	@Override
	protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FourWayShapedBlock.NORTH, FourWayShapedBlock.EAST, FourWayShapedBlock.WEST, FourWayShapedBlock.SOUTH);
		super.createBlockStateDefinition(builder);
	}

	public static int getMask(final Direction facing) {
		return 1 << facing.get2DDataValue();
	}

	@Override
	protected int getIndex(final BlockState state) {
		return this.statePaletteMap.computeIntIfAbsent(state, stateIn -> {
			int i = 0;
			if (stateIn.getValue(FourWayShapedBlock.NORTH))
				i |= FourWayShapedBlock.getMask(Direction.NORTH);

			if (stateIn.getValue(FourWayShapedBlock.EAST))
				i |= FourWayShapedBlock.getMask(Direction.EAST);

			if (stateIn.getValue(FourWayShapedBlock.SOUTH))
				i |= FourWayShapedBlock.getMask(Direction.SOUTH);

			if (stateIn.getValue(FourWayShapedBlock.WEST))
				i |= FourWayShapedBlock.getMask(Direction.WEST);

			return i;
		});
	}

	protected abstract boolean canConnect(BlockState state);

}
