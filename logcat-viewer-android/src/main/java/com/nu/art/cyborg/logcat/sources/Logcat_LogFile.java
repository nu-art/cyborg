package com.nu.art.cyborg.logcat.sources;

import com.nu.art.cyborg.logcat.LogcatSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Logcat_LogFile
	extends LogcatSource {

	private final File file;

	public Logcat_LogFile(File file) {
		super("File: " + file.getName());
		this.file = file;
	}

	@Override
	public BufferedReader createReader()
		throws Exception {
		return new BufferedReader(new FileReader(file));
	}

	public boolean isAvailable() {
		return file.exists();
	}
}
