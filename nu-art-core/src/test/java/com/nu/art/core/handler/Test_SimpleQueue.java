package com.nu.art.core.handler;

import com.nu.art.belog.BeLogged;
import com.nu.art.core.utils.JavaHandler;

import org.junit.Test;

import static com.nu.art.belog.loggers.JavaLogger.Config_FastJavaLogger;

/**
 * Created by TacB0sS on 24/09/2017.
 */

public class Test_SimpleQueue
	extends Test_HandlerCore {

	@Test
	public void test_Handler() {
		BeLogged.getInstance().setConfig(Config_FastJavaLogger);
		JavaHandler.DebugThreads.enable();
		JavaHandler.DebugExecutionTime.enable();

		final JavaHandler handler = new JavaHandler();
		handler.setLogger(this);
		handler.setMinThreads(3);
		handler.setThreadTimeoutMs(5000);
		handler.setMaxThreads(7);
		handler.start("test-handler");

		startedAt = System.currentTimeMillis();
		for (int i = 0; i < 20; i++) {
			int delay = i * 500;
			handler.post(delay, "Sleeping for: " + 2000 + "ms", new SleepRunnable(i, 2000));
		}

		handler.post(20000, "terminating", new Runnable() {
			@Override
			public void run() {
				log("Terminate");
				handler.stop();

				synchronized (handler) {
					handler.notify();
				}
			}
		});
		synchronized (handler) {
			try {
				handler.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}