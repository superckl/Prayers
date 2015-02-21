package me.superckl.prayers.common.entity.tile;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.altar.AltarRegistry;
import me.superckl.prayers.common.altar.crafting.TableCraftingHandler;
import me.superckl.prayers.common.event.OfferingTableCraftingEvent.Post;
import me.superckl.prayers.common.event.OfferingTableCraftingEvent.Pre;
import me.superckl.prayers.common.utility.BlockLocation;
import me.superckl.prayers.common.utility.NumberHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityOfferingTable extends TileEntity implements ISidedInventory{

	@Getter
	private ItemStack currentItem;
	private final List<ItemStack> tertiaryItems = new ArrayList<ItemStack>();
	@Getter
	private TableCraftingHandler currentRecipe;
	private boolean craftingLock;
	@Setter
	private BlockLocation masterLoc;
	@Setter
	private Altar altar;

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		super.readFromNBT(comp);
		if(comp.hasKey("currentItem"))
			this.currentItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("currentItem"));
		else
			this.currentItem = null;
		//TODO may be broken now
		if(comp.hasKey("currentRecipe"))
			this.currentRecipe = TableCraftingHandler.fromNBT(comp.getCompoundTag("currentRecipe"));
		else
			this.currentRecipe = null;
		if(comp.hasKey("masterLoc")){
			final int[] array = comp.getIntArray("masterLoc");
			this.masterLoc = new BlockLocation(array[0], array[1], array[2]);
		}else
			this.masterLoc = null;
		this.tertiaryItems.clear();
		final NBTTagList list = comp.getTagList("tertiaryItems", NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++)
			this.tertiaryItems.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
		if(comp.hasKey("altar")){
			this.altar = new Altar(this);
			this.altar.readFromNBT(comp.getCompoundTag("altar"));
		}else
			this.altar = null;
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		if(this.currentItem != null)
			comp.setTag("currentItem", this.currentItem.writeToNBT(new NBTTagCompound()));
		if(this.currentRecipe != null)
			comp.setTag("currentRecipe", this.currentRecipe.toNBT(new NBTTagCompound()));
		if(this.masterLoc != null)
			comp.setIntArray("masterLoc", new int[] {this.masterLoc.getX(), this.masterLoc.getY(), this.masterLoc.getZ()});
		final NBTTagList list = new NBTTagList();
		for(final ItemStack stack:this.tertiaryItems)
			list.appendTag(stack.writeToNBT(new NBTTagCompound()));
		comp.setTag("tertiaryItems", list);
		if(this.altar != null){
			final NBTTagCompound tag = new NBTTagCompound();
			this.altar.writeToNBT(tag);
			comp.setTag("altar", tag);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound comp = new NBTTagCompound();
		this.writeToNBT(comp);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, comp);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(this.isMaster())
			this.altar.updateEntity(this.worldObj);
		this.handleCrafting();
	}

	private void handleCrafting(){
		if(this.getAltar() == null)
			return;
		if((this.currentRecipe != null) && !this.craftingLock){
			if(this.currentRecipe.isComplete(this)){
				final Post post = new Post(this, this.currentRecipe);
				if(MinecraftForge.EVENT_BUS.post(post)){
					this.currentRecipe = null;
					this.craftingLock = true;
					return;
					//TODO effect
				}
				//this.tertiaryItems.clear();
				//final ItemStack result = post.getCraftingResult().copy();
				//this.currentItem = result == null ? null:result.copy();
				this.currentRecipe.onPostComplete(this);
				this.currentRecipe = null;
				//TODO effect
			}else if(this.currentRecipe.isCrafting(this))
				this.currentRecipe.handleUpdate(this);
			else if(this.currentRecipe.areAdditionalRequirementsMet(this)){
				final Pre pre = new Pre(this, this.currentRecipe);
				if(MinecraftForge.EVENT_BUS.post(pre)){
					this.craftingLock = true;
					return;
				}
				this.currentRecipe.beginCrafting(this);
			}
		}else if((this.worldObj.getWorldTime() % 60) == 0)
			this.findRecipe();
	}

	public boolean isCrafting(){
		return (this.getAltar() != null) && this.getAltar().isActivated() && this.getCurrentRecipe().isCrafting(this);
	}

	public Altar getAltar(){
		TileEntityOfferingTable master;
		if(this.isMaster())
			return this.altar;
		else if((master = this.getMaster()) != null)
			return master.getAltar();
		return null;
	}

	public boolean isMaster(){
		return this.altar != null;
	}

	public TileEntityOfferingTable getMaster(){
		if(this.masterLoc != null){
			final TileEntity te = this.masterLoc.getTileEntity(this.worldObj);
			if((te != null) && (te instanceof TileEntityOfferingTable))
				return (TileEntityOfferingTable) te;
		}
		return null;
	}

	public void onStructureInvalidated(){
		this.altar = null;
		this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public void findRecipe(){
		if(this.currentItem == null)
			return;
		for(final TableCraftingHandler handler:AltarRegistry.getRegisteredRecipes())
			if(handler.areBaseRequirementsMet(this)){
				this.currentRecipe = handler.copy();
				this.craftingLock = false;
				break;
			}
	}

	public void onIngredientsModified(){
		if((this.currentRecipe != null) && this.currentRecipe.isCrafting(this)){
			//TODO effect
		}
		this.currentRecipe = null;
	}

	public void setCurrentItem(final ItemStack stack, final EntityPlayer player){
		this.currentItem = stack;
		this.onIngredientsModified();
	}

	public void addTertiaryIngredient(final ItemStack stack){
		if(stack == null)
			return;
		this.tertiaryItems.add(stack);
		this.onIngredientsModified();
	}

	public ItemStack removeTertiaryIngredient(){
		if(this.tertiaryItems.isEmpty())
			return null;
		final ItemStack stack = this.tertiaryItems.remove(this.tertiaryItems.size()-1);
		this.onIngredientsModified();
		return stack;
	}

	public List<ItemStack> removeAllTertiaryIngredients(){
		final List<ItemStack> list = new ArrayList<ItemStack>(this.tertiaryItems);
		this.tertiaryItems.clear();
		this.onIngredientsModified();
		return list;
	}

	public boolean hasTertiaryIngredients(){
		return !this.tertiaryItems.isEmpty();
	}

	public List<ItemStack> getTertiaryIngredients(){
		return new ArrayList<ItemStack>(this.tertiaryItems);
	}

	@Override
	public int getSizeInventory() {
		return Integer.MAX_VALUE;
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		if(slot <= 0)
			return this.currentItem;
		if(!this.hasTertiaryIngredients() || (slot > this.tertiaryItems.size()))
			return null;
		return this.tertiaryItems.get(slot-1);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amount) {
		if(slot <= 0){
			final ItemStack stack = this.currentItem;
			this.setCurrentItem(null, null);
			return stack;
		}
		if(!this.hasTertiaryIngredients())
			return null;
		if((slot-1) < this.tertiaryItems.size()){
			this.onIngredientsModified();
			return this.tertiaryItems.remove(slot-1);
		}
		return this.removeTertiaryIngredient();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return this.getStackInSlot(slot);
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		if(slot <= 0){
			this.setCurrentItem(stack, null);
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			return;
		}
		if(!this.hasTertiaryIngredients() || (slot > this.tertiaryItems.size())){
			this.addTertiaryIngredient(stack);
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			return;
		}
		this.tertiaryItems.set(slot-1, stack);
		this.onIngredientsModified();
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public String getInventoryName() {
		return StatCollector.translateToLocal("tile.prayers:offeringtable.name");
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(final int p_94041_1_, final ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(final int side) {
		return NumberHelper.fillIncreasing(this.tertiaryItems.size()+1);
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack stack, final int side) {
		return true;
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack p_102008_2_, final int side) {
		return true;
	}

}
