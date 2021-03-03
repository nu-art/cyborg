package com.nu.art.core.file;

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.FileTools;
import com.nu.art.core.utils.RunnableQueue;
import com.nu.art.core.utils.SynchronizedObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class FileCopy {

	public interface FileCopyListener {

		void onSuccess();

		void onError(Throwable t);
	}

	public FileCopy setThreadsCount(int threadCount, FileCopyListener completionListener) {
		if (threadCount <= 0)
			return this;

		todo = new RunnableQueue();
		todo.createThreads("copy", threadCount);
		this.completionListener = completionListener;
		return this;
	}

	public class FileProgress {

		public File sourceFile;
		public File targetFile;
		private long progress;

		public double getProgress() {
			return 1f * progress / sourceFile.length();
		}
	}

	private Throwable error;
	private int bufferSize = 1024;
	private final SynchronizedObject<byte[]> buffers = new SynchronizedObject<>(new Getter<byte[]>() {
		@Override
		public byte[] get() {
			return new byte[bufferSize];
		}
	});
	private final SynchronizedObject<FileProgress> progress = new SynchronizedObject<>(new Getter<FileProgress>() {
		@Override
		public FileProgress get() {
			return new FileProgress();
		}
	});

	private RunnableQueue todo;
	private AtomicInteger inProgress = new AtomicInteger(0);
	private File targetFolder;
	private FileCopyListener completionListener;

	public FileCopy setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}

	public FileCopy setTargetFolder(File targetFolder) {
		this.targetFolder = targetFolder;
		if (targetFolder.isFile())
			throw new BadImplementationException("Target folder is actually a file: " + targetFolder.getAbsolutePath());

		return this;
	}

	public final FileCopy copy(final File file)
		throws IOException {
		copy(file, "");
		return this;
	}

	public final FileCopy copy(final File file, String relativePath)
		throws IOException {
		if (!file.exists())
			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

		_copy(file, new File(this.targetFolder, relativePath));
		return this;
	}

	private void _copy(final File source, final File targetFolder) {
		inProgress.incrementAndGet();
		if (!targetFolder.exists()) {
			try {
				FileTools.mkDir(targetFolder);
			} catch (IOException e) {
				onError(e);
				return;
			}
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				log("Copy: " + source.getName() + "=> To: " + targetFolder.getName());
				try {

					if (source.isDirectory())
						_copyFolder(source, targetFolder);
					else
						_copyFile(source, new File(targetFolder, source.getName()));
				} catch (IOException e) {
					onError(e);
				} finally {
					int inProcess = inProgress.decrementAndGet();
					if (inProcess == 0)
						if (completionListener != null)
							completionListener.onSuccess();
				}
			}
		};
		if (todo != null)
			todo.addItem(runnable);
		else
			runnable.run();
	}

	private void _copyFile(File sourceFile, File targetFile)
		throws IOException {
		copyFileImpl(sourceFile, targetFile);
	}

	private void _copyFolder(File sourceFolder, File targetFolder) {
		File[] files = sourceFolder.listFiles();
		if (files == null)
			return;

		for (File sourceFile : files) {
			_copy(sourceFile, new File(targetFolder, sourceFolder.getName()));
		}
	}

	private void copyFileImpl(File sourceFile, File targetFile)
		throws IOException {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileProgress progress = this.progress.get();
		progress.sourceFile = sourceFile;
		progress.targetFile = targetFile;
		progress.progress = 0;

		try {
			FileTools.createNewFile(targetFile);
			inputStream = new FileInputStream(sourceFile);
			outputStream = new FileOutputStream(targetFile);

			byte[] buffer = buffers.get();
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				if (this.error != null)
					break;

				outputStream.write(buffer, 0, length);
				progress.progress += length;
			}
			outputStream.flush();

			targetFile.setLastModified(sourceFile.lastModified());
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

		if (error == null)
			return;

		todo.clear();
		try {
			FileTools.delete(targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onError(Throwable e) {
		todo.clear();
		e.printStackTrace();
		this.error = e;
	}

	private final static SimpleDateFormat DefaultTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	private SynchronizedObject<StringBuffer> logBuffers = new SynchronizedObject<>(new Getter<StringBuffer>() {
		@Override
		public StringBuffer get() {
			return new StringBuffer();
		}
	});

	private final Date date = new Date();

	private void log(String message) {
		date.setTime(System.currentTimeMillis());

		StringBuffer buffer = logBuffers.get();
		buffer.setLength(0);
		buffer.append(DefaultTimeFormat.format(date)).append(" ");
		buffer.append(Thread.currentThread()).append("/");
		buffer.append("File Copy").append(": ");

		if (message != null)
			buffer.append(message).append("\n");

		System.out.print(buffer.toString());
	}

	private void logError(String message) {
		System.err.println(message);
	}
}
