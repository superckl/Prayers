package me.superckl.prayers.common.handler;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.altar.AltarRegistry;
import me.superckl.prayers.common.entity.EntityUndeadWizardPriest;
import me.superckl.prayers.common.entity.item.EntityCleaningDirtyBone;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.reference.ModAchievements;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.utility.BlockLocation;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import me.superckl.prayers.common.utility.PlayerHelper;
import me.superckl.prayers.common.utility.PotionEffectHashMap;
import me.superckl.prayers.common.utility.PrayerHelper;
import me.superckl.prayers.common.utility.PrayersWorldSaveData;
import me.superckl.prayers.network.MessageUpdatePrayers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityConstruction(final EntityConstructing e){
		if(e.entity instanceof EntityPlayer)
			e.entity.registerExtendedProperties("prayer", new PrayerExtendedProperties());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(final EntityJoinWorldEvent e){
		if((e.entity instanceof EntityLivingBase) == false)
			return;
		PSReflectionHelper.setPrivateFinalValue(EntityLivingBase.class, (EntityLivingBase) e.entity, new PotionEffectHashMap((EntityLivingBase) e.entity, ((EntityLivingBase)e.entity).activePotionsMap), "activePotionsMap", "field_70713_bf");
		if(!e.world.isRemote && (e.entity instanceof EntityPlayerMP)){
			final NBTTagCompound comp = new NBTTagCompound();
			((PrayerExtendedProperties)((EntityPlayer)e.entity).getExtendedProperties("prayer")).saveNBTData(comp);
			ModData.PRAYER_UPDATE_CHANNEL.sendTo(new MessageUpdatePrayers(comp), (EntityPlayerMP) e.entity);
		}
		if(!e.world.isRemote && (e.entity instanceof EntityPlayer) && this.hasDataForPlayer((EntityPlayer) e.entity)){
			final PrayerExtendedProperties prop = ((PrayerExtendedProperties)((EntityPlayer)e.entity).getExtendedProperties("prayer"));
			prop.readFromNBT(this.getPlayerData((EntityPlayer) e.entity, ((EntityPlayer)e.entity).getHealth() > 0));
		}
	}

	//This only only called on the client when we want it... We're going to have to send a packet to implement this
	/*@SubscribeEvent(receiveCanceled = false)
	public void onPlayerRightClick(final PlayerInteractEvent e){
		LogHelper.info("Called");
		if((e.action != Action.RIGHT_CLICK_BLOCK) || e.entityPlayer.isSneaking() || (e.entityPlayer.getHeldItem() != null) || (!Prayers.getInstance().getConfig().isRechargeEverywhere()) ||(e.world.getBlock(e.x, e.y, e.z) == ModBlocks.offeringTable))
			return;
		LogHelper.info("Trying to find altar...");
		final PrayerExtendedProperties prop = (PrayerExtendedProperties) e.entityPlayer.getExtendedProperties("prayer");
		final float diff = prop.getMaxPrayerPoints()-prop.getPrayerPoints();
		if(diff <= 0)
			return;
		final Altar altar = AltarRegistry.findAltarAt(e.world, e.x, e.y, e.z);
		if(altar == null)
			return;
		LogHelper.info("found altar");
		float toRecharge = altar.onRechargePlayer(diff, e.entityPlayer, false);
		if(toRecharge <= 0)
			return;
		toRecharge = altar.onRechargePlayer(diff, e.entityPlayer, true);
		prop.setPrayerPoints(prop.getPrayerPoints()+toRecharge);
		e.setCanceled(true);
	}*/

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent e){
		if((e.source.getEntity() != null)){
			final EntityLivingBase shooter = PlayerHelper.getShooter(e.source);
			if(shooter != null){
				if(e.source.isMagicDamage()) //Compute it if projectile for now, mod compat later
					e.ammount = PrayerHelper.handleEnhanceMagic(e.ammount, PrayerHelper.getActivePrayers(shooter));
				else if (e.source.isProjectile())
					e.ammount = PrayerHelper.handleEnhanceRange(e.ammount, PrayerHelper.getActivePrayers(shooter));
			}else if(e.source.getSourceOfDamage() instanceof EntityLivingBase)
				e.ammount = PrayerHelper.handleEnhanceMelee(e.ammount, PrayerHelper.getActivePrayers((EntityLivingBase) e.source.getSourceOfDamage()));
		}
		if(e.source.isUnblockable())
			return;
		for(final EnumPrayers prayer:PrayerHelper.getActivePrayers(e.entityLiving))
			switch(prayer){
			case PROTECT_MAGIC:
			{
				if(e.source.isMagicDamage())
					e.ammount /= 2F;
				break;
			}
			case PROTECT_RANGE:
			{
				if(e.source.isProjectile() && !e.source.isMagicDamage())
					e.ammount /= 2F;
				break;
			}
			case PROTECT_MELEE:
			{
				if((e.source.getSourceOfDamage() != null) && (e.source.getSourceOfDamage() instanceof EntityLivingBase))
					e.ammount /= 2F;
				break;
			}
			case ENCHANCE_DEFENCE_1:
			{
				e.ammount*=.95;
				break;
			}
			case ENCHANCE_DEFENCE_2:
			{
				e.ammount*=.85;
				break;
			}
			case ENCHANCE_DEFENCE_3:
			{
				e.ammount*=.75;
				break;
			}
			default:
				break;
			}
	}

	@SubscribeEvent
	public void onLivingDrops(final LivingDropsEvent e){
		if(e.entityLiving.getRNG().nextInt(4) == 0)
			return;
		if((e.entityLiving.width*e.entityLiving.height) < 2.0F)
			e.drops.add(new EntityItem(e.entityLiving.worldObj, e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, new ItemStack(ModItems.basicBone, e.lootingLevel+1, e.entityLiving.getRNG().nextInt(100) == 0 ? 2:0)));
		else
			e.drops.add(new EntityItem(e.entityLiving.worldObj, e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, new ItemStack(ModItems.basicBone, e.lootingLevel+1, e.entityLiving.getRNG().nextInt(100) == 0 ? 2:1)));
	}

	@SubscribeEvent(receiveCanceled = false, priority = EventPriority.LOW)
	public void onItemPickup(final EntityItemPickupEvent e){
		if(e.item instanceof EntityCleaningDirtyBone){
			final EntityCleaningDirtyBone ent = (EntityCleaningDirtyBone) e.item;
			final ItemStack stack = ent.getEntityItem();
			if((stack.getItem() != ModItems.basicBone) || (stack.getItemDamage() != 2))
				return;
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			final NBTTagCompound comp = stack.getTagCompound();
			comp.setInteger("progress", ent.getProgress());
		}
	}

	@SubscribeEvent(receiveCanceled = false, priority = EventPriority.LOWEST)
	public void onLivingDeath(final LivingDeathEvent e){
		if((e.entityLiving instanceof EntityPlayer)){
			final PrayerExtendedProperties prop = ((PrayerExtendedProperties)((EntityPlayer)e.entityLiving).getExtendedProperties("prayer"));
			prop.disableAllPrayers(false);
			this.storePlayerData((EntityPlayer) e.entityLiving, prop.writeToNBT());
		}
		else if(!e.entity.worldObj.isRemote && (e.entityLiving instanceof EntityUndeadWizardPriest) && e.entityLiving.getEntityData().getBoolean("ritualSpawn") && (e.source.getSourceOfDamage() != null) && (e.source.getSourceOfDamage() instanceof EntityPlayer)){
			final EntityPlayer player = (EntityPlayer) e.source.getSourceOfDamage();
			player.addStat(ModAchievements.ANCIENTS_WRATH, 1);
		}
	}

	@SubscribeEvent(receiveCanceled = false, priority = EventPriority.LOWEST)
	public void onBlockBreak(final BlockEvent.BreakEvent e){
		final Iterator<WeakReference<Altar>> it = AltarRegistry.getLoadedAltars().iterator();
		while(it.hasNext()){
			final WeakReference<Altar> wr = it.next();
			if(wr.get() == null){
				it.remove();
				continue;
			}
			final List<BlockLocation> blocks = wr.get().getBlocks();
			if(blocks == null)
				continue;
			final BlockLocation loc = new BlockLocation(e.x, e.y, e.z);
			if(blocks.contains(loc)){
				if(e.getPlayer() != null){
					final EnumSet<EnumPrayers> prayers = PrayerHelper.getActivePrayers(e.getPlayer());
					if(prayers.contains(EnumPrayers.DESTRUCTIVISM)){
						wr.get().invalidateStructure();
						return;
					}
				}
				e.setCanceled(true);
			}
		}
	}

	//Wrapper methods for map save

	public void storePlayerData(final EntityPlayer player, final NBTTagCompound compound){
		if(player.worldObj.isRemote)
			return;
		this.ensureMapStorage(player.worldObj);
		final PrayersWorldSaveData data = ((PrayersWorldSaveData)player.worldObj.mapStorage.loadData(PrayersWorldSaveData.class, "prayersextendedsave"));
		data.storePlayerData(player, compound);
	}

	public NBTTagCompound getPlayerData(final EntityPlayer player, final boolean remove){
		if(player.worldObj.isRemote)
			return null;
		this.ensureMapStorage(player.worldObj);
		final PrayersWorldSaveData data = (PrayersWorldSaveData) player.worldObj.mapStorage.loadData(PrayersWorldSaveData.class, "prayersextendedsave");
		return data.getPlayerData(player, remove);
	}

	public boolean hasDataForPlayer(final EntityPlayer player){
		if(player.worldObj.isRemote)
			return false;
		this.ensureMapStorage(player.worldObj);
		final PrayersWorldSaveData data = (PrayersWorldSaveData) player.worldObj.mapStorage.loadData(PrayersWorldSaveData.class, "prayersextendedsave");
		return data.hasDataForPlayer(player);
	}

	private void ensureMapStorage(final World world){
		if(world.mapStorage.loadData(PrayersWorldSaveData.class, "prayersextendedsave") == null)
			world.mapStorage.setData("prayersextendedsave", new PrayersWorldSaveData("prayersextendedsave"));
	}

}
