package me.superckl.prayers.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import me.superckl.prayers.common.entity.EntityUndeadWizardPriest;
import me.superckl.prayers.common.utility.StringHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PrayersWailaEntityProvider implements IWailaEntityProvider{

	public static final PrayersWailaEntityProvider INSTANCE = new PrayersWailaEntityProvider();

	@Override
	public Entity getWailaOverride(final IWailaEntityAccessor accessor,
			final IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaHead(final Entity entity, final List<String> currenttip,
			final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return  currenttip;
	}

	@Override
	public List<String> getWailaBody(final Entity entity, final List<String> currenttip,
			final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		if((entity != null) && (entity instanceof EntityUndeadWizardPriest))
			currenttip.add(StringHelper.build("Level: ", ((EntityUndeadWizardPriest)entity).getLevel()));
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(final Entity entity, final List<String> currenttip,
			final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final Entity ent,
			final NBTTagCompound tag, final World world) {
		// TODO Auto-generated method stub
		return tag;
	}

	public static void callbackRegister(final IWailaRegistrar registrar){
		registrar.registerBodyProvider(PrayersWailaEntityProvider.INSTANCE, EntityUndeadWizardPriest.class);
	}

}
