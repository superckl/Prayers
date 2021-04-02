package me.superckl.prayers.item.decree;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.stream.Stream;

import me.superckl.prayers.Config;
import me.superckl.prayers.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.Constants;

public class FertilityDecreeData extends DecreeData{

	private static final ResourceLocation CROP_TAG = new ResourceLocation("minecraft", "crops");

	private AABBTicket farmlandTicket;
	private final AxisAlignedBB bb;
	private final Random rand = new Random();

	public FertilityDecreeData(final WeakReference<ItemFrameEntity> ref) {
		super(ref);
		this.bb = MathUtil.withSquareRadius(this.ref.get().getPos(), Config.getInstance().getFertilityRange().get());
	}

	@Override
	public void setup() {
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
			final double chance = Config.getInstance().getFertilityChance().get();
			final Stream<BlockPos> positions = BlockPos.betweenClosedStream(this.bb);
			positions.forEach(pos -> {
				final BlockState state = entity.level.getBlockState(pos);
				if(state.getBlock() instanceof IGrowable && state.getBlock().getTags().contains(FertilityDecreeData.CROP_TAG)) {
					final IGrowable growable = (IGrowable)state.getBlock();
					if(growable.isValidBonemealTarget(entity.level, pos, state, entity.level.isClientSide) &&
							this.rand.nextFloat() < chance && growable.isBonemealSuccess(entity.level, this.rand, pos, state)) {
						growable.performBonemeal((ServerWorld) entity.level, this.rand, pos, state);
						entity.level.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 0);
					}
				}
			});
		}
	}

}
