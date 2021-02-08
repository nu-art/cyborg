package com.nu.art.cyborg.logcat.sources;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.nu.art.cyborg.core.abs.Cyborg;
import com.nu.art.cyborg.logcat.LogcatSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Logcat_ContentFile
	extends LogcatSource {

	private final Uri uri;
	private final Cyborg cyborg;

	public Logcat_ContentFile(Cyborg cyborg, Uri uri) {
		super("Content: " + getSubstring(uri));
		this.uri = uri;
		this.cyborg = cyborg;
	}

	@NonNull
	private static String getSubstring(Uri uri) {
		String uriAsString = uri.toString();
		return uriAsString.substring(uriAsString.lastIndexOf("/") + 1);
	}

	@Override
	public BufferedReader createReader()
		throws Exception {
		InputStream inputStream = cyborg.getContentResolver().openInputStream(uri);
		return new BufferedReader(new InputStreamReader(inputStream));
	}

	public boolean isAvailable() {
		return true;
	}
}
