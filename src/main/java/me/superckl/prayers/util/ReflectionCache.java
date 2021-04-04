package me.superckl.prayers.util;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReflectionCache {

	public static final ReflectionCache INSTANCE = new ReflectionCache();

	private final Map<Methods, Method> methodCache = new EnumMap<>(Methods.class);

	private ReflectionCache() {}

	public Method get(final Methods method) {
		this.methodCache.computeIfAbsent(method, compMethod -> {
			final Method m = ObfuscationReflectionHelper.findMethod(compMethod.clazz, compMethod.srgName);
			m.setAccessible(true);
			return m;
		});
		return this.methodCache.get(method);
	}

	@RequiredArgsConstructor
	public enum Methods{
		MOB_ENTITY__SHOULD_DESPAWN_IN_PEACEFUL(MobEntity.class, "func_225511_J_");

		private final Class<?> clazz;
		private final String srgName;

	}

}
