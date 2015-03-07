package me.superckl.prayers.common.altar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.Prayers;
import me.superckl.prayers.api.AltarRegistry;
import me.superckl.prayers.common.altar.multi.BlockRequirement;
import me.superckl.prayers.common.entity.EntityUndeadWizardPriest;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.event.AltarEvent;
import me.superckl.prayers.common.event.AltarEvent.EstablishBlocks;
import me.superckl.prayers.common.event.AltarEvent.SearchForMultiblock.Post;
import me.superckl.prayers.common.reference.ModAchievements;
import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.utility.BlockLocation;
import me.superckl.prayers.common.utility.EntityHelper;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

@Getter
public class Altar{

	/**
	 * Only to be used on the server side.
	 */
	@Getter(AccessLevel.NONE)
	private final Random random = new Random();
	@Setter
	private boolean activated;
	private boolean inRitual;
	@Setter
	private int ritualTimer; //3 day-night cycles
	@Setter
	private float prayerPoints;
	@Setter
	private int baseRechargeDelay;
	@Setter
	private float baseRechargeRate;
	@Setter
	private float maxPrayerPoints;
	private int tier;
	private List<BlockLocation> blocks;
	private List<TileEntityOfferingTable> tables;
	private TileEntityOfferingTable holder;

	public Altar(final TileEntityOfferingTable holder) {
		this.holder = holder;
		AltarRegistry.getLoadedAltars().add(new WeakReference<Altar>(this));
	}

	public void readFromNBT(final NBTTagCompound comp) {
		this.activated = comp.getBoolean("altarActivated");
		this.maxPrayerPoints = comp.getFloat("maxPrayerPoints");
		this.baseRechargeRate = comp.getFloat("baseRechargeRate");
		this.baseRechargeDelay = comp.getInteger("baseRechargeDelay");
		this.tier = comp.getInteger("tier");
		this.prayerPoints = comp.getFloat("prayerPoints");
		this.inRitual = comp.getBoolean("inRitual");
		this.ritualTimer = comp.getInteger("ritualTimer");
		if(comp.hasKey("blocks")){
			final int[] coords = comp.getIntArray("blocks");
			this.blocks = new ArrayList<BlockLocation>();
			for(int i = 0; i < coords.length;)
				this.blocks.add(new BlockLocation(coords[i++], coords[i++], coords[i++]));
		}
	}

	public void writeToNBT(final NBTTagCompound comp) {
		comp.setBoolean("altarActivated", this.activated);
		comp.setFloat("maxPrayerPoints", this.maxPrayerPoints);
		comp.setFloat("baseRechargeRate", this.baseRechargeRate);
		comp.setInteger("baseRechargeDelay", this.baseRechargeDelay);
		comp.setInteger("tier", this.tier);
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
	}

	private int regenTimer = 200;

	public void updateEntity(final World world) {
		if((this.blocks != null) && (this.tables == null)){
			this.tables = new ArrayList<TileEntityOfferingTable>();
			TileEntity te;
			for(final BlockLocation loc:this.blocks)
				if((loc.getBlock(world) == ModBlocks.offeringTable) && ((te = loc.getTileEntity(world)) != null) && (te instanceof TileEntityOfferingTable))
					this.tables.add((TileEntityOfferingTable) te);
		}
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
			MinecraftForge.EVENT_BUS.post(new AltarEvent.ActivationRitualEvent.Post(this));
			//TODO effects...
			return;
		}
		this.ritualTimer--;
		int maxChance = 30000;
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
			if(!allSatisfied){
				this.ritualTimer = tempTimer+1+(this.random.nextInt(9)/8);
				maxChance -= 15000;
			}
		}
		if(this.ritualTimer >= 80000){
			if(MinecraftForge.EVENT_BUS.post(new AltarEvent.ActivationRitualEvent.Fail(this)))
				return;
			this.inRitual = false;
			this.ritualTimer = 0;
			this.holder.getWorldObj().markBlockForUpdate(this.holder.xCoord, this.holder.yCoord, this.holder.zCoord);
			for(final TileEntityOfferingTable te:this.tables){
				final BlockLocation loc = BlockLocation.fromTileEntity(te).add(0, 1, 0);
				final EntityLightningBolt lightning = new EntityLightningBolt(this.holder.getWorldObj(), loc.getX(), loc.getY(), loc.getZ());
				world.spawnEntityInWorld(lightning);
			}
			final BlockLocation loc = EntityHelper.tryFindSpawnLoc(EnumCreatureType.creature, this.holder.getWorldObj(), BlockLocation.fromTileEntity(this.holder), this.random);
			if(loc != null){
				final EntityUndeadWizardPriest priest = new EntityUndeadWizardPriest(this.holder.getWorldObj(), this.tier+Math.round(this.random.nextInt(3)*this.random.nextFloat()));
				priest.forceSpawn = true;
				priest.setLocationAndAngles(loc.getX(), loc.getY(), loc.getZ(), 0F, 0F);
				priest.getEntityData().setBoolean("ritualSpawn", true);
				this.holder.getWorldObj().spawnEntityInWorld(priest);
			}
			return;
			//TODO effect
		}

		if(this.random.nextInt(maxChance) == 0){
			final BlockLocation loc = EntityHelper.tryFindSpawnLoc(EnumCreatureType.creature, this.holder.getWorldObj(), BlockLocation.fromTileEntity(this.holder), this.random);
			if(loc != null){
				final EntityLightningBolt lightning = new EntityLightningBolt(this.holder.getWorldObj(), loc.getX(), loc.getY(), loc.getZ());
				world.spawnEntityInWorld(lightning);
				final EntityUndeadWizardPriest priest = new EntityUndeadWizardPriest(this.holder.getWorldObj(), this.tier+Math.round(this.random.nextInt(3)*this.random.nextFloat()));
				priest.setLocationAndAngles(loc.getX(), loc.getY(), loc.getZ(), 0F, 0F);
				priest.getEntityData().setBoolean("ritualSpawn", true);
				this.holder.getWorldObj().spawnEntityInWorld(priest);
			}
		}
	}

	public void startRitual(final World world){
		if(this.inRitual)
			return;
		MinecraftForge.EVENT_BUS.post(new AltarEvent.ActivationRitualEvent.Pre(this));
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
		if(MinecraftForge.EVENT_BUS.post(new AltarEvent.SearchForMultiblock.Pre(this)))
			return false;
		this.tables = new ArrayList<TileEntityOfferingTable>();
		this.blocks = new ArrayList<BlockLocation>();
		final int tier = 1;
		Map<BlockLocation, BlockRequirement> multi = this.tryFindTier1(world);
		if((multi != null)){
			final AltarEvent.SearchForMultiblock.Post event = new Post(this, tier, multi);
			if(MinecraftForge.EVENT_BUS.post(event))
				return false;
			multi = event.getMultiblock();
			if(!this.establishStructure(multi))
				return false;
			this.tier = tier;
			Prayers.getInstance().getConfig().setStats(this);
			this.holder.setAltar(this);
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
		final Iterator<WeakReference<Altar>> it = AltarRegistry.getLoadedAltars().iterator();
		while(it.hasNext()){
			final WeakReference<Altar> weakR = it.next();
			if(weakR.get() == null){
				it.remove();
				continue;
			}
			if(weakR.get() == this){
				it.remove();
				break;
			}
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
		final EstablishBlocks event = new EstablishBlocks(this, this.blocks, this.tables, multi);
		if(MinecraftForge.EVENT_BUS.post(event)){
			this.blocks = null;
			this.tables = null;
			return false;
		}
		this.blocks = event.getBlocks();
		this.tables = event.getTables();
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
		if((block instanceof BlockWall) && (block.getMaterial() == Material.rock))
			return AltarRegistry.getMultiBlock(1, mainDir);
		return null;
	}

}
