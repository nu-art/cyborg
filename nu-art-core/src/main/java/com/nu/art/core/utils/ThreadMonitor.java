package com.nu.art.core.utils;

import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.ArrayTools;

import static com.nu.art.core.tools.DateTimeTools.Second;

public class ThreadMonitor {

	public static final String DebugFlag = "Debug_" + ThreadMonitor.class.getSimpleName();

	private static final ThreadMonitor ThreadsMonitor = new ThreadMonitor();

	private SynchronizedObject<Monitor> monitors = new SynchronizedObject<>(new Getter<Monitor>() {
		@Override
		public Monitor get() {
			return new Monitor();
		}
	});

	public static class RunnableMonitor
		implements Runnable {

		private final String name;
		private final long estimated;
		private final Runnable runnable;

		public RunnableMonitor(Runnable runnable) {
			this("", 5 * Second, runnable);
		}

		public RunnableMonitor(String name, Runnable runnable) {
			this(name, 5 * Second, runnable);
		}

		public RunnableMonitor(String name, long estimated, Runnable runnable) {
			this.name = name;
			this.runnable = runnable;
			this.estimated = estimated;
		}

		@Override
		public final void run() {
			ThreadsMonitor.getThreadMonitor().started(this);
			runnable.run();
			ThreadsMonitor.getThreadMonitor().ended(this);
		}
	}

	public static ThreadMonitor getInstance() {
		return ThreadsMonitor;
	}

	private Monitor getThreadMonitor() {
		return monitors.get();
	}

	public final class Stats {

		public String name;
		public long duration;
		public long estimated;
	}

	public final class Monitor {

		private final Thread thread;

		private volatile Stats longest = new Stats();

		private long executedRunnables;
		private long totalDuration;

		private volatile long estimated;
		private volatile long started;

		private Monitor() {
			thread = Thread.currentThread();
		}

		private void started(RunnableMonitor runnableMonitor) {
			estimated = runnableMonitor.estimated;
			started = System.currentTimeMillis();
		}

		public long getStarted() {
			return started;
		}

		public final boolean isDelayed() {
			return estimated < System.currentTimeMillis() - started;
		}

		private void ended(RunnableMonitor runnableMonitor) {
			long duration = System.currentTimeMillis() - started;
			if (duration > longest.duration) {
				longest.duration = duration;
				longest.estimated = runnableMonitor.estimated;
				longest.name = runnableMonitor.name;
			}

			executedRunnables += 1;
			totalDuration += duration;
		}

		public Thread getThread() {
			return thread;
		}

		public Stats getState() {
			return longest;
		}

		public long getExecutedRunnables() {
			return executedRunnables;
		}
	}

	public final Monitor[] monitor() {
		return monitor(new Condition<Monitor>() {
			@Override
			public boolean checkCondition(Monitor monitor) {
				return monitor.isDelayed();
			}
		});
	}

	public final Monitor[] monitor(Condition<Monitor> condition) {
		return ArrayTools.asFilteredArray(this.monitors.values(), Monitor.class, condition);
	}
}
