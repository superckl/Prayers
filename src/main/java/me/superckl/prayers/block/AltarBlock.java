package me.superckl.prayers.block;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.Config;
import me.superckl.prayers.block.entity.AltarTileEntity;
import me.superckl.prayers.capability.CapabilityHandler;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AltarBlock extends FourWayShapedBlock{

	@RequiredArgsConstructor
	@Getter
	public enum AltarTypes{

		SANDSTONE,
		GILDED_SANDSTONE,
		MARBLE;

		public int getMaxConnected() {
			return Config.getInstance().getAltarConnected().get(this).get();
		}

		public double getMaxPoints() {
			return Config.getInstance().getAltarPoints().get(this).get();
		}

		public double getRechargeRate() {
			return Config.getInstance().getAltarRecharge().get(this).get();
		}

		public double getTransferRate() {
			return Config.getInstance().getAltarTransfer().get(this).get();
		}

	}

	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(facingProperty -> facingProperty.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	private final AltarTypes type;

	public AltarBlock(final AltarTypes type) {
		super(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F), true);
		this.type = type;
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTiles.ALTARS.get(this.type).get().create();
	}

	@Override
	public void setPlacedBy(final World worldIn, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack stack) {
		if(placer instanceof PlayerEntity) {
			final AltarTileEntity altar = (AltarTileEntity) worldIn.getBlockEntity(pos);
			altar.checkMultiblock(true);
			if(CapabilityHandler.getPrayerCapability((PlayerEntity) placer).isUnlocked())
				altar.setOwner(((PlayerEntity) placer).getUUID(), true);
		}
	}

	@Override
	public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player,
			final Hand handIn, final BlockRayTraceResult hit) {
		final AltarTileEntity altar = (AltarTileEntity) worldIn.getBlockEntity(pos);
		if(!player.isCrouching()) {
			if(worldIn.isClientSide)
				return ActionResultType.SUCCESS;
			if (altar.setOwner(player.getUUID(), true))
				return ActionResultType.CONSUME;
			if(player.getItemInHand(handIn).isEmpty())
				if(altar.rechargeUser(player) > 0)
					return ActionResultType.CONSUME;
		}
		return altar.onActivateBy(player, handIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(final BlockState state, final World worldIn, final BlockPos pos, final BlockState newState, final boolean isMoving) {
		if(!state.is(newState.getBlock())) {
			final AltarTileEntity te = (AltarTileEntity) worldIn.getBlockEntity(pos);
			if(!te.getAltarItem().isEmpty())
				InventoryHelper.dropContents(worldIn, pos, NonNullList.of(ItemStack.EMPTY, te.getAltarItem()));
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public boolean canConnect(final BlockState state) {
		return state.getBlock() instanceof AltarBlock && ((AltarBlock) state.getBlock()).type == this.type;
	}

	@Override
	protected VoxelShape[] getShapes() {
		final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
		final VoxelShape top = Block.box(0, 15, 0, 16, 16, 16);
		final VoxelShape center = Block.box(3, 1, 3, 13, 15, 13);
		final VoxelShape indepAltar = VoxelShapes.or(base, top, center);

		final VoxelShape westExt = Block.box(0, 1, 3, 3, 15, 13);
		final VoxelShape northExt = Block.box(3, 1, 0, 13, 15, 3);
		final VoxelShape eastExt = Block.box(13, 1, 3, 16, 15, 13);
		final VoxelShape southExt = Block.box(3, 1, 13, 13, 15, 16);

		final int north = FourWayShapedBlock.getMask(Direction.NORTH);
		final int east = FourWayShapedBlock.getMask(Direction.EAST);
		final int south = FourWayShapedBlock.getMask(Direction.SOUTH);
		final int west = FourWayShapedBlock.getMask(Direction.WEST);
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

	public static Set<BlockPos> findConnected(final IBlockReader reader, final BlockPos origin) {
		if(!(reader.getBlockState(origin).getBlock() instanceof AltarBlock))
			return Sets.newHashSet();
		final BiPredicate<BlockPos, BlockPos> canConnect = (pos1, pos2) -> {
			final BlockState bs1 = reader.getBlockState(pos1);
			final BlockState bs2 = reader.getBlockState(pos2);
			if(!(bs1.getBlock() instanceof AltarBlock))
				return false;
			return ((AltarBlock) bs1.getBlock()).canConnect(bs2);
		};
		final Function<BlockPos, Set<BlockPos>> neighborhoodSupplier = pos -> Sets.newHashSet(pos.north(), pos.east(), pos.south(), pos.west());
		return MathUtil.dsf(origin, canConnect, neighborhoodSupplier);
	}

	public static VoxelShape connectAltars(final BlockState altar, final IBlockReader reader, final BlockPos origin) {
		final Set<BlockPos> connected = AltarBlock.findConnected(reader, origin);


		return connected.stream().map(pos -> {
			final BlockPos diff = pos.subtract(origin);
			return reader.getBlockState(pos).getShape(reader, pos).move(diff.getX(), diff.getY(), diff.getZ());
		}).reduce(VoxelShapes::or).orElse(VoxelShapes.empty());
	}

}
