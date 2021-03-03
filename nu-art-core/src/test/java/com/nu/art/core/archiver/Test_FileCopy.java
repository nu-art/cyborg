package com.nu.art.core.archiver;

import com.nu.art.core.file.Charsets;
import com.nu.art.core.file.FileCopy;
import com.nu.art.core.file.FileCopy.FileCopyListener;
import com.nu.art.core.tools.FileTools;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class Test_FileCopy {

	private static boolean setUpIsDone = false;
	private static File[] testFiles = new File[10];
	private static File[] testSubFiles = new File[10];
	private static File path = new File("build/test/file-copy");
	static String fileContent;

	@Before
	public void setUp()
		throws IOException {
		if (setUpIsDone) {
			return;
		}

		boolean createNew = false;
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < 10000000; i++) {
			stringBuilder.append("Pah Zevel-");
		}

		fileContent = stringBuilder.toString();
		for (int i = 1; i < 1 + testFiles.length; i++) {
			testFiles[i - 1] = new File(path + "/data", "temp-file-" + i + ".txt");
			if (!testFiles[i - 1].exists() || createNew)
				FileTools.writeToFile(fileContent, testFiles[i - 1], Charsets.UTF_8);
		}

		for (int i = 1; i < 1 + testSubFiles.length; i++) {
			testSubFiles[i - 1] = new File(path + "/data/sub", "temp-sub-file-" + i + ".txt");
			if (!testSubFiles[i - 1].exists() || createNew)
				FileTools.writeToFile(fileContent, testSubFiles[i - 1], Charsets.UTF_8);
		}

		for (int i = 1; i < 20; i++) {
			File file = new File(path + "/data/sub/foo", "temp-sub-foo-file-" + i + ".txt");
			if (!file.exists() || createNew)
				FileTools.writeToFile(fileContent, file, Charsets.UTF_8);
		}

		for (int i = 1; i < 20; i++) {
			File file = new File(path + "/data/sub/bar", "temp-sub-bar-file-" + i + ".txt");
			if (!file.exists() || createNew)
				FileTools.writeToFile(fileContent, file, Charsets.UTF_8);
		}

		setUpIsDone = true;
	}

	@Test
	public void test_CopyFilesSync()
		throws IOException {
		_copyFiles(0);
	}

	@Test
	public void test_CopyFilesAsync_8()
		throws IOException {
		_copyFiles(8);
	}

	@Test
	public void test_CopyFilesAsync_20()
		throws IOException {
		_copyFiles(40);
	}

	private void _copyFiles(int threadCount)
		throws IOException {

		final File targetFolder = new File(path, "target");
		FileTools.delete(targetFolder);
		FileTools.mkDir(targetFolder);
		FileCopyListener listener = new FileCopyListener() {
			@Override
			public void onSuccess() {
				synchronized (targetFolder) {
					targetFolder.notify();
				}
			}

			@Override
			public void onError(Throwable t) {
				synchronized (targetFolder) {
					targetFolder.notify();
				}
			}
		};
		new FileCopy().setTargetFolder(targetFolder)
		              .setThreadsCount(threadCount, listener)
		              .copy(testFiles[0])
		              .copy(testFiles[1])
		              .copy(new File(path, "/data/sub/foo"), "pah/zevel")
		              .copy(testFiles[4], "pah/zevel");

		if (threadCount <= 0)
			return;

		synchronized (targetFolder) {
			try {
				targetFolder.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
