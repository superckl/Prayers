package me.superckl.prayers.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CraftingStandBlock extends FourWayShapedBlock{

	public static final BooleanProperty CENTER = BooleanProperty.create("center");

	public CraftingStandBlock() {
		super(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL), true);
		this.registerDefaultState(this.defaultBlockState().setValue(CraftingStandBlock.CENTER, true));
	}

	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		if(!state.is(this))
			state = super.getStateForPlacement(context).setValue(CraftingStandBlock.CENTER, false);
		final Vector3d hit = context.getClickLocation();
		final Vector3d dirVec = hit.subtract(Math.floor(hit.x)+0.5, 0, Math.floor(hit.z)+0.5);
		final float tolerance = state.getValue(CraftingStandBlock.CENTER) ? 1.5F:2.75F;
		final Direction dir = CraftingStandBlock.directionFromVec(dirVec, tolerance/16);
		if(state.is(this) && state.getValue(CraftingStandBlock.propertyFromDirection(dir)))
			return state;
		return state.setValue(CraftingStandBlock.propertyFromDirection(dir), true);
	}

	@Override
	public BlockState updateShape(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world,
			final BlockPos currentPos, final BlockPos facingPos) {
		//Need to bypass FourWayShapedBlock here, do this manually
		if (state.getValue(ShapedBlock.WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		return state;
	}

	public BlockState subtractStands(BlockState from, final BlockState state) {
		if(state.getValue(CraftingStandBlock.CENTER))
			from = from.setValue(CraftingStandBlock.CENTER, false);
		for(final Property<Boolean> prop:FourWayShapedBlock.FACING_TO_PROPERTY_MAP.values())
			if(state.getValue(prop))
				from = from.setValue(prop, false);
		return from;
	}

	public boolean hasStand(final BlockState state, final Direction dir) {
		return state.getValue(CraftingStandBlock.propertyFromDirection(dir));
	}

	@Override
	public boolean canBeReplaced(final BlockState state, final BlockItemUseContext context) {
		return context.getItemInHand().getItem() == this.asItem() && this.getStateForPlacement(context) != state;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(final BlockState state, final World worldIn, final BlockPos pos, final BlockState newState, final boolean isMoving) {
		final CraftingStandTileEntity crafting_stand = (CraftingStandTileEntity) worldIn.getBlockEntity(pos);
		if(state.is(newState.getBlock())) {
			final NonNullList<ItemStack> stacks = NonNullList.create();
			Direction.Plane.HORIZONTAL.forEach(dir -> {
				if(state.getValue(CraftingStandBlock.propertyFromDirection(dir)) && !newState.getValue(CraftingStandBlock.propertyFromDirection(dir)))
					stacks.add(crafting_stand.removeItemNoUpdate(CraftingStandTileEntity.dirToSlot.getInt(dir)));
			});
			if(state.getValue(CraftingStandBlock.CENTER) && !newState.getValue(CraftingStandBlock.CENTER))
				stacks.add(crafting_stand.removeItemNoUpdate(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP)));
			InventoryHelper.dropContents(worldIn, pos, stacks);
		}else {
			InventoryHelper.dropContents(worldIn, pos, crafting_stand);
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	public static Direction directionFromVec(final Vector3d dirVec, final float tolerance) {
		if(Math.abs(dirVec.x) <= tolerance && Math.abs(dirVec.z) <= tolerance)
			return Direction.UP;
		return Direction.getNearest(dirVec.x, 0, dirVec.z);
	}

	public static Property<Boolean> propertyFromDirection(final Direction dir){
		if(dir == Direction.UP)
			return CraftingStandBlock.CENTER;
		if(dir == Direction.DOWN)
			throw new IllegalArgumentException("Down is not a valid direction!");
		return FourWayShapedBlock.FACING_TO_PROPERTY_MAP.get(dir);
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
	public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player,
			final Hand handIn, final BlockRayTraceResult hit) {
		final CraftingStandTileEntity craftingStand = (CraftingStandTileEntity) worldIn.getBlockEntity(pos);
		final Vector3d hitVec = hit.getLocation();
		final Vector3d dirVec = hitVec.subtract(Math.floor(hitVec.x)+0.5, hitVec.y, Math.floor(hitVec.z)+0.5);
		final Direction dir = CraftingStandBlock.directionFromVec(dirVec, 1.5F/16);
		if(player.isCrouching() && player.getItemInHand(handIn).isEmpty() && craftingStand.getItem(CraftingStandTileEntity.dirToSlot.getInt(dir)).isEmpty()) {
			BlockState newState = state.setValue(CraftingStandBlock.propertyFromDirection(dir), false);
			if(!newState.getValue(CraftingStandBlock.CENTER) && !FourWayShapedBlock.FACING_TO_PROPERTY_MAP.values().stream().anyMatch(newState::getValue))
				newState = newState.getValue(ShapedBlock.WATERLOGGED) ? Blocks.WATER.defaultBlockState():Blocks.AIR.defaultBlockState();
			worldIn.setBlockAndUpdate(pos, newState);
			final ItemStack toDrop = new ItemStack(this::asItem);
			if(!player.addItem(toDrop))
				InventoryHelper.dropContents(worldIn, pos, NonNullList.of(ItemStack.EMPTY, toDrop));
		}
		return craftingStand.onActivate(player, handIn, dir);
	}

	@Override
	protected VoxelShape[] getShapes() {
		final VoxelShape base = Block.box(7, 0, 7, 9, 0.5, 9);
		final VoxelShape connector_1 = Block.box(7, 0.5, 7, 7.5, 1, 7.5);
		final VoxelShape connector_2 = Block.box(7, 0.5, 8.5, 7.5, 1, 9);
		final VoxelShape connector_3 = Block.box(8.5, 0.5, 8.5, 9, 1, 9);
		final VoxelShape connector_4 = Block.box(8.5, 0.5, 7, 9, 1, 7.5);
		final VoxelShape side_top_n = Block.box(7.5, 1, 9, 8.5, 1.5, 9.5);
		final VoxelShape side_top_s = Block.box(7.5, 1, 6.5, 8.5, 1.5, 7);
		final VoxelShape side_top_e = Block.box(9, 1, 7.5, 9.5, 1.5, 8.5);
		final VoxelShape side_top_w = Block.box(6.5, 1, 7.5, 7, 1.5, 8.5);
		final VoxelShape side_n = Block.box(7, 0.5, 9, 9, 1, 9.5);
		final VoxelShape side_s = Block.box(7, 0.5, 6.5, 9, 1, 7);
		final VoxelShape side_e = Block.box(9, 0.5, 7, 9.5, 1, 9);
		final VoxelShape side_w = Block.box(6.5, 0.5, 7, 7, 1, 9);

		final VoxelShape stand = VoxelShapes.or(base, connector_1, connector_2, connector_3, connector_4, side_n,
				side_s, side_e, side_w, side_top_n, side_top_s, side_top_e, side_top_w);

		final VoxelShape stand_n = stand.move(0, 0, -5.5/16);
		final VoxelShape stand_s = stand.move(0, 0, 5.5/16);
		final VoxelShape stand_e = stand.move(5.5/16, 0, 0);
		final VoxelShape stand_w = stand.move(-5.5/16, 0, 0);

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
		if(state.getValue(CraftingStandBlock.CENTER))
			ind |= 16;
		return ind;
	}

	@Override
	protected void createBlockStateDefinition(final Builder<Block, BlockState> builder) {
		builder.add(CraftingStandBlock.CENTER);
		super.createBlockStateDefinition(builder);
	}

	@Override
	protected boolean canConnect(final BlockState state) {
		return false;
	}

}
