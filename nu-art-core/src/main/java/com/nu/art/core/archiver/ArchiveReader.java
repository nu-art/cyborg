package com.nu.art.core.archiver;

import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.StreamTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.nu.art.core.archiver.ArchiveReader.OverridePolicy.DoNotOverride;
import static com.nu.art.core.archiver.ArchiveReader.OverridePolicy.ForceDelete;

/**
 * Created by TacB0sS on 26/04/2018.
 */

public class ArchiveReader {

	public enum OverridePolicy {
		DoNotOverride,
		ForceDelete,
		Merge
	}

	private ZipInputStream jis;
	private InputStream fis;
	private OverridePolicy overridePolicy = DoNotOverride;
	private File inputFile;
	private File outputFolder;
	private boolean doVerify;

	public final ArchiveReader overridePolicy(OverridePolicy overridePolicy) {
		this.overridePolicy = overridePolicy;
		return this;
	}

	public final ArchiveReader doVerify(boolean doVerify) {
		this.doVerify = doVerify;
		return this;
	}

	public final ArchiveReader setOutputFolder(String outputFolder)
		throws IOException {
		return setOutputFolder(new File(outputFolder));
	}

	public final ArchiveReader setOutputFolder(File outputFolder)
		throws IOException {
		this.outputFolder = outputFolder;
		return this;
	}

	public ArchiveReader open(String inputFile)
		throws IOException {
		return open(new File(inputFile));
	}

	public ArchiveReader open(File inputFile)
		throws IOException {
		FileTools.createNewFile(inputFile);
		this.inputFile = inputFile;

		return open(new FileInputStream(inputFile));
	}

	public ArchiveReader open(InputStream inputStream)
		throws IOException {
		if (fis != null)
			throw new IOException(inputFile == null ? "Has open input stream" : "Already have open file: " + inputFile.getAbsolutePath());

		this.fis = inputStream;
		try {
			jis = new JarInputStream(fis, doVerify);
		} catch (IOException e) {
			dispose();
			throw e;
		}
		return this;
	}

	public final void extract()
		throws IOException {
		try {
			if (outputFolder.isFile())
				throw new IOException("output path MUST be a folder: " + outputFolder.getAbsolutePath());

			if (outputFolder.exists()) {
				if (overridePolicy == DoNotOverride)
					throw new IOException("output folder already exists: " + outputFolder.getAbsolutePath());

				if (overridePolicy == ForceDelete)
					FileTools.delete(outputFolder);
			}

			FileTools.mkDir(outputFolder);

			readFilesFromArchive();
		} finally {
			dispose();
		}
	}

	private void readFilesFromArchive()
		throws IOException {
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				continue;
			}
			writeEntryToFile(entry);
		}
	}

	private void writeEntryToFile(ZipEntry entry)
		throws IOException {
		FileOutputStream fos = null;
		File file = new File(outputFolder, entry.getName());

		FileTools.delete(file);
		FileTools.mkDir(file.getParentFile());
		FileTools.createNewFile(file);

		try {
			fos = new FileOutputStream(file);
			StreamTools.copy(jis, fos);
		} finally {
			if (fos != null)
				fos.close();

			if (entry.getTime() != -1)
				file.setLastModified(entry.getTime());
		}
	}

	public final void dispose() {
		if (fis != null)
			try {
				fis.close();
			} catch (IOException ignore) {
			}

		if (jis != null)
			try {
				jis.close();
			} catch (IOException ignore) {
			}
	}
}
