package me.superckl.prayers.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import me.superckl.prayers.common.utility.LogHelper;
import me.superckl.prayers.common.utility.PSReflectionHelper;
import me.superckl.prayers.common.utility.StringHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

public class PrayersIntegration implements IIntegrationModule{

	public static final PrayersIntegration INSTANCE = new PrayersIntegration();
	private static final Map<List<String>, String> modules = new HashMap<List<String>, String>();

	static{
		PrayersIntegration.modules.put(Arrays.asList(new String[] {"NotEnoughItems"}), "me.superckl.prayers.integration.nei.NEIIntegration");
	}

	@Getter
	private final List<IIntegrationModule> activeModules = new ArrayList<IIntegrationModule>();

	private PrayersIntegration() {}

	@Override
	public void preInit(){
		boolean noGo = false;
		for(final Entry<List<String>, String> entry:PrayersIntegration.modules.entrySet()){
			for(final String mod:entry.getKey())
				if(!Loader.isModLoaded(mod)){
					noGo = true;
					break;
				}
			if(!noGo)
				try {
					final IIntegrationModule module = (IIntegrationModule) Class.forName(entry.getValue()).newInstance();
					this.activeModules.add(module);
					LogHelper.info(StringHelper.build("Enabled ", module.getName(), " module."));
				} catch (final Exception e) {
					LogHelper.error("Failed to instantiate integration module "+entry.getValue());
					e.printStackTrace();
				}
		}
		if(!Loader.instance().isInState(LoaderState.PREINITIALIZATION))
			LogHelper.error("Class "+PSReflectionHelper.retrieveCallingStackTraceElement().getClassName()+" attempted to preinitialize integration, but FML is not in that state!");
		for(final IIntegrationModule module:this.activeModules)
			module.preInit();
	}


	@Override
	public void init() {
		if(!Loader.instance().isInState(LoaderState.INITIALIZATION))
			LogHelper.error("Class "+PSReflectionHelper.retrieveCallingStackTraceElement().getClassName()+" attempted to initialize integration, but FML is not in that state!");
		for(final IIntegrationModule module:this.activeModules)
			module.init();
	}

	@Override
	public void postInit(){
		if(!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))
			LogHelper.error("Class "+PSReflectionHelper.retrieveCallingStackTraceElement().getClassName()+" attempted to postinitialize integration, but FML is not in that state!");
		for(final IIntegrationModule module:this.activeModules)
			module.postInit();
	}

	@Override
	public String getName() {
		return "Prayers Integration Manager";
	}

}
