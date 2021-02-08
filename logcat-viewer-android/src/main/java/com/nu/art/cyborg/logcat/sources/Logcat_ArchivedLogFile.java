package com.nu.art.cyborg.logcat.sources;

import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.StreamTools;
import com.nu.art.cyborg.logcat.LogcatSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.zip.ZipInputStream;

public class Logcat_ArchivedLogFile
	extends LogcatSource {

	private final File file;
	private final File tempFile;

	public Logcat_ArchivedLogFile(File file, File tempFile) {
		super("Archive: " + file.getName());
		this.file = file;
		this.tempFile = tempFile;
	}

	public boolean isAvailable() {
		return file.exists();
	}

	@Override
	protected BufferedReader createReader()
		throws Exception {
		FileTools.mkDir(tempFile.getParentFile());

		ZipInputStream zipStream = new ZipInputStream(new FileInputStream(file));
		zipStream.getNextEntry();
		StreamTools.copy(zipStream, tempFile);
		zipStream.closeEntry();
		zipStream.close();

		return new BufferedReader(new FileReader(tempFile));
	}

	@Override
	protected void disposeImpl()
		throws Exception {
		super.disposeImpl();
	}
}
