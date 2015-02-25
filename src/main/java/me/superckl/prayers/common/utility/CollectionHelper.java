package me.superckl.prayers.common.utility;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.SneakyThrows;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CollectionHelper {

	private static Method clone;

	@SneakyThrows //We shouldn't be throwing any errors, ever.
	public static <T extends Cloneable, V extends Cloneable> Map<T,V> deepCopy(final Map<T,V> map){
		if(CollectionHelper.clone == null){
			CollectionHelper.clone = Object.class.getDeclaredMethod("clone");
			CollectionHelper.clone.setAccessible(true);
		}
		final Map<T, V> copy = new HashMap<T, V>();
		for(final Entry<T, V> entry:map.entrySet())
			copy.put((T) CollectionHelper.clone.invoke(entry.getKey()), (V) CollectionHelper.clone.invoke(entry.getValue()));
		return copy;
	}

	@SneakyThrows //We shouldn't be throwing any errors, ever.
	public static <T extends Cloneable, V extends Cloneable> Map<T,Set<V>> deepCopyMapOfSet(final Map<T,Set<V>> map){
		if(CollectionHelper.clone == null){
			CollectionHelper.clone = Object.class.getDeclaredMethod("clone");
			CollectionHelper.clone.setAccessible(true);
		}
		final Map<T, Set<V>> copy = new HashMap<T, Set<V>>();
		for(final Entry<T, Set<V>> entry:map.entrySet()){
			final T t = (T) CollectionHelper.clone.invoke(entry.getKey());
			final Set<V> cloned = new HashSet<V>();
			for(final V v:entry.getValue())
				cloned.add((V) CollectionHelper.clone.invoke(v));
			copy.put(t, cloned);
		}

		return copy;
	}

	@SneakyThrows //We shouldn't be throwing any errors, ever.
	public static <T extends Cloneable, V extends Cloneable> Map<T,V> deepCopyWithIdentityCheck(final Map<T,V> map){
		if(CollectionHelper.clone == null){
			CollectionHelper.clone = Object.class.getDeclaredMethod("clone");
			CollectionHelper.clone.setAccessible(true);
		}
		//Perform identity matching
		Map<V, Set<T>> temp = Maps.newIdentityHashMap();
		for(final Entry<T, V> entry:map.entrySet())
			if(temp.containsKey(entry.getValue()))
				temp.get(entry.getValue()).add(entry.getKey());
			else{
				final Set<T> set = Sets.newIdentityHashSet();
				set.add(entry.getKey());
				temp.put(entry.getValue(), set);
			}
		//Copy
		temp = CollectionHelper.deepCopyMapOfSet(temp);
		//Return to original structure
		final Map<T, V> copy = new HashMap<T, V>();
		for(final Entry<V, Set<T>> entry:temp.entrySet())
			for(final T key:entry.getValue())
				copy.put(key, entry.getKey());
		return copy;
	}

}
