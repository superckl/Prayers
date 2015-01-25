package me.superckl.prayers.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import me.superckl.prayers.common.altar.Altar;
import me.superckl.prayers.common.altar.AltarRegistry;
import me.superckl.prayers.common.block.BlockOfferingTable;
import me.superckl.prayers.common.entity.tile.TileEntityOfferingTable;
import me.superckl.prayers.common.utility.DateHelper;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class PrayersWailaDataProvider implements IWailaDataProvider{

	public static final PrayersWailaDataProvider INSTANCE = new PrayersWailaDataProvider();

	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		if(!config.getConfig("showaltarinfo"))
			return currenttip;
		Altar altar = null;
		TileEntityOfferingTable te = null;
		if((accessor.getTileEntity() != null) && (accessor.getTileEntity() instanceof TileEntityOfferingTable)){
			te = (TileEntityOfferingTable) accessor.getTileEntity();
			altar = te.getAltar();
		}else if(accessor.getBlock() != null){
			final MovingObjectPosition pos = accessor.getPosition();
			altar = AltarRegistry.findAltarAt(accessor.getWorld(), pos.blockX, pos.blockY, pos.blockZ);
		}
		if(altar != null)
			if(altar.isActivated()){
				currenttip.add("Active");
				currenttip.add(StringHelper.build("Points: ", altar.getPrayerPoints(), "/", altar.getMaxPrayerPoints()));
			}else if(altar.isInRitual()){
				currenttip.add("Activating");
				if((accessor.getNBTData() != null) && accessor.getNBTData().hasKey("ritualTimer"))
					currenttip.add(StringHelper.build(EnumChatFormatting.RESET, "Time Left: ", (te != null) && (te.getCurrentItem() == null) ? EnumChatFormatting.OBFUSCATED:"", DateHelper.toDateString(accessor.getNBTData().getInteger("ritualTimer"))));
			}else
				currenttip.add("Inactive");
		if((te != null)){
			if(te.getCurrentItem() != null)
				currenttip.add("Current Item: "+te.getCurrentItem().getDisplayName());
			final List<ItemStack> items = te.getTertiaryIngredients();
			if(!items.isEmpty())
				if(accessor.getPlayer().isSneaking()){
					currenttip.add("Tertiary Items:");
					for(final ItemStack stack:items)
						currenttip.add(StringHelper.build("- ", stack.getDisplayName()));
				}else
					currenttip.add(StringHelper.build("Hold ", EnumChatFormatting.YELLOW, EnumChatFormatting.ITALIC, "Shift ",EnumChatFormatting.RESET, EnumChatFormatting.GRAY, "for more"));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te,
			final NBTTagCompound tag, final World world, final int x, final int y, final int z) {
		Altar altar = null;
		if((te != null) && (te instanceof TileEntityOfferingTable))
			altar = ((TileEntityOfferingTable)te).getAltar();
		/*final Altar altar = (Altar) te;
			if(altar.isInRitual() && !altar.isActivated())
				tag.setInteger("ritualTimer", altar.getRitualTimer());*/
		if(altar != null)
			if(altar.isInRitual() && !altar.isActivated())
				tag.setInteger("ritualTimer", altar.getRitualTimer());
		return tag;
	}

	public static void callbackRegister(final IWailaRegistrar registrar){
		registrar.addConfig("Prayers", "showaltarinfo", "Show Altar Info", true);
		registrar.registerBodyProvider(PrayersWailaDataProvider.INSTANCE, BlockOfferingTable.class);
		registrar.registerNBTProvider(PrayersWailaDataProvider.INSTANCE, BlockOfferingTable.class);
	}

}
