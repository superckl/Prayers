package me.superckl.prayers.proxy;



public interface IProxy {

	public void registerHandlers();
	public void registerRenderers();
	public void registerEntities();
	public void setupGuis();
	public void registerBindings();
	public void registerRecipes();
	public void registerEntitySpawns();
	public void renderEffect(final String name, final Object ... args);

}
