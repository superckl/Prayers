package me.superckl.prayers.common.entity.tile;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.prayer.Altar;
import me.superckl.prayers.common.prayer.AltarRegistry;
import me.superckl.prayers.common.prayer.OfferingTableCraftingHandler;
import me.superckl.prayers.common.reference.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityOfferingTable extends TileEntity{

	@Getter
	private ItemStack currentItem;
	private final List<ItemStack> tertiaryItems = new ArrayList<ItemStack>();
	@Getter
	private OfferingTableCraftingHandler currentRecipe;
	private int[] masterLoc;
	@Setter
	private Altar altar;
	@Getter
	private int waterTimer = 200;

	@Override
	public void readFromNBT(final NBTTagCompound comp) {
		super.readFromNBT(comp);
		if(comp.hasKey("currentItem"))
			this.currentItem = ItemStack.loadItemStackFromNBT(comp.getCompoundTag("currentItem"));
		else
			this.currentItem = null;
		final NBTTagList list = new NBTTagList();
		for(final ItemStack stack:this.tertiaryItems)
			list.appendTag(stack.writeToNBT(new NBTTagCompound()));
		comp.setTag("tertiaryItems", list);
		if(comp.hasKey("altar")){
			this.altar = new Altar(this);
			this.altar.readFromNBT(comp.getCompoundTag("altar"));
		}else
			this.altar = null;
		this.waterTimer = comp.getInteger("waterTimer");
	}

	@Override
	public void writeToNBT(final NBTTagCompound comp) {
		super.writeToNBT(comp);
		if(this.currentItem != null)
			comp.setTag("currentItem", this.currentItem.writeToNBT(new NBTTagCompound()));
		final NBTTagList list = comp.getTagList("tertiaryItems", NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++)
			this.tertiaryItems.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
		if(this.altar != null){
			final NBTTagCompound tag = new NBTTagCompound();
			this.altar.writeToNBT(tag);
			comp.setTag("altar", tag);
		}
		comp.setInteger("waterTimer", this.waterTimer);
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
		if(this.currentRecipe != null){
			if(this.currentRecipe.isComplete(this)){
				this.tertiaryItems.clear();
				this.currentItem = this.currentRecipe.getResult().copy();
				this.currentRecipe = null;
				//TODO effect
			}else if(this.currentRecipe.isCrafting(this))
				this.currentRecipe.handleUpdate(this);
			else if(this.currentRecipe.areAdditionalRequirementsMet(this))
				this.currentRecipe.beginCrafting(this);
		}else if((this.worldObj.getWorldTime() % 60) == 0)
			this.findRecipe();
	}

	/*private void manageWaterBless(){
		Altar altar;
		if((this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && ((altar = this.getAltar()) != null) && altar.isActivated()){
			if(altar.getPrayerPoints() >= 0.5F){
				this.waterTimer--;
				altar.setPrayerPoints(altar.getPrayerPoints()-0.5F);
			}
			if(this.waterTimer <= 0){
				this.waterTimer = 200;
				this.currentItem = ModFluids.filledHolyBottle();
			}
		}
	}*/

	public boolean isBlessingWater(){
		return (this.getAltar() != null) && this.getAltar().isActivated() && (this.currentItem != null) && (this.currentItem.getItem() == Items.potionitem) && (this.currentItem.getItemDamage() == 0) && (this.getAltar().getPrayerPoints() >= 0.5F) && (this.waterTimer > 0);
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
			final TileEntity te = this.worldObj.getTileEntity(this.masterLoc[0], this.masterLoc[1], this.masterLoc[2]);
			if((te != null) && (te instanceof TileEntityOfferingTable))
				return (TileEntityOfferingTable) te;
		}
		return null;
	}

	public void onStructureInvalidated(){
		this.waterTimer = 200;
		this.altar = null;
		this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public void findRecipe(){
		if(this.currentItem == null)
			return;
		for(final OfferingTableCraftingHandler handler:AltarRegistry.getRegisteredRecipes())
			if(handler.checkCompletion(this.currentItem, this.tertiaryItems)){
				this.currentRecipe = handler.clone();
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
		final Altar altar = this.getAltar();
		if((player != null) && !this.worldObj.isRemote && (altar != null) && altar.isInRitual() && (stack != null) && (stack.getItem() == ModItems.basicBone) && (stack.getItemDamage() == 3))
			altar.getContributors().put(player.getGameProfile().getId(), false);
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

}
