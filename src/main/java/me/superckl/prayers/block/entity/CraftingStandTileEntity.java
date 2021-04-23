package me.superckl.prayers.block.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import lombok.Getter;
import me.superckl.prayers.block.CraftingStandBlock;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.inventory.SlotMappedItemHandlerWrapper;
import me.superckl.prayers.recipe.AbstractAltarCraftingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class CraftingStandTileEntity extends InteractableInventoryTileEntity implements ITickableTileEntity{

	public static final String CRAFTING_STAND_KEY = "crafting_stand";

	public static final Reference2IntMap<Direction> dirToSlot = Util.make(new Reference2IntArrayMap<>(5), map -> {
		Direction.Plane.HORIZONTAL.forEach(dir -> map.put(dir, dir.get2DDataValue()));
		map.put(Direction.UP, 4);
	});

	public static final Int2ReferenceMap<Direction> slotToDir = Util.make(new Int2ReferenceArrayMap<>(5), map -> {
		CraftingStandTileEntity.dirToSlot.keySet().forEach(dir -> map.put(CraftingStandTileEntity.dirToSlot.getInt(dir), dir));
	});

	private final Random rand = new Random();

	@Getter
	private AbstractAltarCraftingRecipe activeRecipe;
	private int[] recipeMapping;
	private float consumedPoints;
	private int lastPercentage;
	private boolean inventoryChanged = true;

	private final ItemStackHandler itemHandler = new StandItemHandler();

	private final Map<Direction, IItemHandlerModifiable> mappedHandlers = Util.make(new EnumMap<>(Direction.class), map -> {
		CraftingStandTileEntity.dirToSlot.keySet().forEach(dir -> map.put(dir, new SlotMappedItemHandlerWrapper(this.itemHandler, CraftingStandTileEntity.dirToSlot.getInt(dir))));
	});

	public CraftingStandTileEntity() {
		super(ModTiles.CRAFTING_STAND.get());
	}

	public ActionResultType onActivate(final PlayerEntity player, final Hand hand, final Direction dir) {
		if(player instanceof ServerPlayerEntity)
			this.syncToClientLight((ServerPlayerEntity) player);
		final int slot = CraftingStandTileEntity.dirToSlot.getInt(dir);
		return this.onInteract(player, hand, this.itemHandler, slot);
	}

	@Override
	public void tick() {
		if(this.level.isClientSide)
			return;
		if(this.inventoryChanged) {
			this.inventoryChanged = false;
			this.updateRecipe(true);
			this.syncToClientLight(null);
		}
		if(this.activeRecipe != null)
			this.tickCrafting();
	}

	@Override
	public void onLoad() {
		this.inventoryChanged = false;
		this.updateRecipe(false);
	}

	public void clearRecipe(final boolean clearPoints) {
		final int signal = this.getRedstoneSignal();
		this.activeRecipe = null;
		this.recipeMapping = null;
		this.lastPercentage = 0;
		if(clearPoints)
			this.consumedPoints = 0;
		if(signal != this.getRedstoneSignal())
			this.level.updateNeighbourForOutputSignal(this.getBlockPos(), this.getBlockState().getBlock());
	}

	protected void tickCrafting() {
		if(this.level.isClientSide || this.level.hasNeighborSignal(this.worldPosition))
			return;
		this.findValidAltar().ifPresent(altar -> {
			if(!this.canRecipeOutput(this.activeRecipe))
				return; //no room to output, don't tick
			final int signal = this.getRedstoneSignal();
			final double reqPoints = this.activeRecipe.getPoints();
			final double transfer = altar.getAltarType().getTransferRate();
			final double toTransfer = Math.min(transfer, reqPoints-this.consumedPoints);
			final double transferred = altar.removePoints(toTransfer);
			this.consumedPoints += transferred;

			for(final Direction dir:Direction.Plane.HORIZONTAL) {
				if(this.itemHandler.getStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(dir)).isEmpty() || this.rand.nextFloat() >= 0.05F)
					continue;
				final Vector3d centerPos = new Vector3d(this.worldPosition.getX()+0.5, this.worldPosition.getY()+3D/16, this.worldPosition.getZ()+0.5);
				final Vector3d itemPos = centerPos.add(5.5/16*dir.getStepX(), 0, 5.5/16*dir.getStepZ())
						.add((2*this.rand.nextDouble()-1)*.05, this.rand.nextDouble()*.05, (2*this.rand.nextDouble()-1)*.05);
				Vector3d toCenter = centerPos.subtract(itemPos);
				final double mag = toCenter.length();
				toCenter = toCenter.scale(1/mag);
				((ServerWorld)this.level).sendParticles(ModParticles.ITEM_SACRIFICE.get(), itemPos.x, itemPos.y, itemPos.z, 0, toCenter.x, toCenter.y, toCenter.z, mag/20);
			}

			final int lastPercentage = MathHelper.floor(this.getCraftingProgress()*100);
			if(lastPercentage > this.lastPercentage)
				this.syncToClientLight(null); //Sync to the client when the percentage changes for display
			this.lastPercentage = lastPercentage;
			if(this.consumedPoints >= reqPoints)
				this.finishCrafting();
			else if(signal != this.getRedstoneSignal())
				this.level.updateNeighbourForOutputSignal(this.getBlockPos(), this.getBlockState().getBlock());
		});
	}

	protected void finishCrafting() {
		for(int i = 0; i < this.recipeMapping.length; i++)
			this.itemHandler.extractItem(i, this.activeRecipe.getIngredientCounts()[this.recipeMapping[i]], false);
		final ItemStack output = this.itemHandler.getStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP));
		if(output.isEmpty())
			this.itemHandler.setStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP), this.activeRecipe.getResultItem().copy());
		else
			output.grow(this.activeRecipe.getResultItem().getCount());
		this.setChanged();
		this.clearRecipe(true);
		if(!this.level.isClientSide)
			((ServerWorld)this.level).playSound(null, this.worldPosition.getX()+0.5, this.worldPosition.getY(), this.worldPosition.getZ()+0.5, SoundEvents.TRIDENT_RETURN, SoundCategory.BLOCKS, 0.5F, 1);
	}

	protected boolean canRecipeOutput(final AbstractAltarCraftingRecipe recipe) {
		if(!this.hasOutputSlot())
			return false;
		final ItemStack output = this.itemHandler.getStackInSlot(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP));
		return output.isEmpty() || ItemStack.isSame(output, recipe.getResultItem()) && ItemStack.tagMatches(output, recipe.getResultItem()) && output.getCount()+recipe.getResultItem().getCount() < output.getMaxStackSize();
	}

	public boolean isCrafting() {
		return this.activeRecipe != null;
	}

	public float getCraftingProgress() {
		if(!this.isCrafting())
			return 0;
		return this.consumedPoints/this.activeRecipe.getPoints();
	}

	public int getRedstoneSignal() {
		return (int) (this.getCraftingProgress()*15);
	}

	public boolean willCraftingConsume(final int slot) {
		if(!this.isCrafting())
			return false;
		final ItemStack stack = this.itemHandler.getStackInSlot(slot);
		if(stack.isEmpty())
			return false;
		return stack.getCount() <= this.getActiveRecipe().getIngredientCounts()[this.recipeMapping[slot]];
	}

	@Override
	public void onSlotChange(final int slot) {
		this.inventoryChanged = true;
		super.onSlotChange(slot);
	}

	protected LazyOptional<AltarTileEntity> findValidAltar() {
		final TileEntity below = this.level.getBlockEntity(this.worldPosition.below());
		if(below instanceof AltarTileEntity) {
			final AltarTileEntity altar = (AltarTileEntity) below;
			return altar.canRegen() ? LazyOptional.of(() -> altar):LazyOptional.empty();
		}
		return LazyOptional.empty();
	}

	public void updateRecipe(final boolean clearPoints){
		final List<AbstractAltarCraftingRecipe> recipes = this.level.getRecipeManager().getAllRecipesFor(AbstractAltarCraftingRecipe.TYPE);
		final List<ItemStack> inventory = new ArrayList<>(4);
		for(int i = 0; i < 4; i++)
			inventory.add(i, this.itemHandler.getStackInSlot(i).copy());
		Pair<AbstractAltarCraftingRecipe, int[]> pair = null;
		for(final AbstractAltarCraftingRecipe recipe:recipes) {
			final int[] mapping = recipe.findMapping(inventory);
			if(mapping != null) {
				pair = Pair.of(recipe, mapping);
				break;
			}
		}
		if(pair != null) {
			final AbstractAltarCraftingRecipe recipe = pair.getKey();
			final int[] mapping = pair.getValue();
			boolean enoughItem = true;
			for (int i = 0; i < mapping.length; i++)
				if(this.itemHandler.getStackInSlot(i).getCount() < recipe.getIngredientCounts()[mapping[i]]) {
					enoughItem = false;
					break;
				}
			if(enoughItem) {
				if(this.activeRecipe != recipe) {
					this.activeRecipe = recipe;
					this.recipeMapping = mapping;
					if(clearPoints)
						this.consumedPoints = 0;
				}
			} else
				this.clearRecipe(clearPoints);
		} else
			this.clearRecipe(clearPoints);
	}

	public boolean hasOutputSlot() {
		return this.hasStand(Direction.UP);
	}

	public boolean hasStand(final Direction dir) {
		final BlockState state = this.getBlockState();
		return ((CraftingStandBlock)state.getBlock()).hasStand(state, dir);
	}

	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		final CompoundNBT craft_data = new CompoundNBT();
		craft_data.putFloat("points", this.consumedPoints);
		craft_data.put(InteractableInventoryTileEntity.INV_KEY, this.itemHandler.serializeNBT());
		compound.put(CraftingStandTileEntity.CRAFTING_STAND_KEY, craft_data);
		return super.save(compound);
	}

	@Override
	public void load(final BlockState state, final CompoundNBT nbt) {
		super.load(state, nbt);
		final CompoundNBT craft_data = nbt.getCompound(CraftingStandTileEntity.CRAFTING_STAND_KEY);
		this.consumedPoints = craft_data.getFloat("points");
		this.itemHandler.deserializeNBT(craft_data.getCompound(InteractableInventoryTileEntity.INV_KEY));
	}

	public void syncToClientLight(final ServerPlayerEntity player) {
		if(player == null)
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NO_RERENDER);
		else
			PacketDistributor.PLAYER.with(() -> player).send(this.getUpdatePacket());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		this.load(state, tag);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		if(this.level.isClientSide)
			return null;
		final CompoundNBT nbt = new CompoundNBT();
		nbt.putFloat("points", this.consumedPoints);
		if(this.activeRecipe != null) {
			nbt.putString("recipe", this.activeRecipe.getId().toString());
			nbt.putIntArray("mapping", this.recipeMapping);
		}
		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		if(!this.level.isClientSide)
			return;
		this.updateRecipe(true);
		final CompoundNBT nbt = pkt.getTag();
		this.consumedPoints = nbt.getFloat("points");
		if(nbt.contains("recipe")) {
			this.activeRecipe = (AbstractAltarCraftingRecipe) this.level.getRecipeManager().byKey(new ResourceLocation(nbt.getString("recipe"))).get();
			this.recipeMapping = nbt.getIntArray("mapping");
		}
	}

	@Override
	public IItemHandlerModifiable getItemHandlerForSide(final Direction side) {
		return this.mappedHandlers.get(side);
	}

	@Override
	public IItemHandlerModifiable getInternalItemHandler() {
		return this.itemHandler;
	}

	public class StandItemHandler extends ItemStackHandler{

		public StandItemHandler() {
			super(5);
		}

		@Override
		public boolean isItemValid(final int slot, final ItemStack stack) {
			final Direction dir = CraftingStandTileEntity.slotToDir.get(slot);
			return dir != Direction.UP && CraftingStandTileEntity.this.hasStand(dir);
		}

		@Override
		protected void onContentsChanged(final int slot) {
			CraftingStandTileEntity.this.onSlotChange(slot);
		}

	}

}
