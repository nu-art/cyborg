package com.nu.art.core.handler;

import com.nu.art.belog.BeLogged;
import com.nu.art.core.utils.JavaHandler;

import org.junit.Test;

import static com.nu.art.belog.loggers.JavaLogger.Config_FastJavaLogger;

/**
 * Created by TacB0sS on 24/09/2017.
 */

public class Test_JavaHandler
	extends Test_HandlerCore {

	@Test
	public void test_Handler() {
		BeLogged.getInstance().setConfig(Config_FastJavaLogger);
		JavaHandler.DebugThreads.enable();
		JavaHandler.DebugExecutionTime.enable();

		final JavaHandler handler = new JavaHandler();
		handler.setLogger(this);
		handler.setMinThreads(3);
		handler.setThreadTimeoutMs(1000);
		handler.setMaxThreads(7);
		handler.start("test-handler");

		final PrintRunnable printRunnableNH = new PrintRunnable("N-H", 1200);
		final PrintRunnable printRunnableRH = new PrintRunnable("R-H", 1000);
		PrintRunnable[] items = new PrintRunnable[]{
			new PrintRunnable("A0"),
			new PrintRunnable("A1"),
			new PrintRunnable("A2"),
			new PrintRunnable("A3"),
			new PrintRunnable("A4"),
			new PrintRunnable("A5"),
			new PrintRunnable("A6"),
			new PrintRunnable("A7"),
			new PrintRunnable("B", 1000),
			new PrintRunnable("C"),
			new PrintRunnable("D", 500),
			new PrintRunnable("E", 200),
			new PrintRunnable("F", 100),
			printRunnableRH,
			new PrintRunnable("G")
		};

		startedAt = System.currentTimeMillis();
		for (int i = 0; i < 8; i++) {
			handler.post("SleepRunnable for " + 2000 + "ms", new SleepRunnable(i, 2000));
		}

		for (PrintRunnable runnable : items) {
			handler.post(runnable.delay, "Print log: " + runnable.string, runnable);
		}

		handler.post(800, new Runnable() {
			@Override
			public void run() {
				log("removing R-H");
				handler.remove(printRunnableRH);
				handler.post(printRunnableNH.delay, printRunnableNH);
			}
		});
		handler.post(10000, new Runnable() {
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
