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

import com.nu.art.core.file.Charsets;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.interfaces.ProgressNotifier;
import com.nu.art.core.utils.SynchronizedObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("WeakerAccess")
public class StreamTools {

	public static int CopyBufferSize = 1024;
	private static final SynchronizedObject<byte[]> buffers = new SynchronizedObject<>(new Getter<byte[]>() {
		@Override
		public byte[] get() {
			return new byte[CopyBufferSize];
		}
	});

	/**
	 * Copies one stream into the other =&gt; Copy <b>inputStream</b> to <b>outputStream</b>
	 *
	 * @param inputStream      The input stream to read from.
	 * @param inputStreamSize  The stream input size
	 * @param outputStream     The output stream to write to.
	 * @param progressNotifier To notify for updates.
	 *
	 * @throws IOException
	 */
	public static void copy(InputStream inputStream, long inputStreamSize, OutputStream outputStream, ProgressNotifier progressNotifier)
		throws IOException {
		byte[] buffer = buffers.get();
		int length;
		int readSize = 0;
		try {
			if (progressNotifier != null)
				progressNotifier.onCopyStarted();
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
				if (progressNotifier != null && inputStreamSize > 0) {
					readSize += length;
					progressNotifier.onProgressPercentage((float) readSize / inputStreamSize);
				}
			}
		} catch (Exception e) {
			if (progressNotifier != null)
				progressNotifier.onCopyException(e);
		} finally {
			if (progressNotifier != null)
				progressNotifier.onCopyEnded();
		}
		outputStream.flush();
	}

	/**
	 * Reads the given input stream fully and return the byte[] of this stream.
	 *
	 * @param inputStream the input stream to read
	 *
	 * @return the stream as byte[]
	 *
	 * @throws IOException
	 */
	public static byte[] readFully(InputStream inputStream)
		throws IOException {
		ByteArrayOutputStream bos = readStreamFully(inputStream);
		return bos.toByteArray();
	}

	private static ByteArrayOutputStream readStreamFully(InputStream inputStream)
		throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy(inputStream, bos);
		inputStream.close();
		return bos;
	}

	/**
	 * Reads the given input stream fully and return the byte[] of this stream.
	 *
	 * @param inputStream the input stream to read
	 *
	 * @return the stream as string
	 *
	 * @throws IOException
	 */
	public static String readFullyAsString(InputStream inputStream)
		throws IOException {
		return readFullyAsString(inputStream, "utf-8");
	}

	public static String readFullyAsString(InputStream inputStream, Charsets charset)
		throws IOException {
		ByteArrayOutputStream bos = readStreamFully(inputStream);
		return bos.toString(charset.encoding);
	}

	public static String readFullyAsString(InputStream inputStream, String charset)
		throws IOException {
		ByteArrayOutputStream bos = readStreamFully(inputStream);
		return bos.toString(charset);
	}

	/**
	 * Converts any given numeric value which is not floating, to byte array. <br>
	 * <br>
	 * {@link Byte}<br>
	 * {@link Short}<br>
	 * {@link Integer}<br>
	 * {@link Long}<br>
	 *
	 * @param _value The value to parse
	 *
	 * @return A byte array of the supplied value.
	 */
	public static byte[] toByteArray(Number _value) {
		int length;
		long value;
		if (_value instanceof Byte) {
			length = Byte.SIZE >> 3;
			value = (Byte) _value;
		} else if (_value instanceof Short) {
			length = Short.SIZE >> 3;
			value = (Short) _value;
		} else if (_value instanceof Integer) {
			length = Integer.SIZE >> 3;
			value = (Integer) _value;
		} else if (_value instanceof Long) {
			length = Long.SIZE >> 3;
			value = (Long) _value;
		} else {
			throw new IllegalArgumentException("Parameter must be one of the following types:\n Byte, Short, Integer, Long");
		}
		byte[] byteArray = new byte[length];
		for (int i = 0; i < length; i++) {
			byteArray[i] = (byte) ((value >> (8 * (length - i - 1))) & 0xff);
		}
		return byteArray;
	}

	/**
	 * Converts a byte array, to a numeric value. <br>
	 * <br>
	 * {@link Byte}<br>
	 * {@link Short}<br>
	 * {@link Integer}<br>
	 * {@link Long}<br>
	 *
	 * @param byteArray The byte array with the value to parse.
	 *
	 * @return The value which rests within the supplied byte array
	 */
	public static long fromByteArray(byte[] byteArray) {
		return fromByteArray(byteArray, 0, byteArray.length);
	}

	public static long fromByteArray(byte[] byteArray, int startFrom, int length) {
		long value = 0;

		if (length > 8) {
			throw new IllegalArgumentException("Array parameter length==" + byteArray.length + ". MUST be length <= 8");
		}

		for (int i = 0; i < length; i++) {
			value |= ((0xffL & byteArray[startFrom + i]) << (8 * (length - i - 1)));
		}
		return value;
	}

	public static void copy(File origin, OutputStream outputStream)
		throws IOException {
		if (!origin.exists())
			throw new IOException("Could find origin file: " + origin.getAbsolutePath());

		if (origin.isDirectory())
			throw new IOException("Cannot copy directory to stream: " + origin.getAbsolutePath());

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(origin);
			copy(fis, outputStream);
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	//	add a progress listener
	public static void copy(InputStream inputStream, File target)
		throws IOException {
		if (target.exists()) {
			if (target.isDirectory())
				throw new IOException("Cannot copy stream into directory: " + target.getAbsolutePath());

			if (!target.delete())
				throw new IOException("Could not delete older target file: " + target.getAbsolutePath());
		}

		FileTools.createNewFile(target);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
			copy(inputStream, fos);
		} finally {
			if (fos != null)
				fos.close();
		}
	}

	public static void copy(InputStream inputStream, OutputStream outputStream)
		throws IOException {
		copy(inputStream, outputStream, false);
	}

	public static void copy(InputStream inputStream, OutputStream outputStream, boolean flush)
		throws IOException {
		byte[] buffer = buffers.get();
		int length;
		int i = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
			if (flush && i % 10 == 0)
				outputStream.flush();

			i++;
		}
		outputStream.flush();
	}
}
