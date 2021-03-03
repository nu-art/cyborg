/*
 * The core of the core of all my projects!
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.core.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//@SuppressWarnings("restriction")
public class WebQuickTools {

	// public static byte[] getDataFromUrl(String path)
	// throws IOException {
	// URL url = new URL(path);
	// String passwdstring = "username:password";
	// String encoding = new BASE64Encoder().encode(passwdstring.getBytes());
	//
	// URLConnection uc = url.openConnection();
	// uc.setRequestProperty("Authorization", "Basic " + encoding);
	// return WebQuickTools.readFully(uc.getInputStream());
	// }

	public static byte[] readFully(InputStream inputStream)
		throws IOException {
		ByteArrayOutputStream bos;
		BufferedOutputStream out = new BufferedOutputStream(bos = new ByteArrayOutputStream(inputStream.available()), 1024);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) { out.write(buffer, 0, length); }
		out.flush();
		inputStream.close();
		return bos.toByteArray();
	}
}
