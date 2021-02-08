package com.nu.art.cyborg.logcat.sources;

import com.nu.art.cyborg.logcat.LogcatSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Logcat_LiveSystemLogs
	extends LogcatSource {

	private Process process;

	public Logcat_LiveSystemLogs() {
		super("Live System Logs");
	}

	public BufferedReader createReader()
		throws IOException {
		process = Runtime.getRuntime().exec("logcat -v time tag");
		return new BufferedReader(new InputStreamReader(process.getInputStream()));
	}

	public boolean isAvailable() {
		return true;
	}

	public void disposeImpl()
		throws Exception {
		super.disposeImpl();
		process.destroy();
	}
}
