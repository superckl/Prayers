package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import me.superckl.prayers.Config;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.item.decree.DecreeItem.Type;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.Constants;

public class FertilityDecreeData extends DecreeData{

	private static final ResourceLocation CROP_TAG = new ResourceLocation("minecraft", "crops");
	private static final ResourceLocation SAPLING_TAG = new ResourceLocation("minecraft", "saplings");

	private AABBTicket farmlandTicket;
	private final AxisAlignedBB bb;
	private final Random rand = new Random();

	public FertilityDecreeData(final WeakReference<ItemFrameEntity> ref) {
		super(ref);
		this.bb = MathUtil.withSquareRadius(this.ref.get().getPos(), Config.getInstance().getDecreeRanges().get(Type.FERTILITY).get());
	}

	@Override
	public void onSetup() {
		this.farmlandTicket = FarmlandWaterManager.addAABBTicket(this.ref.get().level, this.bb);
	}

	@Override
	public void onRemove() {
		if(this.farmlandTicket != null)
			this.farmlandTicket.invalidate();
	}

	@Override
	public void tick() {
		final ItemFrameEntity entity = this.ref.get();
		if(entity.level.isClientSide)
			return;
		if(entity.level.isAreaLoaded(entity.getPos(), (int) this.bb.getSize())) {
			final double cropChance = Config.getInstance().getFertilityCropChance().get();
			final Stream<BlockPos> positions = BlockPos.betweenClosedStream(this.bb);
			positions.forEach(pos -> {
				final BlockState state = entity.level.getBlockState(pos);
				if(state.getBlock() instanceof IGrowable && state.getBlock().getTags().contains(FertilityDecreeData.CROP_TAG) ||
						state.getBlock().getTags().contains(FertilityDecreeData.SAPLING_TAG)) {
					final IGrowable growable = (IGrowable)state.getBlock();
					if(growable.isValidBonemealTarget(entity.level, pos, state, entity.level.isClientSide) &&
							this.rand.nextFloat() < cropChance && growable.isBonemealSuccess(entity.level, this.rand, pos, state)) {
						growable.performBonemeal((ServerWorld) entity.level, this.rand, pos, state);
						entity.level.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 0);
						Vector3d toBlock = new Vector3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5).subtract(entity.getX(), entity.getY(), entity.getZ());
						final double mag = toBlock.length();
						toBlock = toBlock.scale(1/mag);
						((ServerWorld)entity.level).sendParticles(ModParticles.ITEM_SACRIFICE.get(), entity.getX(), entity.getY(), entity.getZ(), 0, toBlock.x, toBlock.y, toBlock.z, mag/20);
					}
				}
			});
			final double animalChance = Config.getInstance().getFertilityCropChance().get();
			final List<AnimalEntity> animals = entity.level.getEntitiesOfClass(AnimalEntity.class, this.bb);
			animals.forEach(animal -> {
				if(!animal.isBaby() && animal.canFallInLove() && animal.getAge() == 0 && this.rand.nextFloat() < animalChance)
					animal.setInLove(null);
			});
		}
	}

}
