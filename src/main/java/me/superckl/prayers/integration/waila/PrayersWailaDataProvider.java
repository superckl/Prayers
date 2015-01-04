package me.superckl.prayers.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import me.superckl.prayers.common.block.BlockBasicAltar;
import me.superckl.prayers.common.prayer.IPrayerAltar;
import me.superckl.prayers.common.utility.DateHelper;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.item.ItemStack;

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
		IPrayerAltar altar = null;
		if((accessor.getBlock() != null) && (accessor.getBlock() instanceof IPrayerAltar))
			altar = (IPrayerAltar) accessor.getBlock();
		else if((accessor.getTileEntity() != null) && (accessor.getTileEntity() instanceof IPrayerAltar))
			altar = (IPrayerAltar) accessor.getTileEntity();
		if(altar != null){
			if(altar.isActivated()){
				currenttip.add("Active");
				currenttip.add(StringHelper.build("Points: ", altar.getPrayerPoints(), "/", altar.getMaxPrayerPoints()));
			}else if(altar.isInRitual()){
				currenttip.add("Activating");
				currenttip.add(StringHelper.build("Time Left: ", DateHelper.toDateString(altar.getRitualTimer())));
			}else
				currenttip.add("Inactive");
			if(altar.getCurrentItem() != null)
				currenttip.add("Current Item: "+altar.getCurrentItem().getDisplayName());
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return currenttip;
	}

	public static void callbackRegister(final IWailaRegistrar registrar){
		registrar.addConfig("Prayers", "showaltarinfo", "Show Altar Info", true);
		registrar.registerBodyProvider(PrayersWailaDataProvider.INSTANCE, BlockBasicAltar.class);
	}

}
