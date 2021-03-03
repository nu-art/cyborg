package com.nu.art.core.tools;

public class StringTools {

	public static int compareVersion(String v1, String v2) {
		String[] v1Parts = v1.split("\\.");
		String[] v2Parts = v2.split("\\.");

		for (int i = 0; i < v1Parts.length; i++) {
			int v1i = Integer.parseInt(v1Parts[i]);
			int v2i = v2Parts.length <= i ? 0 : Integer.parseInt(v2Parts[i]);

			if (v1i < v2i)
				return 1;

			if (v1i == v2i)
				continue;

			return -1;
		}

		return 0;
	}

	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}
}
