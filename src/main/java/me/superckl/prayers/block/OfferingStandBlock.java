package me.superckl.prayers.block;

import me.superckl.prayers.init.ModTiles;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class OfferingStandBlock extends Block{

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected final VoxelShape shape;

	public OfferingStandBlock() {
		super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL));
		this.setDefaultState(this.stateContainer.getBaseState().with(OfferingStandBlock.FACING, Direction.NORTH).with(OfferingStandBlock.WATERLOGGED, false));
		this.shape = this.getShape();
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shape;
	}

	@Override
	public VoxelShape getCollisionShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shape;
	}

	@Override
	public VoxelShape getRenderShape(final BlockState state, final IBlockReader reader, final BlockPos pos) {
		return this.shape;
	}

	@Override
	public VoxelShape getRayTraceShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		return this.shape;
	}

	@Override
	public FluidState getFluidState(final BlockState state) {
		return state.get(AltarBlock.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState updatePostPlacement(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
		if (state.get(AltarBlock.WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return state;
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		final FluidState fluidState = context.getWorld().getFluidState(context.getPos());
		return super.getStateForPlacement(context).with(OfferingStandBlock.FACING, context.getPlacementHorizontalFacing().getOpposite())
				.with(OfferingStandBlock.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTiles.OFFERING_STAND.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player,
			final Hand handIn, final BlockRayTraceResult hit) {
		final OfferingStandTileEntity offering_stand = (OfferingStandTileEntity) worldIn.getTileEntity(pos);
		return offering_stand.onActivateBy(player, handIn);
	}

	protected VoxelShape getShape() {
		final VoxelShape base = Block.makeCuboidShape(5, 0, 5, 11, 1, 11);
		final VoxelShape pillar = Block.makeCuboidShape(7, 1, 7, 9, 4, 9);
		final VoxelShape top_base_n = Block.makeCuboidShape(7, 4, 6, 9, 5, 7);
		final VoxelShape top_base_e = Block.makeCuboidShape(9, 4, 7, 10, 5, 9);
		final VoxelShape top_base_s = Block.makeCuboidShape(7, 4, 9, 9, 5, 10);
		final VoxelShape top_base_w = Block.makeCuboidShape(6, 4, 7, 7, 5, 9);
		final VoxelShape trim_1 = Block.makeCuboidShape(9, 5, 9, 10, 6, 10);
		final VoxelShape trim_2 = Block.makeCuboidShape(9, 5, 6, 10, 6, 7);
		final VoxelShape trim_3 = Block.makeCuboidShape(6, 5, 6, 7, 6, 7);
		final VoxelShape trim_4 = Block.makeCuboidShape(6, 5, 9, 7, 6, 10);
		final VoxelShape top_side_n = Block.makeCuboidShape(6, 5, 5, 10, 6, 6);
		final VoxelShape top_side_e = Block.makeCuboidShape(10, 5, 6, 11, 6, 10);
		final VoxelShape top_side_s = Block.makeCuboidShape(6, 5, 10, 10, 6, 11);
		final VoxelShape top_side_w = Block.makeCuboidShape(5, 5, 6, 6, 6, 10);
		final VoxelShape top_side_top_n = Block.makeCuboidShape(7, 6, 5, 9, 7, 6);
		final VoxelShape top_side_top_e = Block.makeCuboidShape(10, 6, 7, 11, 7, 9);
		final VoxelShape top_side_top_s = Block.makeCuboidShape(7, 6, 10, 9, 7, 11);
		final VoxelShape top_side_top_w = Block.makeCuboidShape(5, 6, 7, 6, 7, 9);

		return VoxelShapes.or(base, pillar, top_base_n, top_base_e, top_base_s, top_base_w, trim_1,
				trim_2, trim_3, trim_4, top_side_n, top_side_e, top_side_s, top_side_w, top_side_top_n,
				top_side_top_e, top_side_top_s, top_side_top_w);
	}

	@Override
	protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(OfferingStandBlock.FACING, OfferingStandBlock.WATERLOGGED);
	}

}
