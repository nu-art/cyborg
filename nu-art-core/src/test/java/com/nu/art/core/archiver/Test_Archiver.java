/*
 * cyborg-core is an extendable  module based framework for Android.
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

package com.nu.art.core.archiver;

import com.nu.art.core.archiver.ArchiveReader.OverridePolicy;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.file.Charsets;
import com.nu.art.core.tools.FileTools;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.nu.art.core.archiver.ArchiveReader.OverridePolicy.DoNotOverride;
import static com.nu.art.core.archiver.ArchiveReader.OverridePolicy.ForceDelete;
import static com.nu.art.core.archiver.ArchiveReader.OverridePolicy.Merge;
import static com.nu.art.core.tools.FileTools.getRunningDirectoryPath;
import static com.nu.art.core.tools.FileTools.isParentOfRunningFolder;

/**
 * Created by TacB0sS on 05/04/2018.
 */

public class Test_Archiver {

	private static boolean setUpIsDone = false;
	private static File[] testFiles = new File[10];
	private static File resourcesFolder = new File("build/test/archiver");

	@Before
	public void setUp()
		throws IOException {
		if (setUpIsDone) {
			return;
		}

		for (int i = 1; i < 1 + testFiles.length; i++) {
			testFiles[i - 1] = new File(resourcesFolder + "/data", "temp-file-" + i + ".txt");
			FileTools.writeToFile("test file " + i, testFiles[i - 1], Charsets.UTF_8);
		}

		setUpIsDone = true;
	}

	@Test
	public void test_AddFile()
		throws IOException {
		String testName = "test_CopyFilesSync";
		File zipFile = new File(resourcesFolder + "/output", testName + ".zip");
		File outputFolder = new File(resourcesFolder + "/extracted/" + testName);
		FileTools.delete(outputFolder);

		new ArchiveWriter().open(zipFile).addFile(testFiles[0]).close();
		new ArchiveReader().open(zipFile).setOutputFolder(outputFolder).extract();
	}

	@Test
	public void test_AddFileIntoFolder()
		throws IOException {
		String testName = "test_AddFileIntoFolder";
		File zipFile = new File(resourcesFolder + "/output", testName + ".zip");
		File outputFolder = new File(resourcesFolder + "/extracted/" + testName);
		FileTools.delete(outputFolder);

		new ArchiveWriter().open(zipFile).addFile("here", testFiles[0]).close();
		new ArchiveReader().open(zipFile).setOutputFolder(outputFolder).overridePolicy(ForceDelete).extract();
	}

	@Test
	public void test_AddFilesIntoDifferentFolder()
		throws IOException {
		String testName = "test_AddFilesIntoDifferentFolder";
		File zipFile = new File(resourcesFolder + "/output", testName + ".zip");
		File outputFolder = new File(resourcesFolder + "/extracted/" + testName);
		FileTools.delete(outputFolder);

		new ArchiveWriter().open(zipFile).addFile("here", testFiles[0]).addFile("here1", testFiles[2]).close();
		new ArchiveReader().open(zipFile).setOutputFolder(outputFolder).extract();
	}

	@Test
	public void test_AddFilesIntoFolder()
		throws IOException {
		String testName = "test_AddFilesIntoFolder";
		File outputFile = new File(resourcesFolder + "/output", testName + ".zip");
		File outputFolder = new File(resourcesFolder + "/extracted/" + testName);
		FileTools.delete(outputFolder);

		new ArchiveWriter().open(outputFile).addFiles("here", testFiles[0], testFiles[2]).close();
		new ArchiveReader().open(outputFile).setOutputFolder(outputFolder).extract();
	}

	@Test
	public void test_AddMultipleFilesIntoDifferentFolder()
		throws IOException {
		String testName = "test_AddMultipleFilesIntoDifferentFolder";
		File outputFile = new File(resourcesFolder + "/output", testName + ".zip");
		File outputFolder = new File(resourcesFolder + "/extracted/" + testName);
		FileTools.delete(outputFolder);

		new ArchiveWriter().open(outputFile).addFiles("here", testFiles[0], testFiles[2]).addFiles("here1", testFiles[1], testFiles[3]).close();
		new ArchiveReader().open(outputFile).setOutputFolder(outputFolder).overridePolicy(DoNotOverride).extract();
		new ArchiveReader().open(outputFile).setOutputFolder(outputFolder).overridePolicy(ForceDelete).extract();
		new ArchiveReader().open(outputFile).setOutputFolder(outputFolder).overridePolicy(Merge).extract();
	}

	@Test
	public void test_ProcessorGenericType() {
		File runningFolder = new File(getRunningDirectoryPath());
		if (!isParentOfRunningFolder(runningFolder)) {
			throw new BadImplementationException("Should be a parent running folder");
		}
		if (!isParentOfRunningFolder(runningFolder.getParentFile())) {
			throw new BadImplementationException("Should be a parent running folder");
		}
		if (isParentOfRunningFolder(new File(runningFolder.getParentFile(), "stam"))) {
			throw new BadImplementationException("Should NOT be a parent running folder");
		}
		if (isParentOfRunningFolder(new File(runningFolder, "stam"))) {
			throw new BadImplementationException("Should NOT be a parent running folder");
		}
	}
}
