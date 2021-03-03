package com.nu.art.core.handler;

import com.nu.art.belog.Logger;

/**
 * Created by TacB0sS on 24/09/2017.
 */

public class Test_HandlerCore
	extends Logger {

	long startedAt;

	protected void log(String message) {
		logInfo(String.format("%6dms: ", (System.currentTimeMillis() - startedAt)) + Thread.currentThread().getName() + " - " + message);
	}

	protected class PrintRunnable
		implements Runnable {

		final String string;

		final int delay;

		protected PrintRunnable(String string) {
			this(string, 0);
		}

		protected PrintRunnable(String string, int delay) {
			this.string = string;
			this.delay = delay;
		}

		@Override
		public void run() {
			log(string);
		}
	}

	protected class SleepRunnable
		implements Runnable {

		final int index;
		final int delay;
		final int sleep;

		protected SleepRunnable(int index, int sleep) {
			this(0, index, sleep);
		}

		protected SleepRunnable(int delay, int index, int sleep) {
			this.delay = delay;
			this.index = index;
			this.sleep = sleep;
		}

		@Override
		public void run() {
			log("S" + index + "-" + sleep + "ms - stared");
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException ignore) {}
			log("S" + index + "-" + sleep + "ms - ended");
		}
	}
}
