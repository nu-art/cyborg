package com.nu.art.belog;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.belog.loggers.JavaLogger;
import com.nu.art.core.exceptions.runtime.NotImplementedYetException;

import org.junit.Before;
import org.junit.Test;

import static com.nu.art.belog.loggers.JavaLogger.Config_FastJavaLogger;

/**
 * Created by TacB0sS on 23/04/2018.
 */
public class TestBelogged
	extends Logger {

	private static int Count_Repeat = 1000000;
	private static boolean setUpIsDone = false;
	private JavaLogger logClient;

	@Before
	public void setUp() {
		if (setUpIsDone) {
			return;
		}

		logClient = new JavaLogger();
		BeLogged.getInstance().setConfig(Config_FastJavaLogger);
		setUpIsDone = true;
	}

	@Test
	public void testBelogged() {
		log(LogLevel.Debug, "Wrong Param: Incoming%202.mp3", new NotImplementedYetException("Test Exception error"));
		log(LogLevel.Debug, "%s: Testing param", "Test");
		log(LogLevel.Info, "Testing no param");

		BeLogged.getInstance().setMinLogLevel(LogLevel.Warning);
		BeLogged.getInstance().setMaxLogLevel(LogLevel.Assert);
		log(LogLevel.Info, "Should NOT be shown");
		log(LogLevel.Warning, "Should be shown warning");
		log(LogLevel.Error, "Should be shown error");
		log(LogLevel.Error, "Should be shown With exception", new NotImplementedYetException("Test Exception error"));

		BeLogged.getInstance().setMinLogLevel(LogLevel.Verbose);
		BeLogged.getInstance().setMaxLogLevel(LogLevel.Warning);
		log(LogLevel.Error, "Should NOT be shown error");
		log(LogLevel.Debug, "Should be shown With exception", new NotImplementedYetException("Test Exception debug"));
		log(LogLevel.Debug, "Should be shown With exception %s", new NotImplementedYetException("Test Exception debug"));
		log(LogLevel.Debug, "Should be shown With param and exception %s and %s", "Donno", new NotImplementedYetException("Test Exception debug"));

		log(LogLevel.Info, new NotImplementedYetException("Exception only"));
		BeLogged.getInstance().setMinLogLevel(LogLevel.Warning);
		BeLogged.getInstance().setMaxLogLevel(LogLevel.Assert);
		log(LogLevel.Info, "Should NOT be shown");
		log(LogLevel.Warning, "testing log with %F in it");
	}

	@Test
	public void logBenchmark() {
		benchmarkStringConcat(10, "param1", 2, "param3", 0.84f);
		benchmarkStringFormat(10, "param1", 2, "param3", 0.84f);
		logBenchmarking(1000);
		logBenchmarking(10000);
		logBenchmarking(100000);
		logBenchmarking(1000000);
	}

	private void logBenchmarking(int count) {

		long formatDuration = benchmarkStringFormat(count, "param1", 2, "param3", 0.84f);
		long concatDuration = benchmarkStringConcat(count, "param1", 2, "param3", 0.84f);

		logInfo("Repeat: " + count + " format: " + formatDuration + ", concat: " + concatDuration);
	}

	private long benchmarkStringFormat(int count, Object... p) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			String str = String.format("Test String format: 1-%s, 2-%d, 3-%s, 4-%f", p);
		}
		return System.currentTimeMillis() - start;
	}

	private long benchmarkStringConcat(int count, Object... p) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			String str = "Test String concat: 1-" + p[0] + ", 2-" + p[1] + ", 3-" + p[2] + ", 4-" + p[3];
		}
		return System.currentTimeMillis() - start;
	}
}
