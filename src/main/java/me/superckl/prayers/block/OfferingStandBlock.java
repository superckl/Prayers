package me.superckl.prayers.block;

import me.superckl.prayers.init.ModTiles;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class OfferingStandBlock extends ShapedBlock{

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public OfferingStandBlock() {
		super(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL), true);
		this.registerDefaultState(this.defaultBlockState().setValue(OfferingStandBlock.FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(OfferingStandBlock.FACING, context.getNearestLookingDirection().getOpposite());
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
	public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player,
			final Hand handIn, final BlockRayTraceResult hit) {
		final OfferingStandTileEntity offering_stand = (OfferingStandTileEntity) worldIn.getBlockEntity(pos);
		return offering_stand.onActivateBy(player, handIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(final BlockState state, final World worldIn, final BlockPos pos, final BlockState newState, final boolean isMoving) {
		if(!state.is(newState.getBlock())) {
			final OfferingStandTileEntity offering_stand = (OfferingStandTileEntity) worldIn.getBlockEntity(pos);
			InventoryHelper.dropContents(worldIn, pos, offering_stand);
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	protected VoxelShape[] getShapes() {
		final VoxelShape base = Block.box(5, 0, 5, 11, 1, 11);
		final VoxelShape pillar = Block.box(7, 1, 7, 9, 4, 9);
		final VoxelShape top_base_n = Block.box(7, 4, 6, 9, 5, 7);
		final VoxelShape top_base_e = Block.box(9, 4, 7, 10, 5, 9);
		final VoxelShape top_base_s = Block.box(7, 4, 9, 9, 5, 10);
		final VoxelShape top_base_w = Block.box(6, 4, 7, 7, 5, 9);
		final VoxelShape trim_1 = Block.box(9, 5, 9, 10, 6, 10);
		final VoxelShape trim_2 = Block.box(9, 5, 6, 10, 6, 7);
		final VoxelShape trim_3 = Block.box(6, 5, 6, 7, 6, 7);
		final VoxelShape trim_4 = Block.box(6, 5, 9, 7, 6, 10);
		final VoxelShape top_side_n = Block.box(6, 5, 5, 10, 6, 6);
		final VoxelShape top_side_e = Block.box(10, 5, 6, 11, 6, 10);
		final VoxelShape top_side_s = Block.box(6, 5, 10, 10, 6, 11);
		final VoxelShape top_side_w = Block.box(5, 5, 6, 6, 6, 10);
		final VoxelShape top_side_top_n = Block.box(7, 6, 5, 9, 7, 6);
		final VoxelShape top_side_top_e = Block.box(10, 6, 7, 11, 7, 9);
		final VoxelShape top_side_top_s = Block.box(7, 6, 10, 9, 7, 11);
		final VoxelShape top_side_top_w = Block.box(5, 6, 7, 6, 7, 9);

		return new VoxelShape[] {VoxelShapes.or(base, pillar, top_base_n, top_base_e, top_base_s, top_base_w, trim_1,
				trim_2, trim_3, trim_4, top_side_n, top_side_e, top_side_s, top_side_w, top_side_top_n,
				top_side_top_e, top_side_top_s, top_side_top_w)};
	}

	@Override
	protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(OfferingStandBlock.FACING);
		super.createBlockStateDefinition(builder);
	}

	@Override
	protected int getIndex(final BlockState state) {
		return 0;
	}

}
