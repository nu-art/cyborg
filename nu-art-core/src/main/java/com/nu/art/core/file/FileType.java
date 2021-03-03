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

package com.nu.art.core.file;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class FileType
	implements Serializable {

	private static final long serialVersionUID = -32181400402056153L;

	public static final FileType Directory = new FileType(File.separator, "Directory");

	public static final FileType Zip_File = new FileType("zip", "A Zip - Archive file");

	public static final FileType Rar_File = new FileType("rar", "A Rar - Archive file");

	public static final FileType Jar_File = new FileType("jar", "A Java classes library - Archive file");

	public static final FileType Jad_File = new FileType("jad", "A Java application descriptor file");

	public static final FileType Java_File = new FileType("java", "A Java source code file");

	public static final FileType C_File = new FileType("c", "A C source code file");

	public static final FileType H_File = new FileType("h", "A C source header file");

	public static final FileType Cpp_File = new FileType("cpp", "A C++ source code file");

	public static final FileType Class_File = new FileType("class", "A Java compiled class file");

	public static final FileType Log_File = new FileType("log", "A Log file");

	public static final FileType Data_File = new FileType("dat", "A Data file");

	public static final FileType Png_File = new FileType("png", "A Png image file");

	public static final FileType Jpg_File = new FileType("jpg", "A Jpg image file");

	public static final FileType Configuration_File = new FileType("cfg", "A Configuration file");

	public static final FileType DataChunk_File = new FileType("dcf", "Data chunk file");

	public static final FileType XML_File = new FileType("xml", "XML file");

	public static final FileType Temp_File = new FileType("tmp", "Temp file");

	public static final FileType Wav_File = new FileType("wav", "An audio Wav file");

	public static final FileType AAC_File = new FileType("aac", "An audio AAC file");

	public static final FileType AMR_File = new FileType("amr", "An audio AMR file");

	public static final FileType Mp3_File = new FileType("mp3", "An audio Mp3 file");

	public static final FileType CS_Script = new FileType("scs", "C# script file");

	public static final FileType AllFiles = new FileType("*", "All files") {

		private static final long serialVersionUID = 6455746226238041232L;

		@Override
		public boolean checkType(File file) {
			return true;
		}
	};

	public static final FileTypeCollection Images = new FileTypeCollection(FileType.Jpg_File, FileType.Png_File);

	public static final FileTypeCollection Audio = new FileTypeCollection(FileType.Wav_File, FileType.Mp3_File);

	private static HashMap<String, FileType> suffixMap;

	public static final FileType getFileType(File file)
		throws FileTypeNotSupportedException {
		String name = file.getName();
		int from = name.lastIndexOf('.');
		String suffix = name.substring(from + 1);
		suffix = suffix.toLowerCase();
		FileType type = _getFileTypeFromSuffix(suffix);
		if (type == null) {
			throw new FileTypeNotSupportedException("File type not found for file: " + file.getName() + ", Suffix extracted: " + suffix);
		}
		return type;
	}

	public static final FileType getFileTypeFromSuffix(String suffix)
		throws FileTypeNotSupportedException {
		suffix = suffix.toLowerCase();
		FileType fileType = suffixMap.get(suffix);
		if (fileType == null) {
			throw new FileTypeNotSupportedException("File type not found for suffix: " + suffix);
		}
		return fileType;
	}

	private static final FileType _getFileTypeFromSuffix(String suffix) {
		suffix = suffix.toLowerCase();
		return suffixMap.get(suffix);
	}

	private String suffix;

	private String fileTypeDescription;

	public FileType(String suffix, String fileTypeDescription) {
		this.suffix = suffix;
		this.fileTypeDescription = fileTypeDescription;
		addFileType(suffix, this);
	}

	private static void addFileType(String suffix, FileType fileType) {
		if (suffixMap == null) {
			suffixMap = new HashMap<>();
		}
		suffixMap.put(suffix, fileType);
	}

	public String attachSuffix(String name) {
		if (name.endsWith(getSuffix())) {
			return name;
		}
		return name + "." + getSuffix();
	}

	public boolean checkType(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(suffix.toLowerCase());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileType) {
			FileType toCompare = (FileType) obj;
			return toCompare.getSuffix().equals(getSuffix());
		}
		return super.equals(obj);
	}

	public String getDescription() {
		return fileTypeDescription;
	}

	public String getSuffix() {
		return suffix;
	}

	public String isolateName(String name) {
		if (name.toLowerCase().endsWith("." + suffix)) {
			return name.substring(0, name.length() - ("." + suffix).length());
		}
		return name;
	}

	@Override
	public String toString() {
		return "*." + suffix;
	}

	public void validateFileName(String newFileName)
		throws IOException {
		File file = new File(newFileName);
		if (file.exists()) {
			throw new IOException("File already exists!!");
		}

		if (!file.createNewFile()) {
			throw new IOException("Could not create new file!!");
		}
	}
}
