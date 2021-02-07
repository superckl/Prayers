package me.superckl.prayers.block;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

//Much of this is inspired by FourWayBlock, it's just unfortunately not quite applicable here
public class AltarBlock extends Block implements IWaterLoggable{

	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter(facingProperty -> facingProperty.getKey().getAxis().isHorizontal()).collect(Util.toMapCollector());
	protected final VoxelShape[] shapes;
	private final Object2IntMap<BlockState> statePaletteMap = new Object2IntOpenHashMap<>();

	public AltarBlock() {
		super(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(2.0F, 6.0F));
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
		this.shapes = this.makeShapes();
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shapes[this.getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.getShape(state, reader, pos, context);
	}

	@Override
	public VoxelShape getRenderShape(final BlockState state, final IBlockReader reader, final BlockPos pos) {
		return this.getShape(state, reader, pos, null);
	}

	@Override
	public VoxelShape getRayTraceShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.getShape(state, reader, pos, context);
	}

	@Override
	public FluidState getFluidState(final BlockState state) {
		return state.get(AltarBlock.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState updatePostPlacement(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
		if (state.get(AltarBlock.WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ?
				state.with(AltarBlock.FACING_TO_PROPERTY_MAP.get(facing),
						this.canConnect(facingState, facingState.isSolidSide(world, facingPos, facing.getOpposite()), facing.getOpposite()))
				: state;
	}

	@Override
	protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AltarBlock.NORTH, AltarBlock.EAST, AltarBlock.WEST, AltarBlock.SOUTH, AltarBlock.WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		final IBlockReader blockReader = context.getWorld();
		final BlockPos blockPos = context.getPos();
		final FluidState fluidState = context.getWorld().getFluidState(context.getPos());
		final BlockPos blockNorth = blockPos.north();
		final BlockPos blockEast = blockPos.east();
		final BlockPos blockSouth = blockPos.south();
		final BlockPos blockWest = blockPos.west();
		final BlockState blockStateNorth = blockReader.getBlockState(blockNorth);
		final BlockState blockStateEast = blockReader.getBlockState(blockEast);
		final BlockState blockStateSouth = blockReader.getBlockState(blockSouth);
		final BlockState blockStateWest = blockReader.getBlockState(blockWest);
		return super.getStateForPlacement(context)
				.with(AltarBlock.NORTH, this.canConnect(blockStateNorth, blockStateNorth.isSolidSide(blockReader, blockNorth, Direction.SOUTH), Direction.SOUTH))
				.with(AltarBlock.EAST, this.canConnect(blockStateEast, blockStateEast.isSolidSide(blockReader, blockEast, Direction.WEST), Direction.WEST))
				.with(AltarBlock.SOUTH, this.canConnect(blockStateSouth, blockStateSouth.isSolidSide(blockReader, blockSouth, Direction.NORTH), Direction.NORTH))
				.with(AltarBlock.WEST, this.canConnect(blockStateWest, blockStateWest.isSolidSide(blockReader, blockWest, Direction.EAST), Direction.EAST))
				.with(AltarBlock.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}



	public boolean canConnect(final BlockState state, final boolean isSideSolid, final Direction direction) {
		return state.getBlock() instanceof AltarBlock;
	}

	@Override
	public BlockState rotate(final BlockState state, final Rotation rot) {
		switch(rot) {
		case CLOCKWISE_180:
			return state.with(AltarBlock.NORTH, state.get(AltarBlock.SOUTH)).with(AltarBlock.EAST, state.get(AltarBlock.WEST)).with(AltarBlock.SOUTH, state.get(AltarBlock.NORTH)).with(AltarBlock.WEST, state.get(AltarBlock.EAST));
		case COUNTERCLOCKWISE_90:
			return state.with(AltarBlock.NORTH, state.get(AltarBlock.EAST)).with(AltarBlock.EAST, state.get(AltarBlock.SOUTH)).with(AltarBlock.SOUTH, state.get(AltarBlock.WEST)).with(AltarBlock.WEST, state.get(AltarBlock.NORTH));
		case CLOCKWISE_90:
			return state.with(AltarBlock.NORTH, state.get(AltarBlock.WEST)).with(AltarBlock.EAST, state.get(AltarBlock.NORTH)).with(AltarBlock.SOUTH, state.get(AltarBlock.EAST)).with(AltarBlock.WEST, state.get(AltarBlock.SOUTH));
		default:
			return state;
		}
	}

	@Override
	public BlockState mirror(final BlockState state, final Mirror mirror) {
		switch(mirror) {
		case LEFT_RIGHT:
			return state.with(AltarBlock.NORTH, state.get(AltarBlock.SOUTH)).with(AltarBlock.SOUTH, state.get(AltarBlock.NORTH));
		case FRONT_BACK:
			return state.with(AltarBlock.EAST, state.get(AltarBlock.WEST)).with(AltarBlock.WEST, state.get(AltarBlock.EAST));
		default:
			return state.mirror(mirror);
		}
	}

	protected VoxelShape[] makeShapes() {
		final VoxelShape base = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
		final VoxelShape top = Block.makeCuboidShape(0, 15, 0, 16, 16, 16);
		final VoxelShape center = Block.makeCuboidShape(3, 1, 3, 13, 15, 13);
		final VoxelShape indepAltar = VoxelShapes.or(base, top, center);

		final VoxelShape westExt = Block.makeCuboidShape(0, 1, 3, 3, 15, 13);
		final VoxelShape northExt = Block.makeCuboidShape(3, 1, 0, 13, 15, 3);
		final VoxelShape eastExt = Block.makeCuboidShape(13, 1, 3, 16, 15, 13);
		final VoxelShape southExt = Block.makeCuboidShape(3, 1, 13, 13, 15, 16);

		final int north = AltarBlock.getMask(Direction.NORTH);
		final int east = AltarBlock.getMask(Direction.EAST);
		final int south = AltarBlock.getMask(Direction.SOUTH);
		final int west = AltarBlock.getMask(Direction.WEST);
		//There are 16 possible positions:
		final VoxelShape[] shapes = new VoxelShape[16];
		shapes[0] = indepAltar; //No ext

		//All 8 including north (9)
		shapes[north] = VoxelShapes.or(indepAltar, northExt);

		shapes[north | east] = VoxelShapes.or(shapes[north], eastExt);
		shapes[north | south] = VoxelShapes.or(shapes[north], southExt);
		shapes[north | west] = VoxelShapes.or(shapes[north], westExt);

		shapes[north | east | south] = VoxelShapes.or(shapes[north | east], southExt);
		shapes[north | east | west] = VoxelShapes.or(shapes[north | east], westExt);
		shapes[north | south | west] = VoxelShapes.or(shapes[north | south], westExt);

		shapes[north | south | west | east] = VoxelShapes.or(shapes[north | south | west], eastExt);

		//Remaining 4 including east (13)
		shapes[east] = VoxelShapes.or(indepAltar, eastExt);

		shapes[east | south] = VoxelShapes.or(shapes[east], southExt);
		shapes[east | west] = VoxelShapes.or(shapes[east], westExt);

		shapes[east | west | south] = VoxelShapes.or(shapes[east | west], southExt);

		//Remaining 2 including south (15)
		shapes[south] = VoxelShapes.or(indepAltar, southExt);

		shapes[south | west] = VoxelShapes.or(shapes[south], westExt);

		//Remaining 1 including west (16)
		shapes[west] = VoxelShapes.or(indepAltar, westExt);

		return shapes;
	}

	private static int getMask(final Direction facing) {
		return 1 << facing.getHorizontalIndex();
	}

	protected int getIndex(final BlockState state) {
		return this.statePaletteMap.computeIntIfAbsent(state, stateIn -> {
			int i = 0;
			if (stateIn.get(AltarBlock.NORTH))
				i |= AltarBlock.getMask(Direction.NORTH);

			if (stateIn.get(AltarBlock.EAST))
				i |= AltarBlock.getMask(Direction.EAST);

			if (stateIn.get(AltarBlock.SOUTH))
				i |= AltarBlock.getMask(Direction.SOUTH);

			if (stateIn.get(AltarBlock.WEST))
				i |= AltarBlock.getMask(Direction.WEST);

			return i;
		});
	}

}
