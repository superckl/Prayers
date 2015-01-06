package me.superckl.prayers.common.handler;

import me.superckl.prayers.common.entity.item.EntityCleaningDirtyBone;
import me.superckl.prayers.common.entity.prop.PrayerExtendedProperties;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import me.superckl.prayers.common.utility.PlayerHelper;
import me.superckl.prayers.common.utility.PotionEffectHashMap;
import me.superckl.prayers.common.utility.PrayerHelper;
import me.superckl.prayers.network.MessageUpdatePrayers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityConstruction(final EntityConstructing e){
		if(e.entity instanceof EntityPlayer)
			e.entity.registerExtendedProperties("prayer", new PrayerExtendedProperties());
	}

	@SubscribeEvent
	public void onEntitySpawn(final EntityJoinWorldEvent e){
		if((e.entity instanceof EntityLivingBase) == false)
			return;
		PSReflectionHelper.setPrivateFinalValue(EntityLivingBase.class, (EntityLivingBase) e.entity, new PotionEffectHashMap((EntityLivingBase) e.entity, ((EntityLivingBase)e.entity).activePotionsMap), "activePotionsMap", "field_70713_bf");
		if(!e.world.isRemote && (e.entity instanceof EntityPlayerMP))
			ModData.PRAYER_UPDATE_CHANNEL.sendTo(new MessageUpdatePrayers(PrayerHelper.getActivePrayers((EntityLivingBase) e.entity)), (EntityPlayerMP) e.entity);
	}

	@SubscribeEvent
	public void onLivingHurt(final LivingHurtEvent e){
		if((e.source.getSourceOfDamage() != null)){
			final EntityLivingBase shooter = PlayerHelper.getShooter(e.source.getSourceOfDamage());
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
		if(e.entityLiving instanceof EntityPlayer){
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) e.entityLiving.getExtendedProperties("prayer");
			for(final EnumPrayers prayer:prop.getActivePrayers())
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
					e.ammount*=.9;
					break;
				}
				case ENCHANCE_DEFENCE_3:
				{
					e.ammount*=.85;
					break;
				}
				case ENCHANCE_DEFENCE_4:
				{
					e.ammount*=.75;
					break;
				}
				default:
					break;
				}
		}
	}

	@SubscribeEvent
	public void onLivingDeath(final LivingDropsEvent e){
		if(e.entityLiving.getRNG().nextInt(4) == 0)
			return;
		if((e.entityLiving.width*e.entityLiving.height) < 2.0F)
			e.drops.add(new EntityItem(e.entityLiving.worldObj, e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, new ItemStack(ModItems.basicBone, e.lootingLevel+1, e.entityLiving.getRNG().nextInt(50) == 0 ? 2:0)));
		else
			e.drops.add(new EntityItem(e.entityLiving.worldObj, e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, new ItemStack(ModItems.basicBone, e.lootingLevel+1, e.entityLiving.getRNG().nextInt(50) == 0 ? 2:1)));
	}

	@SubscribeEvent
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

	/*@SubscribeEvent
	public void onPlayerInteract(final PlayerInteractEvent e){
		if(e.action == Action.RIGHT_CLICK_BLOCK){
			final IPrayerAltar altar = PrayerHelper.findAltar(e.world, e.x, e.y, e.z);
			if((altar == null) || !altar.canBlessWater() || !altar.canBlessInstantly() || (e.entityPlayer.getHeldItem() == null) || (e.entityPlayer.getHeldItem().getItem() != Items.potionitem) || (e.entityPlayer.getHeldItem().getItemDamage() != 0) || (e.entityPlayer.getHeldItem().stackSize <= 0))
				return;
			final PrayerExtendedProperties prop = (PrayerExtendedProperties) e.entityPlayer.getExtendedProperties("prayer");
			if(prop.getPrayerLevel() < 20){
				if(!e.world.isRemote)
					ChatHelper.sendFormattedDoubleMessage(e.entityPlayer, "msg.whisper.text", ChatHelper.createTranslatedChatWithStyle("msg.lvltoolow:water.text", new ChatStyle().setItalic(false)), new ChatStyle().setColor(EnumChatFormatting.RED).setItalic(true));
				return;
			}
			final ItemStack filledBottle = new ItemStack(ModItems.bottle);
			ModItems.bottle.fill(filledBottle, new FluidStack(ModFluids.holyWater, FluidContainerRegistry.BUCKET_VOLUME/4), true);
			if(e.entityPlayer.getHeldItem().stackSize == 1)
				e.entityPlayer.inventory.setInventorySlotContents(e.entityPlayer.inventory.currentItem, filledBottle);
			else if(e.entityPlayer.getHeldItem().stackSize > 1)
				e.entityPlayer.inventory.addItemStackToInventory(filledBottle);
		}
	}*/

}
