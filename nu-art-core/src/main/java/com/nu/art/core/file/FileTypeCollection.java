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

import java.io.Serializable;
import java.util.Collections;
import java.util.Vector;

public class FileTypeCollection
	implements Serializable {

	private static final long serialVersionUID = 2101543520036915189L;

	private static final Vector<FileTypeCollection> FileTypesCollections = new Vector<>();

	public static Vector<FileTypeCollection> getAllCollections() {
		return FileTypeCollection.FileTypesCollections;
	}

	public static void setAllCollections(Vector<FileTypeCollection> suppliedTypes) {
		FileTypeCollection.FileTypesCollections.addAll(suppliedTypes);
	}

	private Vector<FileType> fileTypes = new Vector<>();

	public FileTypeCollection(FileType... fileTypes) {
		Collections.addAll(this.fileTypes, fileTypes);
		FileTypeCollection.FileTypesCollections.add(this);
	}

	public void addFileType(FileType fileType) {
		if (fileTypes.contains(fileType)) {
			fileTypes.remove(fileType);
		}
		fileTypes.add(fileType);
	}

	public void removeFileType(FileType fileType) {
		fileTypes.remove(fileType);
	}
}
