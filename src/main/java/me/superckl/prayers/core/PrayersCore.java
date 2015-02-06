package me.superckl.prayers.core;

import java.io.IOException;
import java.util.Map;

import me.superckl.prayers.common.reference.ModData;
import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(2000)
@MCVersion("1.7.10")
@Name("Prayers Core")
public class PrayersCore extends AccessTransformer implements IFMLLoadingPlugin{

	public PrayersCore() throws IOException {
		super(ModData.MOD_ID.toLowerCase() + "_at.cfg");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return ModContainerPrayersCore.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(final Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return this.getClass().getName();
	}

}
