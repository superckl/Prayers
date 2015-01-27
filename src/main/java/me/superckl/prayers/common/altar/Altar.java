package me.superckl.prayers.common.altar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.common.altar.multi.BlockRequirement;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.reference.ModAchievements;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.utility.BlockLocation;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PlayerHelper;
import me.superckl.prayers.common.utility.PrayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Altar{

	/**
	 * Only to be used on the server side.
	 */
	private final Random random = new Random();
	@Getter
	@Setter
	private boolean activated;
	@Getter
	private boolean inRitual;
	@Getter
	@Setter
	private int ritualTimer = 72000; //3 day-night cycles
	@Getter
	@Setter
	private float prayerPoints = 500F;
	@Getter
	@Setter
	private int baseRechargeDelay = Prayers.getInstance().getConfig().getTier1RechargeDelay();
	@Getter
	@Setter
	private float baseRechargeRate = Prayers.getInstance().getConfig().getTier1RechargeRate();
	@Getter
	@Setter
	private float maxPrayerPoints = Prayers.getInstance().getConfig().getTier1Max();
	@Getter
	private List<BlockLocation> blocks;
	@Getter
	private List<TileEntityOfferingTable> tables;
	private boolean isRegistered;
	@Getter
	private TileEntityOfferingTable holder;
	@Getter
	private final Map<UUID, Boolean> contributors = new HashMap<UUID, Boolean>();

	public Altar(@NonNull final TileEntityOfferingTable holder) {
		this.holder = holder;
		AltarRegistry.getLoadedAltars().add(new WeakReference<Altar>(this));
	}

	public void readFromNBT(final NBTTagCompound comp) {
		this.activated = comp.getBoolean("altarActivated");
		this.maxPrayerPoints = comp.getFloat("maxPrayerPoints");
		this.baseRechargeRate = comp.getFloat("baseRechargeRate");
		this.baseRechargeDelay = comp.getInteger("baseRechargeDelay");
		this.prayerPoints = comp.getFloat("prayerPoints");
		this.inRitual = comp.getBoolean("inRitual");
		this.ritualTimer = comp.getInteger("ritualTimer");
		if(comp.hasKey("blocks")){
			final int[] coords = comp.getIntArray("blocks");
			this.blocks = new ArrayList<BlockLocation>();
			for(int i = 0; i < coords.length;)
				this.blocks.add(new BlockLocation(coords[i++], coords[i++], coords[i++]));
		}
		final NBTTagList list = comp.getTagList("contributors", NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++){
			final NBTTagCompound entry = list.getCompoundTagAt(i);
			this.contributors.put(UUID.fromString(entry.getString("name")), entry.getBoolean("rewarded"));
		}
	}

	public void writeToNBT(final NBTTagCompound comp) {
		comp.setBoolean("altarActivated", this.activated);
		comp.setFloat("maxPrayerPoints", this.maxPrayerPoints);
		comp.setFloat("baseRechargeRate", this.baseRechargeRate);
		comp.setInteger("baseRechargeDelay", this.baseRechargeDelay);
		comp.setFloat("prayerPoints", this.prayerPoints);
		comp.setBoolean("inRitual", this.inRitual);
		comp.setInteger("ritualTimer", this.ritualTimer);
		if(this.blocks != null){
			final int[] coords = new int[this.blocks.size()*3];
			int i = 0;
			for(final BlockLocation vec:this.blocks){
				coords[i++] = vec.getX();
				coords[i++] = vec.getY();
				coords[i++] = vec.getZ();
			}
			comp.setIntArray("blocks", coords);
		}
		final NBTTagList list = new NBTTagList();
		for(final Entry<UUID, Boolean> entry:this.contributors.entrySet()){
			final NBTTagCompound nbtEntry = new NBTTagCompound();
			nbtEntry.setString("name", entry.getKey().toString());
			nbtEntry.setBoolean("rewarded", entry.getValue());
		}
		comp.setTag("contributors", list);
	}

	private int regenTimer = 200;

	public void updateEntity(final World world) {
		if(!this.isRegistered && !world.isRemote)
			MinecraftForge.EVENT_BUS.register(this);
		if(this.activated && (this.getPrayerPoints() < this.getMaxPrayerPoints())){
			this.regenTimer--;
			if(this.regenTimer <= 0){
				this.regenTimer = this.baseRechargeDelay;
				this.prayerPoints += this.baseRechargeRate;
				if(this.prayerPoints > this.getMaxPrayerPoints())
					this.prayerPoints = this.getMaxPrayerPoints();
			}
		}else
			this.regenTimer = this.baseRechargeDelay;
		if(!world.isRemote)
			if(this.inRitual)
				this.manageRitual(world);
			else if((this.tables != null) && !this.activated){
				boolean allSatisfied = true;
				for(final TileEntityOfferingTable table:this.tables)
					if(!((table.getCurrentItem() != null) && (table.getCurrentItem().getItem() == ModItems.basicBone) && (table.getCurrentItem().getItemDamage() == 3))){
						allSatisfied = false;
						break;
					}
				if(allSatisfied)
					this.startRitual(world);
			}
	}

	/**
	 * Only to be called on the server side.
	 *
	 * This client actually has no idea what the ritual timer is at any given moment. The timer that appears in WAILA is handled by a special method in WAILA.
	 */
	private void manageRitual(final World world){
		if(world.isRemote)
			return;
		if(this.activated){
			this.inRitual = false;
			world.markBlockForUpdate(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
			return;
		}
		if(this.ritualTimer <= 0){
			this.activated = true;
			this.inRitual = false;
			this.holder.getWorldObj().markBlockForUpdate(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
			this.holder.getWorldObj().spawnEntityInWorld(new EntityLightningBolt(this.holder.getWorldObj(), this.holder.xCoord+0.5D, this.holder.yCoord+0.5D, this.holder.zCoord+0.5D));
			//TODO effects...
			for(final Entry<UUID, Boolean> entry:this.contributors.entrySet())
				if(!entry.getValue()){
					final EntityPlayerMP player = PlayerHelper.getPlayer(entry.getKey());
					if(player != null){
						player.addStat(ModAchievements.SUCCESS, 1);
						entry.setValue(true);
					}
				}
			return;
		}
		this.ritualTimer--;
		if(this.tables != null){
			final int tempTimer = this.ritualTimer;
			boolean allSatisfied = true;
			for(final TileEntityOfferingTable table:this.tables)
				if((table.getCurrentItem() != null) && (table.getCurrentItem().getItem() == ModItems.basicBone) && (table.getCurrentItem().getItemDamage() == 3)){
					if(table.getCurrentItem().hasTagCompound())
						if(table.getCurrentItem().getTagCompound().getBoolean("soaked")){
							this.ritualTimer -= this.random.nextInt(2);
							if(this.random.nextInt(2400) == 0){
								table.getCurrentItem().setTagCompound(null);
								table.getWorldObj().markBlockForUpdate(table.xCoord, table.yCoord, table.zCoord);
								table.getWorldObj().spawnParticle("largesmoke", table.xCoord+.5D, table.yCoord+1.2D, table.zCoord+.5D, 0D, 0D, 0D);
								table.getWorldObj().spawnParticle("largesmoke", table.xCoord+.5D, table.yCoord+.8D, table.zCoord+.5D, 0D, 0D, 0D);
								table.getWorldObj().playSoundEffect(table.xCoord + 0.5F, table.yCoord + 0.5F, table.zCoord + 0.5F, "random.fizz", 0.5F, 2.6F + ((this.random.nextFloat() - this.random.nextFloat()) * 0.8F));
							}
						}
					if(this.random.nextInt(7000) == 0){
						table.setCurrentItem(null, null);
						table.getWorldObj().markBlockForUpdate(table.xCoord, table.yCoord, table.zCoord);
						table.getWorldObj().playSoundEffect(table.xCoord+0.5F, table.yCoord+0.5F, table.zCoord+0.5F, "mob.endermen.portal", 1.0F, 1.0F);
						for (int l = 0; l < 4; ++l)
						{
							final double d0 = table.xCoord + this.random.nextFloat();
							final double d1 = table.yCoord + this.random.nextFloat();
							final double d2 = table.zCoord + this.random.nextFloat();
							double d3 = 0.0D;
							double d4 = 0.0D;
							double d5 = 0.0D;
							final int i1 = (this.random.nextInt(2) * 2) - 1;
							d3 = (this.random.nextFloat() - 0.5D) * 0.5D;
							d4 = (this.random.nextFloat() - 0.5D) * 0.5D;
							d5 = (this.random.nextFloat() - 0.5D) * 0.5D;

							table.getWorldObj().spawnParticle("portal", d0, d1, d2, d3, d4, d5);
						}
					}
				}else
					allSatisfied = false;
			if(!allSatisfied)
				this.ritualTimer = tempTimer+1+(this.random.nextInt(9)/8);
		}
		if(this.ritualTimer >= 100000){
			this.inRitual = false;
			this.ritualTimer = 72000;
			this.holder.getWorldObj().markBlockForUpdate(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
			//TODO effect
		}
		//if((this.ritualTimer % 40) == 0)
		//	this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord); //TODO Meh... It only happens during the ritual I guess...
		//Fixed by WAILA getNBTTag method
	}

	public void startRitual(final World world){
		if(this.inRitual)
			return;
		this.inRitual = true;
		this.ritualTimer = 72000;
		this.holder.getWorldObj().markBlockForUpdate(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
		//TODO
	}

	/**
	 * Tries to determine the multiblock altar
	 * @return If the structure was determined succesfully.
	 */
	public boolean determineBlocks(final World world){
		this.tables = new ArrayList<TileEntityOfferingTable>();
		this.blocks = new ArrayList<BlockLocation>();
		final Map<BlockLocation, BlockRequirement> multi = this.tryFindTier1(world);
		if((multi != null) && this.establishStructure(multi)){
			this.holder.setAltar(this);
			MinecraftForge.EVENT_BUS.register(this);
			this.isRegistered = true;
			return true;
		}
		return false;
	}

	public float onRechargePlayer(float points, final EntityPlayer player, final boolean shouldSubtract) {
		if(!this.activated)
			return 0F;
		if(points > this.prayerPoints)
			points = this.prayerPoints;
		if(shouldSubtract){
			this.prayerPoints -= points;
			if(!player.worldObj.isRemote){
				player.addStat(ModAchievements.RECHARGED, 1);
				if((points >= this.maxPrayerPoints) && (this.prayerPoints <= 0))
					player.addStat(ModAchievements.TOO_OP, 1);
				final UUID uuid = player.getGameProfile().getId();
				if(this.contributors.containsKey(uuid)){
					player.addStat(ModAchievements.CONVENIENCE, 1);
					if(!this.contributors.get(uuid)){
						player.addStat(ModAchievements.SUCCESS, 1);
						this.contributors.put(uuid, true);
					}
				}
			}
		}
		return points;
	}

	public void invalidateStructure(){
		this.blocks = null;
		this.activated = false;
		this.inRitual = false;
		this.prayerPoints = 0F;
		this.holder.onStructureInvalidated();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent(receiveCanceled = false, priority = EventPriority.LOWEST)
	public void onBlockBreak(final BlockEvent.BreakEvent e){
		if(this.blocks == null)
			return;
		final BlockLocation loc = new BlockLocation(e.x, e.y, e.z);
		if(this.blocks.contains(loc)){
			if(e.getPlayer() != null){
				final List<EnumPrayers> prayers = PrayerHelper.getActivePrayers(e.getPlayer());
				if(prayers.contains(EnumPrayers.DESTRUCTIVISM)){
					this.invalidateStructure();
					return;
				}
			}
			e.setCanceled(true);
		}

	}

	private boolean establishStructure(final Map<BlockLocation, BlockRequirement> multi){
		final World world = this.holder.getWorldObj();
		final BlockLocation orig = new BlockLocation(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
		for(final Entry<BlockLocation, BlockRequirement> entry:multi.entrySet()){
			final BlockLocation loc = entry.getKey().add(orig);
			final Block block = loc.getBlock(world);
			if(!entry.getValue().isSatisfied(block)){
				this.blocks = null;
				this.tables = null;
				LogHelper.info(loc);
				return false;
			}
			this.blocks.add(loc);
			if(block == ModBlocks.offeringTable){
				final TileEntity te = loc.getTileEntity(world);
				if(te instanceof TileEntityOfferingTable){
					this.tables.add((TileEntityOfferingTable) te);
					if(te != this.holder)
						((TileEntityOfferingTable)te).setMasterLoc(orig);
				}
			}
		}
		return true;
	}

	private Map<BlockLocation, BlockRequirement> tryFindTier1(final World world){
		final ForgeDirection secondDir;
		BlockLocation loc = new BlockLocation(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
		final BlockLocation origLoc = loc;
		if((loc = origLoc.shift(ForgeDirection.NORTH)).getBlock(world) == ModBlocks.offeringTable)
			secondDir = ForgeDirection.NORTH;
		else if((loc = origLoc.shift(ForgeDirection.EAST)).getBlock(world) == ModBlocks.offeringTable)
			secondDir = ForgeDirection.EAST;
		else if((loc = origLoc.shift(ForgeDirection.SOUTH)).getBlock(world) == ModBlocks.offeringTable)
			secondDir = ForgeDirection.SOUTH;
		else if((loc = origLoc.shift(ForgeDirection.WEST)).getBlock(world) == ModBlocks.offeringTable)
			secondDir = ForgeDirection.WEST;
		else
			return null;
		final TileEntity te = loc.getTileEntity(world);
		if((te instanceof TileEntityOfferingTable) == false)
			return null;
		final BlockLocation teLoc = loc;
		ForgeDirection mainDir = secondDir.getRotation(ForgeDirection.DOWN);
		Block block = (loc = loc.shift(mainDir.getOpposite())).getBlock(world);
		if(block == Blocks.air){
			this.holder = (TileEntityOfferingTable) origLoc.shift(secondDir).getTileEntity(world);
			block = (loc = loc.shift(mainDir).shift(mainDir)).getBlock(world);
			mainDir = mainDir.getOpposite();
		}
		if((block instanceof BlockWall) && (block.getMaterial() == Material.rock)){
			this.maxPrayerPoints = 500F;
			this.baseRechargeDelay = 200;
			return AltarRegistry.getMultiBlock(1, mainDir);
		}
		return null;
	}

}
