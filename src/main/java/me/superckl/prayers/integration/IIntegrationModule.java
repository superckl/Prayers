package me.superckl.prayers.integration;

public interface IIntegrationModule {

	public void preInit();
	public void init();
	public void postInit();
	public String getName();

}
