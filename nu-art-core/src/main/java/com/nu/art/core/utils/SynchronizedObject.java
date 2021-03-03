package com.nu.art.core.utils;

import com.nu.art.core.interfaces.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by TacB0sS on 08/04/2018.
 */

public class SynchronizedObject<Type> {

	private final WeakHashMap<Thread, Type> instanceMap = new WeakHashMap<>();
	private final Getter<Type> getter;

	public SynchronizedObject(Getter<Type> getter) {
		this.getter = getter;
	}

	public final Type get() {
		Thread thread = Thread.currentThread();
		Type object = instanceMap.get(thread);
		if (object == null)
			instanceMap.put(thread, object = getter.get());

		return object;
	}

	public final Set<Thread> getKeySet() {
		return instanceMap.keySet();
	}

	public final Collection<Type> values() {return instanceMap.values();}
}
