package me.superckl.prayers.block;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.superckl.prayers.capability.IPrayerUser;
import me.superckl.prayers.init.ModBlocks;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

//Much of this is inspired by FourWayBlock, it's just unfortunately not quite applicable here
public class AltarBlock extends FourWayShapedBlock{

	public static final Set<ResourceLocation> validTopBlocks = Sets.newHashSet(new ResourceLocation("minecraft", "torch"), new ResourceLocation("minecraft", "air"), ModBlocks.OFFERING_STAND.getId());

	@RequiredArgsConstructor
	@Getter
	public enum AltarTypes{
		SANDSTONE(100, 1F/48000F, 2),
		GILDED_SANDSTONE(1000, 1F/24000F, 4),
		MARBLE(100000, 1F/24000F, 5);

		private final float maxPoints;
		private final float rechargeRate;
		private final int maxConnected;

	}

	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter(facingProperty -> facingProperty.getKey().getAxis().isHorizontal()).collect(Util.toMapCollector());
	private final AltarTypes type;

	public AltarBlock(final AltarTypes type) {
		super(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(2.0F, 6.0F), true);
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
	public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack stack) {
		if(placer instanceof PlayerEntity) {
			final AltarTileEntity altar = (AltarTileEntity) worldIn.getTileEntity(pos);
			altar.checkMultiblock(true);
			altar.setOwner(((PlayerEntity) placer).getUniqueID(), true);
		}
	}

	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player,
			final Hand handIn, final BlockRayTraceResult hit) {
		final AltarTileEntity altar = (AltarTileEntity) worldIn.getTileEntity(pos);
		if(!player.isSneaking()) {
			if (altar.setOwner(player.getUniqueID(), true))
				return worldIn.isRemote ? ActionResultType.SUCCESS:ActionResultType.CONSUME;
			if(player.getHeldItem(handIn).isEmpty()) {
				final IPrayerUser user = IPrayerUser.getUser(player);
				if(altar.rechargeUser(user) > 0)
					return worldIn.isRemote ? ActionResultType.SUCCESS:ActionResultType.CONSUME;
			}
		}
		return altar.onActivateBy(player, handIn);
	}

	@Override
	public boolean canConnect(final BlockState state) {
		return state.getBlock() instanceof AltarBlock && ((AltarBlock) state.getBlock()).type == this.type;
	}

	@Override
	protected VoxelShape[] getShapes() {
		final VoxelShape base = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
		final VoxelShape top = Block.makeCuboidShape(0, 15, 0, 16, 16, 16);
		final VoxelShape center = Block.makeCuboidShape(3, 1, 3, 13, 15, 13);
		final VoxelShape indepAltar = VoxelShapes.or(base, top, center);

		final VoxelShape westExt = Block.makeCuboidShape(0, 1, 3, 3, 15, 13);
		final VoxelShape northExt = Block.makeCuboidShape(3, 1, 0, 13, 15, 3);
		final VoxelShape eastExt = Block.makeCuboidShape(13, 1, 3, 16, 15, 13);
		final VoxelShape southExt = Block.makeCuboidShape(3, 1, 13, 13, 15, 16);

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
			return reader.getBlockState(pos).getShape(reader, pos).withOffset(diff.getX(), diff.getY(), diff.getZ());
		}).reduce(VoxelShapes::or).orElse(VoxelShapes.empty());
	}

}
