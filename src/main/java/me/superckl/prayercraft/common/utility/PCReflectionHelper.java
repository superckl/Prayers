package me.superckl.prayercraft.common.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PCReflectionHelper {

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
		return PCReflectionHelper.retrieveCallingStackTraceElement(3);
	}

	public static StackTraceElement retrieveCallingStackTraceElement(final int depth){
		return new Throwable().getStackTrace()[depth];
	}

	public static final String[] field_ocean = {"ocean", "field_76771_b"};
	public static final String[] field_deepOcean = {"deepOcean", "field_150575_M"};
	public static final String[] field_water = {"water", "field_150355_j"};
	public static final String[] field_gravel = {"gravel", "field_150351_n"};
	public static final String[] field_sand = {"sand", "field_150354_m"};
	public static final String[] field_worldObj = {"worldObj", "field_73230_p"};

	public static final String[] method_genBiomeTerrain = {"genBiomeTerrain", "func_150560_b"};
	public static final String[] method_func_147424_a = {"func_147424_a"};

	public static final String[] desc_genBiomeTerrain = {"(Lnet/minecraft/world/World;Ljava/util/Random;[Lnet/minecraft/block/Block;[BIID)V", "(Lahb;Ljava/util/Random;[Laji;[BIID)V"};
	public static final String[] desc_func_147424_a = {"(II[Lnet/minecraft/block/Block;)V", "(II[Laji;)V"};

	public static final String[] class_biomeGenBase = {"net.minecraft.world.biome.BiomeGenBase", "ahu"};
	public static final String[] class_chunkProviderGenerate = {"net.minecraft.world.gen.ChunkProviderGenerate", "aqz"};

}
