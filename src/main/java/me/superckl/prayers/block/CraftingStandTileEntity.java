package me.superckl.prayers.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import lombok.Getter;
import me.superckl.prayers.init.ModParticles;
import me.superckl.prayers.init.ModTiles;
import me.superckl.prayers.inventory.InteractableInventoryTileEntity;
import me.superckl.prayers.recipe.AbstractAltarCraftingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ISidedInventory;
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

public class CraftingStandTileEntity extends InteractableInventoryTileEntity implements ISidedInventory, ITickableTileEntity{

	public static final String CRAFTING_STAND_KEY = "crafting_stand";

	public static final Reference2IntMap<Direction> dirToSlot = Util.make(new Reference2IntArrayMap<Direction>(5), map -> {
		Direction.Plane.HORIZONTAL.forEach(dir -> map.put(dir, dir.get2DDataValue()));
		map.put(Direction.UP, 4);
	});

	public static final Int2ReferenceMap<Direction> slotToDir = Util.make(new Int2ReferenceArrayMap<Direction>(5), map -> {
		CraftingStandTileEntity.dirToSlot.keySet().forEach(dir -> map.put(CraftingStandTileEntity.dirToSlot.getInt(dir), dir));
	});

	private final Random rand = new Random();

	@Getter
	private AbstractAltarCraftingRecipe activeRecipe;
	private int[] recipeMapping;
	private float consumedPoints;
	private int lastPercentage;
	private boolean inventoryChanged = true;

	public CraftingStandTileEntity() {
		super(ModTiles.CRAFTING_STAND.get(), 5);
	}

	public ActionResultType onActivate(final PlayerEntity player, final Hand hand, final Direction dir) {
		if(player instanceof ServerPlayerEntity)
			this.syncToClientLight((ServerPlayerEntity) player);
		final int slot = CraftingStandTileEntity.dirToSlot.getInt(dir);
		return this.onInteract(player, hand, slot);
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
		this.activeRecipe = null;
		this.recipeMapping = null;
		this.lastPercentage = 0;
		if(clearPoints)
			this.consumedPoints = 0;
	}

	protected void tickCrafting() {
		if(this.level.isClientSide)
			return;
		this.findValidAltar().ifPresent(altar -> {
			if(!this.canRecipeOutput(this.activeRecipe))
				return; //no room to output, don't tick
			final float reqPoints = this.activeRecipe.getPoints();
			final float transfer = altar.getAltarType().getTransferRate();
			final float toTransfer = Math.min(transfer, reqPoints-this.consumedPoints);
			final float transferred = altar.removePoints(toTransfer);
			this.consumedPoints += transferred;

			for(final Direction dir:Direction.Plane.HORIZONTAL) {
				if(this.getItem(CraftingStandTileEntity.dirToSlot.getInt(dir)).isEmpty() || this.rand.nextFloat() >= 0.05F)
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
		});
	}

	protected void finishCrafting() {
		for(int i = 0; i < this.recipeMapping.length; i++)
			this.removeItem(i, this.activeRecipe.getIngredientCounts()[this.recipeMapping[i]]);
		final ItemStack output = this.getItem(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP));
		if(output.isEmpty())
			this.setItem(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP), this.activeRecipe.getResultItem().copy());
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
		final ItemStack output = this.getItem(CraftingStandTileEntity.dirToSlot.getInt(Direction.UP));
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

	public boolean willCraftingConsume(final int slot) {
		if(!this.isCrafting())
			return false;
		final ItemStack stack = this.getItem(slot);
		if(stack.isEmpty())
			return false;
		return stack.getCount() <= this.getActiveRecipe().getIngredientCounts()[this.recipeMapping[slot]];
	}

	@Override
	public void onSlotChange(final int slot, final boolean itemChanged) {
		this.inventoryChanged = true;
		super.onSlotChange(slot, itemChanged);
	}

	protected LazyOptional<AltarTileEntity> findValidAltar() {
		final TileEntity below = this.level.getBlockEntity(this.worldPosition.below());
		if(below != null && below instanceof AltarTileEntity) {
			final AltarTileEntity altar = (AltarTileEntity) below;
			return altar.canRegen() ? LazyOptional.of(() -> altar):LazyOptional.empty();
		}else
			return LazyOptional.empty();
	}

	public void updateRecipe(final boolean clearPoints){
		final List<AbstractAltarCraftingRecipe> recipes = this.level.getRecipeManager().getAllRecipesFor(AbstractAltarCraftingRecipe.TYPE);
		final List<ItemStack> inventory = this.items.subList(0, 4);
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
				if(this.getItem(i).getCount() < recipe.getIngredientCounts()[mapping[i]]) {
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

	@Override
	public boolean canPlaceItem(final int index, final ItemStack stack) {
		return CraftingStandTileEntity.slotToDir.get(index) != Direction.UP;
	}

	@Override
	public int[] getSlotsForFace(final Direction side) {
		if(CraftingStandTileEntity.dirToSlot.containsKey(side) && this.hasStand(side))
			return new int[] {CraftingStandTileEntity.dirToSlot.getInt(side)};
		else
			return new int[0];
	}

	public boolean hasOutputSlot() {
		return this.hasStand(Direction.UP);
	}

	public boolean hasStand(final Direction dir) {
		final BlockState state = this.getBlockState();
		return ((CraftingStandBlock)state.getBlock()).hasStand(state, dir);
	}

	@Override
	public boolean canPlaceItemThroughFace(final int index, final ItemStack itemStackIn, final Direction direction) {
		return direction != Direction.UP && Arrays.stream(this.getSlotsForFace(direction)).anyMatch(slot -> slot == index) && this.canPlaceItem(index, itemStackIn);
	}

	@Override
	public boolean canTakeItemThroughFace(final int index, final ItemStack stack, final Direction direction) {
		return Arrays.stream(this.getSlotsForFace(direction)).anyMatch(slot -> slot == index);
	}

	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		final CompoundNBT craft_data = new CompoundNBT();
		super.writeInventory(craft_data);
		craft_data.putFloat("points", this.consumedPoints);
		compound.put(CraftingStandTileEntity.CRAFTING_STAND_KEY, craft_data);

		return super.save(compound);
	}

	@Override
	public void load(final BlockState state, final CompoundNBT nbt) {
		final CompoundNBT craft_data = nbt.getCompound(CraftingStandTileEntity.CRAFTING_STAND_KEY);
		this.readInventory(craft_data);
		this.consumedPoints = craft_data.getFloat("points");
		super.load(state, nbt);
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
		if(this.activeRecipe != null)
			nbt.putString("recipe", this.activeRecipe.getId().toString());
		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		if(!this.level.isClientSide)
			return;
		this.updateRecipe(true);
		final CompoundNBT nbt = pkt.getTag();
		this.consumedPoints = nbt.getFloat("points");
		if(nbt.contains("recipe"))
			this.activeRecipe = (AbstractAltarCraftingRecipe) this.level.getRecipeManager().byKey(new ResourceLocation(nbt.getString("recipe"))).get();
	}

}
