package com.nu.art.cyborg.logcat.sources;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.StreamTools;
import com.nu.art.cyborg.core.abs.Cyborg;
import com.nu.art.cyborg.logcat.LogcatSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Logcat_ContentArchiveFile
	extends LogcatSource {

	private final Uri uri;
	private final Cyborg cyborg;
	private final File tempFile;

	public Logcat_ContentArchiveFile(Cyborg cyborg, Uri uri, File tempFile) {
		super("Content: " + getSubstring(uri));
		this.cyborg = cyborg;
		this.uri = uri;
		this.tempFile = tempFile;
	}

	@NonNull
	private static String getSubstring(Uri uri) {
		String uriAsString = uri.toString();
		return uriAsString.substring(uriAsString.lastIndexOf("/") + 1);
	}

	@Override
	public BufferedReader createReader()
		throws Exception {
		FileTools.mkDir(tempFile.getParentFile());

		InputStream inputStream = cyborg.getContentResolver().openInputStream(uri);
		ZipInputStream zipStream = new ZipInputStream(inputStream);
		ZipEntry nextEntry;
		while ((nextEntry = zipStream.getNextEntry()) != null) {
			if (!nextEntry.getName().endsWith(".txt")) {
				zipStream.closeEntry();
				continue;
			}

			break;
		}

		if (nextEntry == null)
			StreamTools.copy(new ByteArrayInputStream("No log files found in archive".getBytes()), tempFile);
		else
			StreamTools.copy(zipStream, tempFile);

		zipStream.closeEntry();
		zipStream.close();

		return new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
	}

	public boolean isAvailable() {
		return true;
	}
}
