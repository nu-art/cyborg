/*
 * The module-manager project, is THE infrastructure that all my frameworks
 *  are based on, it allows encapsulation of logic where needed, and allow
 *  modules to converse without other design patterns limitations.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.modular.core;

import com.nu.art.belog.Logger;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.WhoCalledThis;
import com.nu.art.core.generics.GenericParamExtractor;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.tools.ArrayTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by TacB0sS on 16-Jul 2017.
 */

public class EventDispatcher
	extends Logger {

	private ArrayList<WeakReference<Object>> toBeRemoved = new ArrayList<>();

	@SuppressWarnings("unchecked")
	private WeakReference<Object>[] _listeners = new WeakReference[0];

	private Thread ownerThread;

	private final GenericParamExtractor extractor;

	public EventDispatcher(String name, GenericParamExtractor extractor) {
		this.extractor = extractor;
		setTag(name);
	}

	public final void addListener(Object listener) {
		for (WeakReference<Object> ref : _listeners) {
			if (ref.get() == listener)
				return;
		}

		_listeners = ArrayTools.appendElement(_listeners, new WeakReference<>(listener));
	}

	public final EventDispatcher own() {
		if (ownerThread != null)
			throw new BadImplementationException("This dispatcher is already owned by '" + ownerThread.getName() + "' and cannot be assigned to '" + Thread.currentThread()
			                                                                                                                                               .getName() + "'");

		ownerThread = Thread.currentThread();
		return this;
	}

	@SuppressWarnings("unchecked")
	public <EventType> void dispatchEvent(WhoCalledThis whoCalledThis, Processor<EventType> processor) {
		verifyThread();
		Class<EventType> eventType = extractor.extractGenericType(Processor.class, processor, 0);
		dispatchEvent(whoCalledThis, eventType, processor);
	}

	@SuppressWarnings("unchecked")
	public <EventType> void dispatchEvent(WhoCalledThis whoCalledThis, Class<EventType> eventType, Processor<EventType> processor) {
		verifyThread();

		for (WeakReference<Object> ref : _listeners) {
			Object listener = ref.get();
			if (listener == null) {
				toBeRemoved.add(ref);
				continue;
			}

			if (!eventType.isAssignableFrom(listener.getClass()))
				continue;

			try {
				processor.process((EventType) listener);
			} catch (RuntimeException t) {
				if (whoCalledThis != null)
					logError(whoCalledThis);

				throw new RuntimeException("Error while processing event:\n + eventType:" + eventType.getSimpleName() + "\n listenerType:" + listener.getClass(), t);
			}
		}

		_listeners = ArrayTools.removeElements(_listeners, toBeRemoved);
		toBeRemoved.clear();
	}

	private void verifyThread() {
		if (ownerThread != null && Thread.currentThread() != ownerThread)
			throw new BadImplementationException("Dispatching event must be done on a single thread, owner thread: " + ownerThread.getName() + ", calling thread: " + Thread
				.currentThread()
				.getName());
	}

	public void removeListener(Object listener) {
		for (WeakReference<Object> ref : _listeners) {
			if (ref.get() == null) {
				toBeRemoved.add(ref);
				continue;
			}

			if (ref.get() == listener) {
				toBeRemoved.add(ref);
			}
		}

		_listeners = ArrayTools.removeElements(_listeners, toBeRemoved);
		toBeRemoved.clear();
	}
}
