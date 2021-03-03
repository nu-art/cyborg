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
import com.nu.art.core.file.FileType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import sun.awt.OSInfo;
import sun.awt.OSInfo.OSType;

public class FileTools {

	public static boolean CaseSensitiveOS;

	static {
		try {
			CaseSensitiveOS = OSInfo.getOSType() != OSType.MACOSX;
		} catch (Throwable t) {
			CaseSensitiveOS = true;
		}
	}

	public static void createNewFile(String file)
		throws IOException {
		createNewFile(new File(file));
	}

	public static void createNewFile(File file)
		throws IOException {
		if (file.exists()) {
			return;
		}

		/*
		 * If the parent folder does not exists, create it.
		 */
		if (!file.getParentFile().exists()) {
			mkDir(file.getParentFile());
		}

		/*
		 * If the file itself does not exists, create it.
		 */
		if (!file.createNewFile()) {
			throw new IOException("Failed to create file: '" + file.getAbsolutePath() + "'");
		}
	}

	private static void copyDirTo(File sourceFolder, File destinationFolder)
		throws IOException {

		File[] files = sourceFolder.listFiles();
		if (files == null) {
			return;
		}

		File df = new File(destinationFolder, sourceFolder.getName());

		if (!df.exists()) {
			FileTools.mkDir(df);
		}

		for (File file : files) {
			if (file.isDirectory()) {
				FileTools.copyDirTo(file, destinationFolder);
			} else {
				FileTools.copyFileTo(file, df);
			}
		}
	}

	public static void copyFile(File source, File destinationFolder)
		throws IOException {
		if (!destinationFolder.isDirectory()) {
			throw new IOException("Destination MUST be a folder");
		}

		// /a/b/c.txt => /a/d/e
		if (destinationFolder.getAbsolutePath().startsWith(source.getAbsolutePath())) {
			throw new IOException("Recursive copying is not allowed");
		}

		if (source.isDirectory()) {
			FileTools.copyDirTo(source, destinationFolder);
		} else {
			FileTools.copyFileTo(source, destinationFolder);
		}
	}

	public static void copyFile(String source, String destinationFolder)
		throws IOException {
		FileTools.copyFile(new File(source), new File(destinationFolder));
	}

	public static void copyFiles(File[] source, File destinationFolder)
		throws IOException {
		if (!destinationFolder.isDirectory()) {
			throw new IOException("Destination MUST be a folder");
		}
		for (File file : source) {
			FileTools.copyFile(file, destinationFolder);
		}
	}

	public static void move(File origin, File target)
		throws IOException {
		if (!origin.renameTo(target))
			throw new IOException("Error moving file: " + origin.getAbsolutePath() + " -> " + target.getAbsolutePath());
	}

	private static void copyFileTo(File source, File destinationFolder)
		throws IOException {
		File newFile = new File(destinationFolder, source.getName());
		createNewFile(newFile);

		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(source);
			outputStream = new FileOutputStream(newFile);
			StreamTools.copy(inputStream, outputStream);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException ignore) {
			}

			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException ignore) {
			}
		}

		newFile.setLastModified(source.lastModified());
	}

	public static void delete(String toDelete)
		throws IOException {
		delete(new File(toDelete));
	}

	public static void delete(File toDelete)
		throws IOException {
		if (!toDelete.exists())
			return;

		if (toDelete.isDirectory()) {
			FileTools.deleteDirectory(toDelete);
		} else {
			FileTools.deleteFile(toDelete);
		}
	}

	private static void deleteDirectory(File toDelete)
		throws IOException {
		if (isParentOfRunningFolder(toDelete))
			throw new IllegalStateException("MAJOR ERROR!!! Cannot delete running folder!!!");

		File[] files = toDelete.listFiles();

		if (files != null)
			for (File file : files) {
				delete(file);
			}

		if (!toDelete.delete())
			throw new IOException("Failed to delete directory: " + toDelete.getAbsolutePath());
	}

	public static boolean isParentOfRunningFolder(File toDelete) {
		String runningDir = getRunningDirectoryPath();
		String toDeleteAbsolutePath = toDelete.getAbsolutePath();
		if (!CaseSensitiveOS) {
			runningDir = runningDir.toLowerCase();
			toDeleteAbsolutePath = toDeleteAbsolutePath.toLowerCase();
		}

		return runningDir.length() > 2 && runningDir.startsWith(toDeleteAbsolutePath);
	}

	private static void deleteFile(File toDelete)
		throws IOException {
		if (!toDelete.delete()) {
			throw new IOException("Failed to delete file: " + toDelete.getAbsolutePath());
		}
	}

	public static String getParentFolderPath(String file) {
		file = file.replace("\\", "/");
		String path;
		if (file.endsWith("/")) {
			path = file.substring(0, file.lastIndexOf("/", file.length() - 2));
		} else {
			path = file.substring(0, file.lastIndexOf("/"));
		}
		return path;
	}

	public static String getRunningDirectoryPath() {
		String appPath = new File(".").getAbsolutePath();
		return appPath.substring(0, appPath.length() - 1);
	}

	public static void mkDir(String destination)
		throws IOException {
		mkDir(new File(destination));
	}

	public static void mkDir(File destination)
		throws IOException {
		Vector<File> toMake = new Vector<>();
		while (destination != null && !destination.exists()) {
			toMake.insertElementAt(destination, 0);
			destination = destination.getParentFile();
		}
		for (File dir : toMake) {
			if (!dir.mkdir()) {
				throw new IOException("Error while creating folder: '" + dir.getAbsolutePath() + "'");
			}
		}
	}

	public static byte[] readFully(String pathToFile)
		throws IOException {
		return readFully(new File(pathToFile));
	}

	public static byte[] readFully(File file)
		throws IOException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return StreamTools.readFully(inputStream);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	public static String readFullyAsString(File file)
		throws IOException {
		return readFullyAsString(file, Charsets.UTF_8);
	}

	public static String readFullyAsString(File file, Charsets charset)
		throws IOException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return StreamTools.readFullyAsString(inputStream, charset);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	public static File[] searchForFiles(File dir, FileType jarFile) {
		return FileTools.searchForFiles(dir, jarFile, 0);
	}

	public static File[] searchForFiles(File dir, FileType fileType, int depth, File... excludes) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Supplied file MUST be a directory. File: " + dir.getAbsolutePath());
		}
		Vector<File> matchingFiles = new Vector<>();
		FileTools.searchForFiles(matchingFiles, dir, fileType, depth, excludes);
		return matchingFiles.toArray(new File[matchingFiles.size()]);
	}

	private static void searchForFiles(Vector<File> matchingFiles, File dir, FileType fileType, int depth, File... excludes) {
		for (File file : excludes) {
			if (file.equals(dir)) {
				return;
			}
		}

		File[] files = dir.listFiles();
		if (!(depth > 0 || depth == -1) || files == null) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				FileTools.searchForFiles(matchingFiles, file, fileType, depth == -1 ? depth : depth - 1, excludes);
				continue;
			}
			if (fileType.checkType(file)) {
				matchingFiles.add(file);
			}
		}
	}

	public static File[] searchForFiles(File dir, String regex, int depth, File... excludes) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Supplied file MUST be a directory. File: " + dir.getAbsolutePath());
		}
		Vector<File> matchingFiles = new Vector<>();
		FileTools.searchForFiles(matchingFiles, dir, regex, depth, excludes);
		return matchingFiles.toArray(new File[matchingFiles.size()]);
	}

	private static void searchForFiles(Vector<File> matchingFiles, File dir, String regex, int depth, File... excludes) {
		for (File file : excludes) {
			if (file.equals(dir)) {
				return;
			}
		}

		File[] files = dir.listFiles();
		if (!(depth > 0 || depth == -1)) {
			return;
		}

		if (files == null || files.length == 0) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				FileTools.searchForFiles(matchingFiles, file, regex, depth == -1 ? depth : depth - 1, excludes);
				continue;
			}
			if (file.getName().matches(regex)) {
				matchingFiles.add(file);
			}
		}
	}

	public static void writeToFile(String text, File file, Charsets encoding)
		throws IOException {
		FileOutputStream fos = null;
		BufferedWriter out = null;
		createNewFile(file);
		try {
			fos = new FileOutputStream(file);
			out = new BufferedWriter(new OutputStreamWriter(fos, encoding.encoding));
			out.write(text);
			out.flush();
		} finally {
			if (out != null)
				out.close();

			if (fos != null) {
				fos.close();
			}
		}
	}

	public static File[] searchForFiles(File[] sourceFolders, com.nu.art.core.interfaces.Condition<File> matchCondition) {
		ArrayList<File> sourceFiles = new ArrayList<>();
		for (File folder : sourceFolders) {
			searchForFile(folder, matchCondition, sourceFiles);
		}
		return sourceFiles.toArray(new File[sourceFiles.size()]);
	}

	private static void searchForFile(File folder, com.nu.art.core.interfaces.Condition<File> matchCondition, ArrayList<File> sourceFiles) {
		File[] listFiles = folder.listFiles();
		if (listFiles == null)
			return;

		for (File file : listFiles) {
			if (!file.isDirectory()) {
				if (matchCondition.checkCondition(file)) {
					sourceFiles.add(file);
				}
				continue;
			}

			searchForFile(file, matchCondition, sourceFiles);
		}
	}

	public static void archive(File output, File... filesToZip)
		throws IOException {
		byte[] buffer = new byte[1024];

		FileOutputStream fos = null;
		FileInputStream in = null;
		try {
			fos = new FileOutputStream(output);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (File file : filesToZip) {
				ZipEntry ze = new ZipEntry(file.getName());
				ze.setTime(file.lastModified());
				ze.setSize(file.length());
				zos.putNextEntry(ze);
				in = new FileInputStream(file);
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
				in = null;
				zos.closeEntry();
			}
			zos.close();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void renameFile(File origin, File target)
		throws IOException {
		if (target.exists())
			throw new IOException("Target already exists: " + target.getAbsolutePath());

		File parentFile = target.getParentFile();
		if (!parentFile.exists())
			if (!parentFile.mkdirs())
				throw new IOException("Unable to create parent folder: " + parentFile.getAbsolutePath());

		if (!origin.renameTo(target))
			throw new IOException("Unable to rename file from: " + origin.getName() + " => " + target.getName());
	}
}
