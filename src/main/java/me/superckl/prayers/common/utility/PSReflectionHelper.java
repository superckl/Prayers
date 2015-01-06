package me.superckl.prayers.common.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PSReflectionHelper {

	public static <T> Field removeFinal(final Class <? super T > clazz, final String... fieldNames)
	{
		final Field field = ReflectionHelper.findField(clazz, ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), fieldNames));

		try
		{
			final Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return field;
	}

	public static <T, E> void setPrivateFinalValue(final Class <? super T > classToAccess, final T instance, final E value, final String... fieldNames)
	{
		final Field field = ReflectionHelper.findField(classToAccess, ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));

		try
		{
			final Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

			field.set(instance, value);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	public static <T> Class<?>[] toClassArray(final T[] array){
		final Class<?>[] classArray = new Class<?>[array.length];
		for(int i = 0; i < array.length; i++)
			classArray[i] = array[i].getClass();
		return classArray;
	}

	public static StackTraceElement retrieveCallingStackTraceElement(){
		return PSReflectionHelper.retrieveCallingStackTraceElement(3);
	}

	public static StackTraceElement retrieveCallingStackTraceElement(final int depth){
		return new Throwable().getStackTrace()[depth];
	}

	/**
	 * Attempts to find something with the given class at the given coordinates. First checks the Block and then the TileEntity.
	 * @param clazz The Class to find
	 * @return The found object, or null if it was not found;
	 */
	public static <T> T findAt(final Class<T> clazz, final World world, final int x, final int y, final int z){
		final Block block = world.getBlock(x, y, z);
		if(clazz.isAssignableFrom(block.getClass()))
			return clazz.cast(block);
		final TileEntity te = world.getTileEntity(x, y, z);
		if((te != null) && clazz.isAssignableFrom(te.getClass()))
			return clazz.cast(te);
		return null;
	}

}
