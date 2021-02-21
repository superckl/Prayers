package me.superckl.prayers.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CraftingStandBlock extends FourWayShapedBlock{

	public static final BooleanProperty CENTER = BooleanProperty.create("center");

	public CraftingStandBlock() {
		super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL), true);
		this.setDefaultState(this.getDefaultState().with(CraftingStandBlock.CENTER, true));
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		final Vector3d hit = context.getHitVec();
		final Vector3d dirVec = hit.subtract(Math.floor(hit.x)+0.5, 0, Math.floor(hit.z)+0.5);
		final Direction dir = Direction.getFacingFromVector(dirVec.getX(), 0, dirVec.getZ());

		return super.getStateForPlacement(context).with(CraftingStandBlock.CENTER, false).with(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir), true);
	}

	@Override
	public BlockState updatePostPlacement(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world,
			final BlockPos currentPos, final BlockPos facingPos) {
		//Need to bypass FourWayShapedBlock here, do this manually
		if (state.get(ShapedBlock.WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		return state;
	}

	public boolean hasStand(final BlockState state, final Direction dir) {
		return dir == Direction.UP ? state.get(CraftingStandBlock.CENTER):state.get(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir));
	}

	@Override
	public boolean isReplaceable(final BlockState state, final BlockItemUseContext context) {
		return context.getItem().getItem() == this.asItem() && this.findNextPlacement(state, context) != state;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(final BlockState state, final World worldIn, final BlockPos pos, final BlockState newState, final boolean isMoving) {
		final CraftingStandTileEntity crafting_stand = (CraftingStandTileEntity) worldIn.getTileEntity(pos);
		if(state.isIn(newState.getBlock())) {
			final List<ItemStack> stacks = Lists.newArrayList();
			Direction.Plane.HORIZONTAL.forEach(dir -> {
				if(state.get(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir)) && !newState.get(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir)))
					stacks.add(crafting_stand.removeStackFromSlot(CraftingStandTileEntity.dirToSlot.getInt(dir)));
			});
			if(state.get(CraftingStandBlock.CENTER) && !newState.get(CraftingStandBlock.CENTER))
				stacks.add(crafting_stand.removeStackFromSlot(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP)));
			InventoryHelper.dropInventoryItems(worldIn, pos, new Inventory(stacks.toArray(new ItemStack[stacks.size()])));
		}else {
			InventoryHelper.dropInventoryItems(worldIn, pos, crafting_stand);
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new CraftingStandTileEntity();
	}

	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player,
			final Hand handIn, final BlockRayTraceResult hit) {
		final CraftingStandTileEntity craftingStand = (CraftingStandTileEntity) worldIn.getTileEntity(pos);
		final Vector3d hitVec = hit.getHitVec();
		final Vector3d dirVec = hitVec.subtract(Math.floor(hitVec.x)+0.5, hitVec.y, Math.floor(hitVec.z)+0.5);
		Direction dir = Direction.getFacingFromVector(dirVec.x, 0, dirVec.z);
		if(Math.abs(dirVec.x) <= 1.5F/16 && Math.abs(dirVec.z) <= 1.5F/16)
			dir = Direction.UP;
		return craftingStand.onActivate(player, handIn, dir);
	}

	public BlockState findNextPlacement(final BlockState state, final BlockItemUseContext context) {
		final Direction[] toCheck = new Direction[] {Direction.NORTH, Direction.EAST};
		Direction empty = null;
		for(final Direction dir:toCheck) {
			final Direction opposite = dir.getOpposite();
			if(state.get(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir))) {
				if(!state.get(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(opposite)))
					return state.with(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(opposite), true);
				else if(!state.get(CraftingStandBlock.CENTER))
					return state.with(CraftingStandBlock.CENTER, true);
			} else if(state.get(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(opposite)))
				return state.with(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir), true);
			else
				empty = dir;
		}
		if(empty == null)
			return state;
		final Vector3d hit = context.getHitVec();
		final Vector3d dirVec = hit.subtract(Math.floor(hit.x)+0.5, 0, Math.floor(hit.z)+0.5);
		final Vector3i emptyVec = empty.getDirectionVec();
		final double emptyDot = emptyVec.getX()*dirVec.getX()+emptyVec.getZ()*dirVec.getZ();
		if(emptyDot > 0)
			return state.with(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(empty), true);
		else
			return state.with(FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(empty.getOpposite()), true);
	}

	@Override
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

		final int north = FourWayShapedBlock.getMask(Direction.NORTH);
		final int east = FourWayShapedBlock.getMask(Direction.EAST);
		final int south = FourWayShapedBlock.getMask(Direction.SOUTH);
		final int west = FourWayShapedBlock.getMask(Direction.WEST);

		final VoxelShape[] shapes = new VoxelShape[16];
		shapes[0] = stand;//only center, this state should never happen

		//All 8 including north (9)
		shapes[north] = stand_n;

		shapes[north | east] = VoxelShapes.or(shapes[north], stand_e);
		shapes[north | south] = VoxelShapes.or(shapes[north], stand_s);
		shapes[north | west] = VoxelShapes.or(shapes[north], stand_w);

		shapes[north | east | south] = VoxelShapes.or(shapes[north | east], stand_s);
		shapes[north | east | west] = VoxelShapes.or(shapes[north | east], stand_w);
		shapes[north | south | west] = VoxelShapes.or(shapes[north | south], stand_w);

		shapes[north | east | south | west] = VoxelShapes.or(shapes[north | east | south], stand_w);

		//Remaining 4 including east (13)
		shapes[east] = stand_e;

		shapes[east | south] = VoxelShapes.or(shapes[east], stand_s);
		shapes[east | west] = VoxelShapes.or(shapes[east], stand_w);

		shapes[east | west | south] = VoxelShapes.or(shapes[east | west], stand_s);

		//Remaining 2 including south (15)
		shapes[south] = stand_s;

		shapes[south | west] = VoxelShapes.or(shapes[south], stand_w);

		//Remaining 1 including west (16)
		shapes[west] = stand_w;

		final VoxelShape[] allShapes = new VoxelShape[32];
		System.arraycopy(shapes, 0, allShapes, 0, 16);
		for (int i = 0; i < shapes.length; i++)
			allShapes[i | 16] = VoxelShapes.or(stand, shapes[i]);

		return allShapes;
	}

	@Override
	protected int getIndex(final BlockState state) {
		int ind = super.getIndex(state);
		if(state.get(CraftingStandBlock.CENTER))
			ind |= 16;
		return ind;
	}

	@Override
	protected void fillStateContainer(final Builder<Block, BlockState> builder) {
		builder.add(CraftingStandBlock.CENTER);
		super.fillStateContainer(builder);
	}

	@Override
	protected boolean canConnect(final BlockState state) {
		return false;
	}

}
