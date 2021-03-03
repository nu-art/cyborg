package com.nu.art.core.utils;

import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.ILogger;
import com.nu.art.core.utils.DebugFlags.DebugFlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class JavaHandler
	implements ILogger {

	public static final DebugFlag DebugThreads = DebugFlags.createFlag("Debug_JavaHandler_Threads");
	public static final DebugFlag DebugExecutionTime = DebugFlags.createFlag("Debug_JavaHandler_ExecutionTime");
	private ILogger logger;

	private final Object lock = new Object();

	private final HashSet<Thread> busy = new HashSet<>();
	private final ArrayList<Thread> threadPool = new ArrayList<>();
	private final _Executable _cache = new _Executable(0, "query", null);
	private final ArrayList<_Executable> queue = new ArrayList<>();

	private int minThreads = 1;
	private int maxThreads = 1;
	private int threadTimeoutMs = 10000;
	private String name;
	private Processor<Thread> threadInitiator;

	private boolean running = true;

	public interface Executable {

		String getName();

		long getWhen();
	}

	private class _Executable
		implements Executable {

		private Runnable toExecute;
		private final String name;
		private final long when;

		private _Executable(long when, String name, Runnable toExecute) {
			this.name = name;
			this.toExecute = toExecute;
			this.when = when;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (o == null || getClass() != o.getClass())
				return false;

			_Executable that = (_Executable) o;

			return toExecute.equals(that.toExecute);
		}

		@Override
		public int hashCode() {
			return toExecute.hashCode();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public long getWhen() {
			return when;
		}
	}

	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	public JavaHandler setThreadTimeoutMs(int threadTimeoutMs) {
		this.threadTimeoutMs = threadTimeoutMs;
		if (this.threadTimeoutMs < 1000)
			this.threadTimeoutMs = 1000;

		return this;
	}

	public JavaHandler setThreadInitiator(Processor<Thread> threadInitiator) {
		this.threadInitiator = threadInitiator;
		return this;
	}

	public JavaHandler setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
		return this;
	}

	public JavaHandler setMinThreads(int minThreads) {
		this.minThreads = minThreads;
		return this;
	}

	public final synchronized JavaHandler start(String name) {
		if (this.name != null)
			throw new RuntimeException("instance already started with name: " + name);

		this.name = name;
		running = true;

		for (int i = 0; i < minThreads; i++) {
			createNewThread();
		}
		return this;
	}

	private void createNewThread() {
		final Thread thread = new Thread(new Runnable() {

			long mark = System.currentTimeMillis();
			boolean keepAlive = true;

			@Override
			public void run() {
				Thread thread = Thread.currentThread();
				if (threadInitiator != null)
					threadInitiator.process(Thread.currentThread());

				long sleep;
				while (running && keepAlive) {
					synchronized (lock) {
						if (disposeThread(thread))
							continue;
					}

					sleep = 0;
					_Executable executable = null;
					synchronized (lock) {
						try {
							if (queue.size() == 0) {
								sleep = threadTimeoutMs + 50;
							} else {
								long sleepInterval = queue.get(0).when - System.currentTimeMillis();
								if (sleepInterval > 5) {
									sleep = sleepInterval - 5;

									if (sleep > threadTimeoutMs + 50)
										sleep = threadTimeoutMs + 50;
								}
							}

							if (sleep > 0) {
								busy.remove(thread);
								lock.wait(sleep);
								continue;
							}

							executable = queue.remove(0);
							busy.add(thread);
							createThreadIfNeeded();
						} catch (InterruptedException ignore) {}
					}

					if (executable == null)
						continue;

					mark = System.currentTimeMillis();
					if (DebugExecutionTime.isEnabled())
						logDebug("Executing action START: " + executable.name);
					executable.toExecute.run();
					if (DebugExecutionTime.isEnabled())
						logDebug("Executing action END: " + executable.name + " (" + (System.currentTimeMillis() - mark) + "ms)");
				}
			}

			private void createThreadIfNeeded() {
				if (busy.size() == threadPool.size() && threadPool.size() < maxThreads)
					createNewThread();
			}

			private boolean disposeThread(Thread thread) {
				if (System.currentTimeMillis() - mark < threadTimeoutMs || threadPool.size() <= minThreads || busy.contains(thread))
					return false;

				threadPool.remove(thread);
				busy.remove(thread);
				if (DebugThreads.isEnabled())
					logDebug("Disposing of thread: '" + thread.getName() + "' - New thread count (" + threadPool.size() + ")");
				keepAlive = false;
				return true;
			}
		}, this.name + "-" + threadPool.size());

		synchronized (lock) {
			threadPool.add(thread);
			if (DebugThreads.isEnabled())
				logDebug("Added thread: '" + thread.getName() + "' - New thread count (" + threadPool.size() + ")");
		}

		thread.start();
	}

	public void stop() {
		running = false;
	}

	public int getItemsCount() {
		synchronized (lock) {
			return queue.size();
		}
	}

	public List<Executable> getItems() {
		synchronized (lock) {
			return new ArrayList<Executable>(queue);
		}
	}

	public void post(Runnable action) {
		post(0, action);
	}

	public void post(String name, Runnable action) {
		post(0, name, action);
	}

	public void post(int delay, Runnable action) {
		postAt(System.currentTimeMillis() + delay, action);
	}

	public void post(int delay, String name, Runnable action) {
		postAt(System.currentTimeMillis() + delay, name, action);
	}

	public void postAt(long when, Runnable action) {
		postAt(when, "nameless action: " + action.getClass().getName(), action);
	}

	public void postAt(long when, String name, Runnable action) {
		postImpl(when, name, action);
	}

	private void postImpl(long when, String name, Runnable action) {
		synchronized (lock) {
			queue.add(new _Executable(when, name, action));
			sortQueue();
			lock.notify();
		}
	}

	public void clear() {
		synchronized (lock) {
			queue.clear();
			lock.notify();
		}
	}

	public boolean removeAndPost(Runnable action) {
		return removeAndPost(0, action);
	}

	public boolean removeAndPost(int delay, Runnable action) {
		return removeAndPostAt(System.currentTimeMillis() + delay, action);
	}

	public boolean removeAndPostAt(long when, Runnable action) {
		return removeAndPostImpl(when, action);
	}

	public boolean remove(Runnable action) {
		return removeAndPostImpl(0, action);
	}

	private boolean removeAndPostImpl(long when, Runnable action) {
		if (maxThreads > 1)
			logWarning("Trying to remove a scheduled action in a multi threaded handler... there is no guarantee it will be removed before executed!!");

		synchronized (lock) {
			_cache.toExecute = action;
			_Executable removedExecutable = null;
			int indexOf = queue.indexOf(_cache);

			if (indexOf != -1) { // remove runnable if exists in queue
				removedExecutable = queue.remove(indexOf);
			}
			String name = "nameless action: " + action.getClass().getName();
			if (removedExecutable != null)
				name = removedExecutable.name;

			if (when > 0) {
				queue.add(new _Executable(when, name, action));
				sortQueue();
				lock.notify();
			}
			return true;
		}
	}

	private void sortQueue() {
		Collections.sort(queue, new Comparator<_Executable>() {
			@Override
			public int compare(_Executable o1, _Executable o2) {
				return Long.compare(o1.when, o2.when);
			}
		});
	}

	@Override
	public void logVerbose(String verbose) {
		if (logger != null)
			logger.logVerbose(verbose);
	}

	@Override
	public void logVerbose(String verbose, Object... params) {
		if (logger != null)
			logger.logVerbose(verbose, params);
	}

	@Override
	public void logVerbose(Throwable e) {
		if (logger != null)
			logger.logVerbose(e);
	}

	@Override
	public void logVerbose(String verbose, Throwable e) {
		if (logger != null)
			logger.logVerbose(verbose, e);
	}

	@Override
	public void logDebug(String debug) {
		if (logger != null)
			logger.logDebug(debug);
	}

	@Override
	public void logDebug(String debug, Object... params) {
		if (logger != null)
			logger.logDebug(debug, params);
	}

	@Override
	public void logDebug(Throwable e) {
		if (logger != null)
			logger.logDebug(e);
	}

	@Override
	public void logDebug(String debug, Throwable e) {
		if (logger != null)
			logger.logDebug(debug, e);
	}

	@Override
	public void logInfo(String info) {
		if (logger != null)
			logger.logInfo(info);
	}

	@Override
	public void logInfo(String info, Object... params) {
		if (logger != null)
			logger.logInfo(info, params);
	}

	@Override
	public void logInfo(Throwable e) {
		if (logger != null)
			logger.logInfo(e);
	}

	@Override
	public void logInfo(String info, Throwable e) {
		if (logger != null)
			logger.logInfo(info, e);
	}

	@Override
	public void logWarning(String warning) {
		if (logger != null)
			logger.logWarning(warning);
	}

	@Override
	public void logWarning(String warning, Object... params) {
		if (logger != null)
			logger.logWarning(warning, params);
	}

	@Override
	public void logWarning(Throwable e) {
		if (logger != null)
			logger.logWarning(e);
	}

	@Override
	public void logWarning(String warning, Throwable e) {
		if (logger != null)
			logger.logWarning(warning, e);
	}

	@Override
	public void logError(String error) {
		if (logger != null)
			logger.logError(error);
	}

	@Override
	public void logError(String error, Object... params) {
		if (logger != null)
			logger.logError(error, params);
	}

	@Override
	public void logError(Throwable e) {
		if (logger != null)
			logger.logError(e);
	}

	@Override
	public void logError(String error, Throwable e) {
		if (logger != null)
			logger.logError(error, e);
	}
}