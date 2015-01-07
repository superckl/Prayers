package me.superckl.prayers.proxy;



public interface IProxy {

	public void registerHandlers();
	public void registerEntities();
	public void setupGuis();
	public void registerBindings();
	public void registerRecipes();
	public void renderEffect(final String name, final Object ... args);

}
