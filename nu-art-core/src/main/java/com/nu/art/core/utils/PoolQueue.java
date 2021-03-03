/*
 * The core of the core of all my projects!
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

package com.nu.art.core.utils;

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.generics.Processor;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings( {
	                   "unused",
	                   "unchecked"
                   })
public abstract class PoolQueue<Type> {

	private volatile boolean keepAlive;

	private final ArrayList<Thread> threadPool = new ArrayList<>();

	private final ArrayList<Type> itemsQueue = new ArrayList<>();

	private Processor<Thread> threadInitiator;

	private final Runnable queueAction = new Runnable() {

		@Override
		public final void run() {
			addThreadToPool();
			while (keepAlive) {
				Type item = getNextItem();
				if (item == null) {
					continue;
				}
				try {
					executeAction(item);
				} catch (Throwable e) {
					onExecutionError(item, e);
				}
			}
			removeThreadFromPool();
		}
	};

	public PoolQueue<Type> setThreadInitiator(Processor<Thread> threadInitiator) {
		this.threadInitiator = threadInitiator;
		return this;
	}

	private boolean addThreadToPool() {
		synchronized (this.itemsQueue) {
			if (threadInitiator != null)
				threadInitiator.process(Thread.currentThread());
			return threadPool.add(Thread.currentThread());
		}
	}

	private boolean removeThreadFromPool() {
		synchronized (this.itemsQueue) {
			return threadPool.remove(Thread.currentThread());
		}
	}

	private Type getNextItem() {
		synchronized (this.itemsQueue) {
			if (itemsQueue.size() == 0) {
				try {
					this.itemsQueue.wait();
				} catch (InterruptedException ignore) {
				}
				return null;
			}

			return itemsQueue.remove(0);
		}
	}

	protected final Thread[] getThreads() {
		synchronized (this.itemsQueue) {
			return threadPool.toArray(new Thread[0]);
		}
	}

	public final boolean isAlive() {
		synchronized (this.itemsQueue) {
			return keepAlive;
		}
	}

	protected void onExecutionError(Type item, Throwable e) {
		e.printStackTrace();
	}

	protected abstract void executeAction(Type type)
		throws Throwable;

	public final void kill() {
		synchronized (this.itemsQueue) {
			keepAlive = false;
			this.itemsQueue.notifyAll();
			clear();
		}
	}

	public final boolean contains(Type item) {
		synchronized (this.itemsQueue) {
			return itemsQueue.contains(item);
		}
	}

	public final void addItemIfNotInQueue(Type item) {
		synchronized (this.itemsQueue) {
			if (contains(item))
				return;

			addItem(item);
		}
	}

	public final void moveToHeadOfQueue(Type... items) {
		synchronized (this.itemsQueue) {
			for (int i = items.length - 1; i >= 0; i--) {
				removeItem(items[i]);
				addFirst(items[i]);
			}
		}
	}

	public final void addItem(Type... items) {
		synchronized (this.itemsQueue) {
			Collections.addAll(this.itemsQueue, items);
			this.itemsQueue.notify();
		}
	}

	public final void addFirst(Type item) {
		synchronized (this.itemsQueue) {
			itemsQueue.add(0, item);
			this.itemsQueue.notify();
		}
	}

	public final boolean removeItem(Type item) {
		synchronized (this.itemsQueue) {
			return itemsQueue.remove(item);
		}
	}

	public final Type removeItem(int index) {
		synchronized (this.itemsQueue) {
			return itemsQueue.remove(index);
		}
	}

	public final int getItemsCount() {
		synchronized (this.itemsQueue) {
			return itemsQueue.size();
		}
	}

	public final void clear() {
		synchronized (this.itemsQueue) {
			itemsQueue.clear();
		}
	}

	public final void createThreads(String name) {
		createThreads(name, 1);
	}

	public final void createThreads(String name, int count) {
		if (count < 1)
			throw new BadImplementationException("Threads count request is '" + count + "' but MUST be >= 1");

		if (this.keepAlive)
			throw new BadImplementationException("PoolQueue instance already initialized!!!");

		this.keepAlive = true;
		for (int i = 0; i < count; i++) {
			Thread t = new Thread(queueAction, name + " #" + i);
			t.start();
		}
	}
}
